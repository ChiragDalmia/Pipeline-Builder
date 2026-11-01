package ca.communitycentre.workshops.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.InvalidInstructorException;
import ca.communitycentre.workshops.exception.RecordNotFoundException;
import ca.communitycentre.workshops.form.WorkshopForm;
import ca.communitycentre.workshops.projection.WorkshopOverview;
import ca.communitycentre.workshops.repository.InstructorRepository;
import ca.communitycentre.workshops.repository.WorkshopRepository;

@ExtendWith(MockitoExtension.class)
class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private WorkshopService workshopService;

    private Instructor ruth;
    private Instructor sam;

    @BeforeEach
    void setUp() {
        ruth = new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing");
        ReflectionTestUtils.setField(ruth, "id", 1L);
        sam = new Instructor("Sam Ortiz", "sam@example.org", "Pottery");
        ReflectionTestUtils.setField(sam, "id", 2L);
    }

    private static WorkshopForm form(String code, Long instructorId) {
        WorkshopForm form = new WorkshopForm();
        form.setCode(code);
        form.setTitle("  Hemming   Trousers ");
        form.setScheduledDate(LocalDate.of(2026, 12, 1));
        form.setDurationMinutes(60);
        form.setCapacity(8);
        form.setInstructorId(instructorId);
        return form;
    }

    private Workshop existingWorkshop() {
        Workshop workshop = new Workshop("SW-1", "Hemming", LocalDate.of(2026, 11, 20), 45, 6, ruth);
        ReflectionTestUtils.setField(workshop, "id", 10L);
        return workshop;
    }

    @Test
    void createLinksInstructorAndNormalisesCode() {
        when(workshopRepository.existsByCode("SW-1")).thenReturn(false);
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(ruth));
        when(workshopRepository.saveAndFlush(any(Workshop.class))).thenAnswer(call -> call.getArgument(0));

        workshopService.create(form(" sw-1 ", 1L));

        ArgumentCaptor<Workshop> saved = ArgumentCaptor.forClass(Workshop.class);
        verify(workshopRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getCode()).isEqualTo("SW-1");
        assertThat(saved.getValue().getTitle()).isEqualTo("Hemming Trousers");
        assertThat(saved.getValue().getInstructor()).isSameAs(ruth);
        assertThat(saved.getValue().getDurationMinutes()).isEqualTo(60);
        assertThat(saved.getValue().getCapacity()).isEqualTo(8);
    }

    @Test
    void createRejectsUnknownInstructor() {
        when(workshopRepository.existsByCode("SW-1")).thenReturn(false);
        when(instructorRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workshopService.create(form("SW-1", 77L)))
                .isInstanceOf(InvalidInstructorException.class)
                .hasFieldOrPropertyWithValue("field", "instructorId");
        verify(workshopRepository, never()).saveAndFlush(any());
    }

    @Test
    void createRejectsDuplicateCode() {
        when(workshopRepository.existsByCode("SW-1")).thenReturn(true);

        assertThatThrownBy(() -> workshopService.create(form("sw-1", 1L)))
                .isInstanceOf(DuplicateValueException.class)
                .hasFieldOrPropertyWithValue("field", "code");
        verify(workshopRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateKeepsOwnCodeAndChangesInstructor() {
        Workshop existing = existingWorkshop();
        when(workshopRepository.findWithInstructorById(10L)).thenReturn(Optional.of(existing));
        when(workshopRepository.existsByCodeAndIdNot("SW-1", 10L)).thenReturn(false);
        when(instructorRepository.findById(2L)).thenReturn(Optional.of(sam));

        Workshop updated = workshopService.update(10L, form("SW-1", 2L));

        assertThat(updated).isSameAs(existing);
        assertThat(updated.getId()).isEqualTo(10L);
        assertThat(updated.getCode()).isEqualTo("SW-1");
        assertThat(updated.getInstructor()).isSameAs(sam);
        assertThat(updated.getScheduledDate()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(updated.getCapacity()).isEqualTo(8);
        verify(workshopRepository).flush();
        verify(workshopRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateRejectsCodeUsedByAnotherWorkshop() {
        Workshop existing = existingWorkshop();
        when(workshopRepository.findWithInstructorById(10L)).thenReturn(Optional.of(existing));
        when(workshopRepository.existsByCodeAndIdNot("PT-1", 10L)).thenReturn(true);

        assertThatThrownBy(() -> workshopService.update(10L, form("PT-1", 1L)))
                .isInstanceOf(DuplicateValueException.class);
        assertThat(existing.getCode()).isEqualTo("SW-1");
        verify(workshopRepository, never()).flush();
    }

    @Test
    void updateRejectsUnknownInstructorWithoutChangingWorkshop() {
        Workshop existing = existingWorkshop();
        when(workshopRepository.findWithInstructorById(10L)).thenReturn(Optional.of(existing));
        when(workshopRepository.existsByCodeAndIdNot("SW-1", 10L)).thenReturn(false);
        when(instructorRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workshopService.update(10L, form("SW-1", 55L)))
                .isInstanceOf(InvalidInstructorException.class);
        assertThat(existing.getInstructor()).isSameAs(ruth);
        assertThat(existing.getTitle()).isEqualTo("Hemming");
    }

    @Test
    void updateThrowsForMissingWorkshop() {
        when(workshopRepository.findWithInstructorById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workshopService.update(404L, form("SW-1", 1L)))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void overviewComesFromTheJoinQuery() {
        List<WorkshopOverview> rows = List.of(new WorkshopOverview(1L, "Ruth Adeyemi", "ruth@example.org", "Sewing",
                10L, "SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8));
        when(instructorRepository.findWorkshopOverview()).thenReturn(rows);

        assertThat(workshopService.getOverview()).isEqualTo(rows);
    }
}
