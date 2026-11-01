package ca.communitycentre.workshops.exception;

import java.util.Locale;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Turns a database constraint failure into a message that can be shown on a form.
 * The constraint names are set explicitly on the entities, so they can be matched
 * here without showing any SQL to the user.
 */
public final class IntegrityViolationTranslator {

    public static final String GENERAL_MESSAGE =
            "The record could not be saved because it conflicts with existing data. Check the values and try again.";

    private IntegrityViolationTranslator() {
    }

    /**
     * @return a field-specific exception when the constraint is recognised, otherwise
     *         an exception with a {@code null} field that should be shown as a form-level error
     */
    public static FieldValidationException translate(DataIntegrityViolationException ex) {
        String detail = collectMessages(ex).toLowerCase(Locale.ROOT);
        if (detail.contains("uk_instructor_email")) {
            return new DuplicateValueException("email", "Another instructor already uses this email address.");
        }
        if (detail.contains("uk_workshop_code")) {
            return new DuplicateValueException("code", "Another workshop already uses this code.");
        }
        if (detail.contains("fk_workshop_instructor")) {
            return new FieldValidationException("instructorId", "The selected instructor does not exist.");
        }
        return new FieldValidationException(null, GENERAL_MESSAGE);
    }

    private static String collectMessages(Throwable ex) {
        StringBuilder text = new StringBuilder();
        Throwable current = ex;
        while (current != null) {
            if (current.getMessage() != null) {
                text.append(current.getMessage()).append(' ');
            }
            if (current instanceof ConstraintViolationException violation && violation.getConstraintName() != null) {
                text.append(violation.getConstraintName()).append(' ');
            }
            current = current.getCause() == current ? null : current.getCause();
        }
        return text.toString();
    }
}
