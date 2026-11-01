package ca.communitycentre.workshops.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.RecordNotFoundException;
import ca.communitycentre.workshops.form.InstructorForm;
import ca.communitycentre.workshops.service.InstructorService;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorService instructorService;

    @Test
    void blankFormShowsRequiredFieldErrorsAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/instructors").param("fullName", " ").param("email", "").param("speciality", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attributeHasFieldErrors("instructorForm", "fullName", "email", "speciality"));
        verify(instructorService, never()).create(any());
    }

    @Test
    void invalidEmailAndOverlongNameAreRejected() throws Exception {
        mockMvc.perform(post("/instructors")
                        .param("fullName", "x".repeat(101))
                        .param("email", "not-an-email")
                        .param("speciality", "Sewing"))
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attributeHasFieldErrorCode("instructorForm", "fullName", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("instructorForm", "email", "Email"));
    }

    @Test
    void validCreateRedirectsWithSuccessMessage() throws Exception {
        when(instructorService.create(any(InstructorForm.class)))
                .thenReturn(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));

        mockMvc.perform(post("/instructors")
                        .param("fullName", "Ruth Adeyemi")
                        .param("email", "ruth@example.org")
                        .param("speciality", "Sewing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instructors"))
                .andExpect(flash().attribute("successMessage", "Instructor Ruth Adeyemi was added."));
    }

    @Test
    void duplicateEmailFromServiceBecomesFieldError() throws Exception {
        when(instructorService.create(any())).thenThrow(new DuplicateValueException("email", "Already used."));

        mockMvc.perform(post("/instructors")
                        .param("fullName", "Ruth Adeyemi")
                        .param("email", "ruth@example.org")
                        .param("speciality", "Sewing"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attributeHasFieldErrors("instructorForm", "email"));
    }

    @Test
    void constraintViolationAtCommitBecomesEmailFieldError() throws Exception {
        DataIntegrityViolationException commitFailure = new DataIntegrityViolationException("could not execute statement",
                new ConstraintViolationException("Unique index or primary key violation",
                        null, "PUBLIC.UK_INSTRUCTOR_EMAIL"));
        when(instructorService.update(eq(3L), any())).thenThrow(commitFailure);

        mockMvc.perform(post("/instructors/3")
                        .param("fullName", "Ruth Adeyemi")
                        .param("email", "ruth@example.org")
                        .param("speciality", "Sewing"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attribute("instructorId", 3L))
                .andExpect(model().attributeHasFieldErrors("instructorForm", "email"));
    }

    @Test
    void unknownConstraintBecomesGeneralFormError() throws Exception {
        when(instructorService.create(any())).thenThrow(new DataIntegrityViolationException("something else"));

        mockMvc.perform(post("/instructors")
                        .param("fullName", "Ruth Adeyemi")
                        .param("email", "ruth@example.org")
                        .param("speciality", "Sewing"))
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attributeHasErrors("instructorForm"));
    }

    @Test
    void editFormIsPopulatedFromExistingRecord() throws Exception {
        when(instructorService.findById(3L)).thenReturn(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));

        mockMvc.perform(get("/instructors/3/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attribute("instructorId", 3L))
                .andExpect(model().attribute("instructorForm",
                        org.hamcrest.Matchers.hasProperty("email", org.hamcrest.Matchers.is("ruth@example.org"))));
    }

    @Test
    void validUpdateRedirectsToList() throws Exception {
        when(instructorService.update(eq(3L), any()))
                .thenReturn(new Instructor("Ruth Adeyemi", "ruth@example.org", "Tailoring"));

        mockMvc.perform(post("/instructors/3")
                        .param("fullName", "Ruth Adeyemi")
                        .param("email", "ruth@example.org")
                        .param("speciality", "Tailoring")
                        .param("id", "999"))
                .andExpect(redirectedUrl("/instructors"))
                .andExpect(flash().attributeExists("successMessage"));
        verify(instructorService).update(eq(3L), any());
    }

    @Test
    void unknownIdShowsNotFoundPage() throws Exception {
        when(instructorService.findById(404L)).thenThrow(new RecordNotFoundException("Instructor", 404L));

        mockMvc.perform(get("/instructors/404/edit"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/not-found"));
    }

    @Test
    void nonNumericIdShowsNotFoundPage() throws Exception {
        mockMvc.perform(get("/instructors/abc/edit"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/not-found"));
    }
}
