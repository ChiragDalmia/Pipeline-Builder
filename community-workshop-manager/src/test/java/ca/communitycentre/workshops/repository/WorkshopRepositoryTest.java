package ca.communitycentre.workshops.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.IntegrityViolationTranslator;

@DataJpaTest
class WorkshopRepositoryTest {

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Instructor ruth;
    private Instructor sam;

    @BeforeEach
    void setUp() {
        ruth = entityManager.persist(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        sam = entityManager.persist(new Instructor("Sam Ortiz", "sam@example.org", "Pottery"));
        entityManager.flush();
    }

    @Test
    void savesWorkshopWithInstructorRelationship() {
        Workshop saved = workshopRepository.saveAndFlush(
                new Workshop("SW-1", "Hemming Trousers", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        entityManager.clear();

        Workshop found = workshopRepository.findWithInstructorById(saved.getId()).orElseThrow();

        assertThat(found.getCode()).isEqualTo("SW-1");
        assertThat(found.getTitle()).isEqualTo("Hemming Trousers");
        assertThat(found.getScheduledDate()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(found.getDurationMinutes()).isEqualTo(60);
        assertThat(found.getCapacity()).isEqualTo(8);
        assertThat(found.getInstructor().getId()).isEqualTo(ruth.getId());
        assertThat(found.getInstructor().getFullName()).isEqualTo("Ruth Adeyemi");

        Object storedForeignKey = entityManager.getEntityManager()
                .createNativeQuery("SELECT instructor_id FROM workshops WHERE id = :id")
                .setParameter("id", saved.getId())
                .getSingleResult();
        assertThat(((Number) storedForeignKey).longValue()).isEqualTo(ruth.getId());
    }

    @Test
    void updateCanMoveWorkshopToAnotherInstructorWithoutNewRow() {
        Workshop saved = workshopRepository.saveAndFlush(
                new Workshop("SW-1", "Hemming Trousers", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        entityManager.clear();

        Workshop loaded = workshopRepository.findById(saved.getId()).orElseThrow();
        loaded.setTitle("Hemming and Patching");
        loaded.setCapacity(10);
        loaded.setInstructor(entityManager.find(Instructor.class, sam.getId()));
        workshopRepository.flush();
        entityManager.clear();

        assertThat(workshopRepository.count()).isEqualTo(1);
        Workshop reloaded = workshopRepository.findWithInstructorById(saved.getId()).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("Hemming and Patching");
        assertThat(reloaded.getCapacity()).isEqualTo(10);
        assertThat(reloaded.getCode()).isEqualTo("SW-1");
        assertThat(reloaded.getInstructor().getId()).isEqualTo(sam.getId());
    }

    @Test
    void databaseRejectsDuplicateWorkshopCode() {
        workshopRepository.saveAndFlush(new Workshop("SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8, ruth));

        assertThatThrownBy(() -> workshopRepository.saveAndFlush(
                new Workshop("SW-1", "Different title", LocalDate.of(2026, 12, 2), 30, 5, sam)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .satisfies(ex -> assertThat(IntegrityViolationTranslator
                        .translate((DataIntegrityViolationException) ex).getField()).isEqualTo("code"));
    }

    @Test
    void codeExistenceChecksIgnoreTheRecordBeingEdited() {
        Workshop first = workshopRepository.saveAndFlush(
                new Workshop("SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        Workshop second = workshopRepository.saveAndFlush(
                new Workshop("PT-1", "Bowls", LocalDate.of(2026, 12, 3), 120, 6, sam));

        assertThat(workshopRepository.existsByCode("SW-1")).isTrue();
        assertThat(workshopRepository.existsByCodeAndIdNot("SW-1", first.getId())).isFalse();
        assertThat(workshopRepository.existsByCodeAndIdNot("SW-1", second.getId())).isTrue();
    }

    @Test
    void workshopWithoutInstructorIsRejected() {
        assertThatThrownBy(() -> workshopRepository.saveAndFlush(
                new Workshop("SW-9", "No instructor", LocalDate.of(2026, 12, 1), 60, 8, null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void foreignKeyRejectsUnknownInstructorId() {
        assertThatThrownBy(() -> entityManager.getEntityManager().createNativeQuery("""
                        INSERT INTO workshops (code, title, scheduled_date, duration_minutes, capacity, instructor_id)
                        VALUES ('XX-1', 'Orphan workshop', DATE '2026-12-01', 60, 8, 99999)
                        """).executeUpdate())
                .satisfies(ex -> assertThat(ex).hasStackTraceContaining("FK_WORKSHOP_INSTRUCTOR"));
    }

    @Test
    void checkConstraintRejectsCapacityOutOfRange() {
        Workshop saved = workshopRepository.saveAndFlush(
                new Workshop("SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8, ruth));
        saved.setCapacity(0);

        assertThatThrownBy(() -> workshopRepository.saveAndFlush(saved))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByIdReturnsEmptyForMissingWorkshop() {
        assertThat(workshopRepository.findWithInstructorById(12_345L)).isEmpty();
        assertThat(workshopRepository.findById(12_345L)).isEmpty();
    }

    @Test
    void listIsOrderedByDateAndLoadsInstructor() {
        workshopRepository.save(new Workshop("SW-2", "Darning", LocalDate.of(2026, 12, 8), 45, 8, ruth));
        workshopRepository.save(new Workshop("PT-1", "Bowls", LocalDate.of(2026, 12, 3), 120, 6, sam));
        workshopRepository.flush();
        entityManager.clear();

        List<Workshop> workshops = workshopRepository.findAllByOrderByScheduledDateAscCodeAsc();

        assertThat(workshops).extracting(Workshop::getCode).containsExactly("PT-1", "SW-2");
        assertThat(workshops).allSatisfy(w -> assertThat(Hibernate.isInitialized(w.getInstructor())).isTrue());
    }
}
