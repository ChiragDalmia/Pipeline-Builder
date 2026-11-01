package ca.communitycentre.workshops.form;

import ca.communitycentre.workshops.entity.Instructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Fields a user may submit for an instructor. The generated ID and the workshop
 * collection are deliberately absent, so request parameters cannot change them.
 */
public class InstructorForm {

    @NotBlank(message = "Enter the instructor's full name.")
    @Size(max = 100, message = "Full name must be 100 characters or fewer.")
    private String fullName;

    @NotBlank(message = "Enter an email address.")
    @Email(message = "Enter a valid email address, for example name@example.org.")
    @Size(max = 120, message = "Email must be 120 characters or fewer.")
    private String email;

    @NotBlank(message = "Enter the instructor's speciality.")
    @Size(max = 80, message = "Speciality must be 80 characters or fewer.")
    private String speciality;

    public static InstructorForm from(Instructor instructor) {
        InstructorForm form = new InstructorForm();
        form.setFullName(instructor.getFullName());
        form.setEmail(instructor.getEmail());
        form.setSpeciality(instructor.getSpeciality());
        return form;
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
}
