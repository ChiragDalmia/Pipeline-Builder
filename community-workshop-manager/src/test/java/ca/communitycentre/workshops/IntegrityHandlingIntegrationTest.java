package ca.communitycentre.workshops;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.repository.InstructorRepository;

/**
 * Simulates two users saving the same email at once: the service's own duplicate
 * check is made to miss, so the unique constraint fails when the insert is flushed.
 * The real controller, service, transaction and H2 database are used.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:integrity-handling;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class IntegrityHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private InstructorRepository instructorRepository;

    @Test
    void uniqueConstraintFailureOnFlushIsShownAsFormErrorAndRolledBack() throws Exception {
        instructorRepository.saveAndFlush(new Instructor("Ruth Adeyemi", "ruth@example.org", "Sewing"));
        doReturn(false).when(instructorRepository).existsByEmail(anyString());

        mockMvc.perform(post("/instructors")
                        .param("fullName", "Second Ruth")
                        .param("email", "RUTH@example.org")
                        .param("speciality", "Pottery"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructors/form"))
                .andExpect(model().attributeHasFieldErrors("instructorForm", "email"));

        assertThat(instructorRepository.count()).isEqualTo(1);
    }
}
