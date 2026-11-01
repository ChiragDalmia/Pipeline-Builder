package ca.communitycentre.workshops.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ca.communitycentre.workshops.exception.RecordNotFoundException;

/**
 * Shows a friendly page for unknown record IDs. Other errors are rendered by
 * /WEB-INF/jsp/error.jsp through Spring Boot's error controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(RecordNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/not-found";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String badId(Model model) {
        model.addAttribute("message", "The address does not refer to a valid record.");
        return "error/not-found";
    }
}
