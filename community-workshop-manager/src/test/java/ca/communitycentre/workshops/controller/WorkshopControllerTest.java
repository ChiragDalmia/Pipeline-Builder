package ca.communitycentre.workshops.controller;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.DuplicateValueException;
import ca.communitycentre.workshops.exception.InvalidInstructorException;
import ca.communitycentre.workshops.service.InstructorService;
import ca.communitycentre.workshops.service.WorkshopService;

@WebMvcTest(WorkshopController.class)
class WorkshopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkshopService workshopService;

    @MockitoBean
    private InstructorService instructorService;

    private final Instructor ruth = new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing");

    @BeforeEach
    void setUp() {
        when(instructorService.findAll()).thenReturn(List.of(ruth));
    }

    @Test
    void newFormLoadsInstructorChoices() throws Exception {
        mockMvc.perform(get("/workshops/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("workshops/form"))
                .andExpect(model().attribute("instructors", hasSize(1)));
    }

    @Test
    void missingFieldsAreReportedAndEnteredValuesKept() throws Exception {
        mockMvc.perform(post("/workshops")
                        .param("code", "")
                        .param("title", "Hemming Trousers")
                        .param("scheduledDate", "")
                        .param("durationMinutes", "")
                        .param("capacity", "")
                        .param("instructorId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("workshops/form"))
                .andExpect(model().attributeHasFieldErrors("workshopForm",
                        "code", "scheduledDate", "durationMinutes", "capacity"))
                .andExpect(model().attribute("workshopForm", hasProperty("title", is("Hemming Trousers"))))
                .andExpect(model().attribute("workshopForm", hasProperty("instructorId", is(1L))))
                .andExpect(model().attribute("instructors", hasSize(1)));
        verify(workshopService, never()).create(any());
    }

    @Test
    void invalidDateAndOutOfRangeNumbersAreRejected() throws Exception {
        mockMvc.perform(post("/workshops")
                        .param("code", "SW-1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-13-45")
                        .param("durationMinutes", "5")
                        .param("capacity", "500")
                        .param("instructorId", ""))
                .andExpect(view().name("workshops/form"))
                .andExpect(model().attributeHasFieldErrorCode("workshopForm", "scheduledDate", "typeMismatch"))
                .andExpect(model().attributeHasFieldErrorCode("workshopForm", "durationMinutes", "Min"))
                .andExpect(model().attributeHasFieldErrorCode("workshopForm", "capacity", "Max"))
                .andExpect(model().attributeHasFieldErrorCode("workshopForm", "instructorId", "NotNull"));
    }

    @Test
    void codeWithSpacesInsideIsRejected() throws Exception {
        mockMvc.perform(post("/workshops")
                        .param("code", "SW 1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-12-01")
                        .param("durationMinutes", "60")
                        .param("capacity", "8")
                        .param("instructorId", "1"))
                .andExpect(model().attributeHasFieldErrorCode("workshopForm", "code", "Pattern"));
    }

    @Test
    void validCreateRedirectsWithSuccessMessage() throws Exception {
        when(workshopService.create(any()))
                .thenReturn(new Workshop("SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8, ruth));

        mockMvc.perform(post("/workshops")
                        .param("code", "sw-1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-12-01")
                        .param("durationMinutes", "60")
                        .param("capacity", "8")
                        .param("instructorId", "1"))
                .andExpect(redirectedUrl("/workshops"))
                .andExpect(flash().attribute("successMessage", "Workshop SW-1 was added."));
    }

    @Test
    void unknownInstructorBecomesFieldError() throws Exception {
        when(workshopService.create(any())).thenThrow(new InvalidInstructorException(99L));

        mockMvc.perform(post("/workshops")
                        .param("code", "SW-1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-12-01")
                        .param("durationMinutes", "60")
                        .param("capacity", "8")
                        .param("instructorId", "99"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workshopForm", "instructorId"))
                .andExpect(model().attribute("instructors", hasSize(1)));
    }

    @Test
    void duplicateCodeOnUpdateBecomesFieldError() throws Exception {
        when(workshopService.update(eq(10L), any())).thenThrow(new DuplicateValueException("code", "In use."));

        mockMvc.perform(post("/workshops/10")
                        .param("code", "PT-1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-12-01")
                        .param("durationMinutes", "60")
                        .param("capacity", "8")
                        .param("instructorId", "1"))
                .andExpect(view().name("workshops/form"))
                .andExpect(model().attribute("workshopId", 10L))
                .andExpect(model().attributeHasFieldErrors("workshopForm", "code"));
    }

    @Test
    void validUpdateUsesPathIdAndRedirects() throws Exception {
        when(workshopService.update(eq(10L), any()))
                .thenReturn(new Workshop("SW-1", "Hemming", LocalDate.of(2026, 12, 1), 60, 8, ruth));

        mockMvc.perform(post("/workshops/10")
                        .param("code", "SW-1")
                        .param("title", "Hemming")
                        .param("scheduledDate", "2026-12-01")
                        .param("durationMinutes", "60")
                        .param("capacity", "8")
                        .param("instructorId", "2"))
                .andExpect(redirectedUrl("/workshops"))
                .andExpect(flash().attribute("successMessage", "Changes to workshop SW-1 were saved."));
        verify(workshopService).update(eq(10L), any());
    }
}
