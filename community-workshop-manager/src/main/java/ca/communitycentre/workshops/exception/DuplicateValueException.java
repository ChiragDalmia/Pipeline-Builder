package ca.communitycentre.workshops.exception;

/** A value that must be unique (instructor email or workshop code) is already in use. */
public class DuplicateValueException extends FieldValidationException {

    public DuplicateValueException(String field, String message) {
        super(field, message);
    }
}
