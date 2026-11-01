package ca.communitycentre.workshops;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Starts the real embedded Tomcat with Jasper and checks that the JSP views render,
 * which MockMvc alone cannot show.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "app.seed-data=true",
        "spring.datasource.url=jdbc:h2:mem:jsp-rendering;DB_CLOSE_DELAY=-1"
})
class JspRenderingIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Accept", "text/html")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void listPagesAndOverviewRenderSeededData() throws Exception {
        HttpResponse<String> instructors = get("/instructors");
        assertThat(instructors.statusCode()).isEqualTo(200);
        assertThat(instructors.body()).contains("<h1>Instructors</h1>", "10 instructors", "Amara Okafor");

        HttpResponse<String> workshops = get("/workshops");
        assertThat(workshops.statusCode()).isEqualTo(200);
        assertThat(workshops.body()).contains("10 workshops", "CK-101", "Batch Cooking on a Budget");

        HttpResponse<String> overview = get("/overview");
        assertThat(overview.statusCode()).isEqualTo(200);
        assertThat(overview.body()).contains("Schedule overview", "Daniel Reyes")
                .doesNotContain("Lucas Moreau", "Sofia Petrova");
    }

    @Test
    void workshopFormListsInstructorsFromDatabase() throws Exception {
        HttpResponse<String> form = get("/workshops/new");
        assertThat(form.statusCode()).isEqualTo(200);
        assertThat(form.body()).contains("<select id=\"instructorId\"", "Sofia Petrova (Conversational French)");
    }

    @Test
    void unknownRecordAndUnknownPathShowFriendlyPages() throws Exception {
        HttpResponse<String> missing = get("/workshops/9999/edit");
        assertThat(missing.statusCode()).isEqualTo(404);
        assertThat(missing.body()).contains("Record not found").doesNotContain("Exception");

        HttpResponse<String> unknownPath = get("/no-such-page");
        assertThat(unknownPath.statusCode()).isEqualTo(404);
        assertThat(unknownPath.body()).contains("Page not found");
    }

    @Test
    void rootRedirectsToOverview() throws Exception {
        HttpResponse<String> root = get("/");
        assertThat(root.statusCode()).isEqualTo(302);
        assertThat(root.headers().firstValue("Location")).hasValueSatisfying(l -> assertThat(l).endsWith("/overview"));
    }
}
