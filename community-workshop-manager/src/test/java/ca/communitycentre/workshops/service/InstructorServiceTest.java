package ca.communitycentre.workshops.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.RecordNotFoundException;
import ca.communitycentre.workshops.form.InstructorForm;
import ca.communitycentre.workshops.repository.InstructorRepository;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private InstructorService instructorService;

    private static InstructorForm form(String name, String email, String speciality) {
        InstructorForm form = new InstructorForm();
        form.setFullName(name);
        form.setEmail(email);
        form.setSpeciality(speciality);
        return form;
    }

    private static Instructor instructorWithId(long id, String name, String email, String speciality) {
        Instructor instructor = new Instructor(name, email, speciality);
        ReflectionTestUtils.setField(instructor, "id", id);
        return instructor;
    }

    @Test
    void createNormalisesValuesAndSaves() {
        when(instructorRepository.existsByEmail("ruth@example.org")).thenReturn(false);
        when(instructorRepository.saveAndFlush(any(Instructor.class))).thenAnswer(call -> call.getArgument(0));

        Instructor created = instructorService.create(form("  Ruth   Adeyemi ", " Ruth@Example.ORG ", " Sewing "));

        ArgumentCaptor<Instructor> saved = ArgumentCaptor.forClass(Instructor.class);
        verify(instructorRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getFullName()).isEqualTo("Ruth Adeyemi");
        assertThat(saved.getValue().getEmail()).isEqualTo("ruth@example.org");
        assertThat(saved.getValue().getSpeciality()).isEqualTo("Sewing");
        assertThat(created).isSameAs(saved.getValue());
    }

    @Test
    void createRejectsEmailAlreadyInUse() {
        when(instructorRepository.existsByEmail("ruth@example.org")).thenReturn(true);

        assertThatThrownBy(() -> instructorService.create(form("Ruth", "RUTH@example.org", "Sewing")))
                .isInstanceOf(DuplicateValueException.class)
                .hasFieldOrPropertyWithValue("field", "email");
        verify(instructorRepository, never()).saveAndFlush(any());
    }

    @Test
    void findByIdThrowsForMissingInstructor() {
        when(instructorRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instructorService.findById(42L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void updateChangesExistingInstructorAndKeepsOwnEmail() {
        Instructor existing = instructorWithId(7L, "Ruth Adeyemi", "ruth@example.org", "Sewing");
        when(instructorRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(instructorRepository.existsByEmailAndIdNot("ruth@example.org", 7L)).thenReturn(false);

        Instructor updated = instructorService.update(7L, form("Ruth Adeyemi", "ruth@example.org", "Tailoring"));

        assertThat(updated).isSameAs(existing);
        assertThat(updated.getId()).isEqualTo(7L);
        assertThat(updated.getSpeciality()).isEqualTo("Tailoring");
        assertThat(updated.getEmail()).isEqualTo("ruth@example.org");
        verify(instructorRepository).flush();
        verify(instructorRepository, never()).save(any());
        verify(instructorRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateRejectsEmailUsedByAnotherInstructor() {
        Instructor existing = instructorWithId(7L, "Ruth Adeyemi", "ruth@example.org", "Sewing");
        when(instructorRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(instructorRepository.existsByEmailAndIdNot("sam@example.org", 7L)).thenReturn(true);

        assertThatThrownBy(() -> instructorService.update(7L, form("Ruth", "sam@example.org", "Sewing")))
                .isInstanceOf(DuplicateValueException.class);
        assertThat(existing.getEmail()).isEqualTo("ruth@example.org");
        verify(instructorRepository, never()).flush();
    }

    @Test
    void updateThrowsForMissingInstructor() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instructorService.update(99L, form("A", "a@example.org", "B")))
                .isInstanceOf(RecordNotFoundException.class);
        verify(instructorRepository, never()).flush();
    }
}
