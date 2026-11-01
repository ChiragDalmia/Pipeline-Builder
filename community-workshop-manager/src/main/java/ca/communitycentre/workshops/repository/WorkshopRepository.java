package ca.communitycentre.workshops.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ca.communitycentre.workshops.entity.Workshop;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {

    /** Loads the instructor in the same query so the list page does not need an open session. */
    @EntityGraph(attributePaths = "instructor")
    List<Workshop> findAllByOrderByScheduledDateAscCodeAsc();

    @EntityGraph(attributePaths = "instructor")
    Optional<Workshop> findWithInstructorById(Long id);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
