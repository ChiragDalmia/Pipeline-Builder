package ca.communitycentre.workshops.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.FieldValidationException;
import ca.communitycentre.workshops.exception.IntegrityViolationTranslator;
import ca.communitycentre.workshops.projection.WorkshopOverview;

@DataJpaTest
class InstructorRepositoryTest {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAndReadsBackInstructor() {
        Instructor saved = instructorRepository.saveAndFlush(
                new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        entityManager.clear();

        Instructor found = instructorRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getFullName()).isEqualTo("Ruth Adeyemi");
        assertThat(found.getEmail()).isEqualTo("ruth@example.org");
        assertThat(found.getSpeciality()).isEqualTo("Sewing");
    }

    @Test
    void updateChangesExistingRowInsteadOfInsertingNewOne() {
        Instructor saved = instructorRepository.saveAndFlush(
                new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        Long id = saved.getId();
        entityManager.clear();

        Instructor loaded = instructorRepository.findById(id).orElseThrow();
        loaded.setSpeciality("Sewing and Mending");
        loaded.setFullName("Ruth A. Adeyemi");
        instructorRepository.flush();
        entityManager.clear();

        assertThat(instructorRepository.count()).isEqualTo(1);
        Instructor reloaded = instructorRepository.findById(id).orElseThrow();
        assertThat(reloaded.getFullName()).isEqualTo("Ruth A. Adeyemi");
        assertThat(reloaded.getSpeciality()).isEqualTo("Sewing and Mending");
        assertThat(reloaded.getEmail()).isEqualTo("ruth@example.org");
    }

    @Test
    void databaseRejectsDuplicateEmail() {
        instructorRepository.saveAndFlush(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));

        assertThatThrownBy(() -> instructorRepository.saveAndFlush(
                new Instructor("Another Person", "ruth@example.org", "Pottery")))
                .isInstanceOf(DataIntegrityViolationException.class)
                .satisfies(ex -> {
                    FieldValidationException translated =
                            IntegrityViolationTranslator.translate((DataIntegrityViolationException) ex);
                    assertThat(translated.getField()).isEqualTo("email");
                });
    }

    @Test
    void emailExistenceChecksIgnoreTheRecordBeingEdited() {
        Instructor ruth = instructorRepository.saveAndFlush(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        Instructor sam = instructorRepository.saveAndFlush(new Instructor("Sam Ortiz", "sam@example.org", "Pottery"));

        assertThat(instructorRepository.existsByEmail("ruth@example.org")).isTrue();
        assertThat(instructorRepository.existsByEmail("nobody@example.org")).isFalse();
        // Ruth keeping her own email is fine; Sam taking it is not.
        assertThat(instructorRepository.existsByEmailAndIdNot("ruth@example.org", ruth.getId())).isFalse();
        assertThat(instructorRepository.existsByEmailAndIdNot("ruth@example.org", sam.getId())).isTrue();
    }

    @Test
    void findByIdReturnsEmptyForMissingInstructor() {
        assertThat(instructorRepository.findById(9_999L)).isEmpty();
    }

    @Test
    void listIsSortedByFullName() {
        instructorRepository.save(new Instructor("Zoe Grant", "zoe@example.org", "Pottery"));
        instructorRepository.save(new Instructor("Abel Kim", "abel@example.org", "Chess"));
        instructorRepository.flush();

        assertThat(instructorRepository.findAllByOrderByFullNameAsc())
                .extracting(Instructor::getFullName)
                .containsExactly("Abel Kim", "Zoe Grant");
    }

    @Test
    void inverseSideListsWorkshopsLinkedThroughForeignKey() {
        Instructor ruth = entityManager.persist(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        entityManager.persist(new Workshop("SW-1", "Hemming Trousers", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        entityManager.persist(new Workshop("SW-2", "Darning Socks", LocalDate.of(2026, 12, 8), 45, 8, ruth));
        entityManager.flush();
        entityManager.clear();

        Instructor reloaded = instructorRepository.findById(ruth.getId()).orElseThrow();

        assertThat(reloaded.getWorkshops()).extracting(Workshop::getCode).containsExactly("SW-1", "SW-2");
    }

    @Test
    void overviewInnerJoinReturnsInstructorAndWorkshopFields() {
        Instructor ruth = entityManager.persist(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        Workshop hemming = entityManager.persist(
                new Workshop("SW-1", "Hemming Trousers", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        entityManager.flush();
        entityManager.clear();

        List<WorkshopOverview> rows = instructorRepository.findWorkshopOverview();

        assertThat(rows).hasSize(1);
        WorkshopOverview row = rows.get(0);
        assertThat(row.instructorId()).isEqualTo(ruth.getId());
        assertThat(row.instructorName()).isEqualTo("Ruth Adeyemi");
        assertThat(row.instructorEmail()).isEqualTo("ruth@example.org");
        assertThat(row.speciality()).isEqualTo("Sewing");
        assertThat(row.workshopId()).isEqualTo(hemming.getId());
        assertThat(row.workshopCode()).isEqualTo("SW-1");
        assertThat(row.workshopTitle()).isEqualTo("Hemming Trousers");
        assertThat(row.scheduledDate()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(row.durationMinutes()).isEqualTo(60);
        assertThat(row.capacity()).isEqualTo(8);
    }

    @Test
    void overviewExcludesInstructorsWithoutWorkshopsAndRepeatsThoseWithSeveral() {
        Instructor ruth = entityManager.persist(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        Instructor idle = entityManager.persist(new Instructor("Ivan Idle", "ivan@example.org", "Chess"));
        Instructor sam = entityManager.persist(new Instructor("Sam Ortiz", "sam@example.org", "Pottery"));
        entityManager.persist(new Workshop("SW-2", "Darning Socks", LocalDate.of(2026, 12, 8), 45, 8, ruth));
        entityManager.persist(new Workshop("SW-1", "Hemming Trousers", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        entityManager.persist(new Workshop("PT-1", "Hand-built Bowls", LocalDate.of(2026, 12, 3), 120, 6, sam));
        entityManager.flush();
        entityManager.clear();

        List<WorkshopOverview> rows = instructorRepository.findWorkshopOverview();

        assertThat(rows).extracting(WorkshopOverview::workshopCode).containsExactly("SW-1", "PT-1", "SW-2");
        assertThat(rows).extracting(WorkshopOverview::instructorId)
                .containsOnly(ruth.getId(), sam.getId())
                .doesNotContain(idle.getId());
        assertThat(instructorRepository.count()).isEqualTo(3);
    }
}
