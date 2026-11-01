package ca.communitycentre.workshops.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import ca.communitycentre.workshops.entity.Workshop;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Fields a user may submit for a workshop. The instructor is chosen by ID and
 * resolved against the database in the service layer.
 */
public class WorkshopForm {

    @NotBlank(message = "Enter a workshop code.")
    @Size(max = 20, message = "Workshop code must be 20 characters or fewer.")
    @Pattern(regexp = "^\\s*[A-Za-z0-9]+(-[A-Za-z0-9]+)*\\s*$",
            message = "Use letters, numbers and single hyphens only, for example CK-101.")
    private String code;

    @NotBlank(message = "Enter a workshop title.")
    @Size(max = 120, message = "Title must be 120 characters or fewer.")
    private String title;

    @NotNull(message = "Choose the date the workshop runs.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate scheduledDate;

    @NotNull(message = "Enter the duration in minutes.")
    @Min(value = Workshop.MIN_DURATION, message = "Duration must be at least 15 minutes.")
    @Max(value = Workshop.MAX_DURATION, message = "Duration cannot be more than 480 minutes.")
    private Integer durationMinutes;

    @NotNull(message = "Enter the participant capacity.")
    @Min(value = Workshop.MIN_CAPACITY, message = "Capacity must be at least 1 participant.")
    @Max(value = Workshop.MAX_CAPACITY, message = "Capacity cannot be more than 200 participants.")
    private Integer capacity;

    @NotNull(message = "Choose an instructor.")
    private Long instructorId;

    public static WorkshopForm from(Workshop workshop) {
        WorkshopForm form = new WorkshopForm();
        form.setCode(workshop.getCode());
        form.setTitle(workshop.getTitle());
        form.setScheduledDate(workshop.getScheduledDate());
        form.setDurationMinutes(workshop.getDurationMinutes());
        form.setCapacity(workshop.getCapacity());
        form.setInstructorId(workshop.getInstructor().getId());
        return form;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}
