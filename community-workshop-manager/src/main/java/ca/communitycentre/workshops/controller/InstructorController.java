package ca.communitycentre.workshops.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.communitycentre.workshops.entity.Instructor;
import ca.communitycentre.workshops.exception.FieldValidationException;
import ca.communitycentre.workshops.exception.IntegrityViolationTranslator;
import ca.communitycentre.workshops.form.InstructorForm;
import ca.communitycentre.workshops.service.InstructorService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/instructors")
public class InstructorController {

    private static final String FORM_VIEW = "instructors/form";

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("instructors", instructorService.findAll());
        return "instructors/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("instructorForm", new InstructorForm());
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("instructorForm") InstructorForm form, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            Instructor saved = instructorService.create(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Instructor " + saved.getFullName() + " was added.");
            return "redirect:/instructors";
        } catch (FieldValidationException ex) {
            FormErrors.apply(result, ex);
        } catch (DataIntegrityViolationException ex) {
            // Raised on flush or commit if the database rejects the row.
            FormErrors.apply(result, IntegrityViolationTranslator.translate(ex));
        }
        return FORM_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Instructor instructor = instructorService.findById(id);
        model.addAttribute("instructorId", id);
        model.addAttribute("instructorForm", InstructorForm.from(instructor));
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("instructorForm") InstructorForm form,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("instructorId", id);
        if (result.hasErrors()) {
            instructorService.findById(id); // unknown IDs still lead to the not-found page
            return FORM_VIEW;
        }
        try {
            Instructor saved = instructorService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Changes to " + saved.getFullName() + " were saved.");
            return "redirect:/instructors";
        } catch (FieldValidationException ex) {
            FormErrors.apply(result, ex);
        } catch (DataIntegrityViolationException ex) {
            FormErrors.apply(result, IntegrityViolationTranslator.translate(ex));
        }
        return FORM_VIEW;
    }
}
