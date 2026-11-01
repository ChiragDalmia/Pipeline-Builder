package ca.communitycentre.workshops.config;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.repository.InstructorRepository;
import ca.communitycentre.workshops.repository.WorkshopRepository;

/**
 * Loads sample data into an empty database. If any instructor or workshop already
 * exists the seeder does nothing, so restarting the application never duplicates rows.
 */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final InstructorRepository instructorRepository;
    private final WorkshopRepository workshopRepository;

    public DataSeeder(InstructorRepository instructorRepository, WorkshopRepository workshopRepository) {
        this.instructorRepository = instructorRepository;
        this.workshopRepository = workshopRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed();
    }

    @Transactional
    public boolean seed() {
        if (instructorRepository.count() > 0 || workshopRepository.count() > 0) {
            log.info("Sample data skipped: database already contains records.");
            return false;
        }

        List<Instructor> instructors = instructorRepository.saveAll(List.of(
                new Instructor("Amara Okafor", "amara.okafor@northside-community.org", "Home Cooking"),
                new Instructor("Daniel Reyes", "daniel.reyes@northside-community.org", "Digital Skills"),
                new Instructor("Priya Nair", "priya.nair@northside-community.org", "Gardening"),
                new Instructor("Tomasz Kowalski", "tomasz.kowalski@northside-community.org", "Woodworking"),
                new Instructor("Grace Mensah", "grace.mensah@northside-community.org", "First Aid"),
                new Instructor("Hannah Lindqvist", "hannah.lindqvist@northside-community.org", "Watercolour Painting"),
                new Instructor("Omar Haddad", "omar.haddad@northside-community.org", "Personal Finance"),
                new Instructor("Mei Chen", "mei.chen@northside-community.org", "Yoga and Mobility"),
                new Instructor("Lucas Moreau", "lucas.moreau@northside-community.org", "Bicycle Repair"),
                new Instructor("Sofia Petrova", "sofia.petrova@northside-community.org", "Conversational French")));

        Instructor amara = instructors.get(0);
        Instructor daniel = instructors.get(1);
        LocalDate start = LocalDate.of(2026, 11, 14);

        // Amara and Daniel teach two workshops each. Lucas and Sofia have none yet.
        workshopRepository.saveAll(List.of(
                new Workshop("CK-101", "Batch Cooking on a Budget", start, 120, 12, amara),
                new Workshop("CK-102", "Breads Without a Mixer", start.plusDays(21), 150, 10, amara),
                new Workshop("DG-110", "Smartphone Basics for Beginners", start.plusDays(3), 90, 15, daniel),
                new Workshop("DG-120", "Staying Safe Online", start.plusDays(17), 60, 20, daniel),
                new Workshop("GD-201", "Winter Herb Windowsills", start.plusDays(7), 75, 16, instructors.get(2)),
                new Workshop("WW-301", "Build a Simple Bird Box", start.plusDays(9), 180, 8, instructors.get(3)),
                new Workshop("FA-401", "Emergency First Aid at Home", start.plusDays(10), 240, 18, instructors.get(4)),
                new Workshop("AR-501", "Watercolour Landscapes", start.plusDays(14), 120, 12, instructors.get(5)),
                new Workshop("FN-601", "Household Budgeting Made Simple", start.plusDays(15), 90, 25, instructors.get(6)),
                new Workshop("YG-701", "Gentle Yoga for Desk Workers", start.plusDays(19), 60, 20, instructors.get(7))));

        log.info("Sample data loaded: {} instructors and {} workshops.",
                instructorRepository.count(), workshopRepository.count());
        return true;
    }
}
