package ca.communitycentre.workshops.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import ca.communitycentre.workshops.projection.WorkshopOverview;
import ca.communitycentre.workshops.repository.InstructorRepository;
import ca.communitycentre.workshops.repository.WorkshopRepository;

@DataJpaTest
class DataSeederTest {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private WorkshopRepository workshopRepository;

    @Test
    void seedsTenOfEachOnceAndSkipsWhenDataExists() {
        DataSeeder seeder = new DataSeeder(instructorRepository, workshopRepository);

        assertThat(seeder.seed()).isTrue();
        assertThat(seeder.seed()).isFalse();

        assertThat(instructorRepository.count()).isEqualTo(10);
        assertThat(workshopRepository.count()).isEqualTo(10);

        Map<String, Long> workshopsPerInstructor = instructorRepository.findWorkshopOverview().stream()
                .collect(Collectors.groupingBy(WorkshopOverview::instructorName, Collectors.counting()));
        assertThat(workshopsPerInstructor).containsEntry("Amara Okafor", 2L).containsEntry("Daniel Reyes", 2L);
        // Ten instructors, but only eight teach something, so two are missing from the join.
        assertThat(workshopsPerInstructor).hasSize(8).doesNotContainKeys("Lucas Moreau", "Sofia Petrova");
    }
}
