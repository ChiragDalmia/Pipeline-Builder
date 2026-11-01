package ca.communitycentre.workshops.projection;

import java.time.LocalDate;

/**
 * Read-only row produced by the instructor/workshop inner join in
 * {@link ca.communitycentre.workshops.repository.InstructorRepository#findWorkshopOverview()}.
 */
public record WorkshopOverview(
        Long instructorId,
        String instructorName,
        String instructorEmail,
        String speciality,
        Long workshopId,
        String workshopCode,
        String workshopTitle,
        LocalDate scheduledDate,
        int durationMinutes,
        int capacity) {
}
