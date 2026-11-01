package ca.communitycentre.workshops.exception;

/** A workshop form did not refer to an instructor that exists in the database. */
public class InvalidInstructorException extends FieldValidationException {

    public InvalidInstructorException(Long instructorId) {
        super("instructorId", instructorId == null
                ? "Choose an instructor."
                : "The selected instructor no longer exists. Choose another instructor.");
    }
}
