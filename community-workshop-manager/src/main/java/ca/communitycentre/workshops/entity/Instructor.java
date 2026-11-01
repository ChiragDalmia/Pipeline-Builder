package ca.communitycentre.workshops.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "instructors",
        uniqueConstraints = @UniqueConstraint(name = "uk_instructor_email", columnNames = "email"))
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "speciality", nullable = false, length = 80)
    private String speciality;

    // Inverse side of the relationship. The foreign key lives in the workshops table.
    @OneToMany(mappedBy = "instructor")
    @OrderBy("scheduledDate ASC")
    private List<Workshop> workshops = new ArrayList<>();

    protected Instructor() {
        // required by JPA
    }

    public Instructor(String fullName, String email, String speciality) {
        this.fullName = fullName;
        this.email = email;
        this.speciality = speciality;
    }

    /** Keeps both sides of the relationship in step when a workshop is linked in memory. */
    public void addWorkshop(Workshop workshop) {
        workshops.add(workshop);
        workshop.setInstructor(this);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }
}
