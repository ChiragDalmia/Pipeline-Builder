package ca.communitycentre.workshops.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.projection.WorkshopOverview;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    List<Instructor> findAllByOrderByFullNameAsc();

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Inner join between instructors and their workshops. Instructors with no
     * workshops have no matching rows on the right-hand side, so they are left out.
     */
    @Query("""
            SELECT new ca.communitycentre.workshops.projection.WorkshopOverview(
                i.id, i.fullName, i.email, i.speciality,
                w.id, w.code, w.title, w.scheduledDate, w.durationMinutes, w.capacity)
            FROM Instructor i
            INNER JOIN i.workshops w
            ORDER BY w.scheduledDate ASC, w.code ASC
            """)
    List<WorkshopOverview> findWorkshopOverview();
}
