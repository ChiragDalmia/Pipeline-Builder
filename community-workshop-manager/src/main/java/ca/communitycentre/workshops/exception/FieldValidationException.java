package ca.communitycentre.workshops.exception;

/**
 * Business rule failure that belongs to one form field, such as a duplicate
 * email address. Controllers turn it into a field error on the form.
 */
public class FieldValidationException extends RuntimeException {

    private final String field;

    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
