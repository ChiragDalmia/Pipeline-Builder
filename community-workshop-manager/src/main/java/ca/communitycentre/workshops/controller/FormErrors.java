package ca.communitycentre.workshops.controller;

import org.springframework.validation.BindingResult;

import ca.communitycentre.workshops.exception.FieldValidationException;

/** Copies a service-layer validation failure onto the Spring binding result. */
final class FormErrors {

    private FormErrors() {
    }

    static void apply(BindingResult result, FieldValidationException ex) {
        if (ex.getField() == null) {
            result.reject("save.failed", ex.getMessage());
        } else {
            result.rejectValue(ex.getField(), "save.failed", ex.getMessage());
        }
    }
}
