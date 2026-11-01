package ca.communitycentre.workshops.entity;

import java.time.LocalDate;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "workshops",
        uniqueConstraints = @UniqueConstraint(name = "uk_workshop_code", columnNames = "code"))
@Check(name = "ck_workshop_duration", constraints = "duration_minutes BETWEEN 15 AND 480")
@Check(name = "ck_workshop_capacity", constraints = "capacity BETWEEN 1 AND 200")
public class Workshop {

    public static final int MIN_DURATION = 15;
    public static final int MAX_DURATION = 480;
    public static final int MIN_CAPACITY = 1;
    public static final int MAX_CAPACITY = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    // Owning side: every workshop must point at an existing instructor row.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_workshop_instructor"))
    private Instructor instructor;

    protected Workshop() {
        // required by JPA
    }

    public Workshop(String code, String title, LocalDate scheduledDate, int durationMinutes, int capacity,
                    Instructor instructor) {
        this.code = code;
        this.title = title;
        this.scheduledDate = scheduledDate;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
        this.instructor = instructor;
    }

    public Long getId() {
        return id;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}
