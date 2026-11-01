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

import ca.communitycentre.workshops.entity.Workshop;
import ca.communitycentre.workshops.exception.FieldValidationException;
import ca.communitycentre.workshops.exception.IntegrityViolationTranslator;
import ca.communitycentre.workshops.form.WorkshopForm;
import ca.communitycentre.workshops.service.InstructorService;
import ca.communitycentre.workshops.service.WorkshopService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/workshops")
public class WorkshopController {

    private static final String FORM_VIEW = "workshops/form";

    private final WorkshopService workshopService;
    private final InstructorService instructorService;

    public WorkshopController(WorkshopService workshopService, InstructorService instructorService) {
        this.workshopService = workshopService;
        this.instructorService = instructorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("workshops", workshopService.findAll());
        return "workshops/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("workshopForm", new WorkshopForm());
        return showForm(model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("workshopForm") WorkshopForm form, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return showForm(model);
        }
        try {
            Workshop saved = workshopService.create(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Workshop " + saved.getCode() + " was added.");
            return "redirect:/workshops";
        } catch (FieldValidationException ex) {
            FormErrors.apply(result, ex);
        } catch (DataIntegrityViolationException ex) {
            FormErrors.apply(result, IntegrityViolationTranslator.translate(ex));
        }
        return showForm(model);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Workshop workshop = workshopService.findById(id);
        model.addAttribute("workshopId", id);
        model.addAttribute("workshopForm", WorkshopForm.from(workshop));
        return showForm(model);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("workshopForm") WorkshopForm form,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("workshopId", id);
        if (result.hasErrors()) {
            workshopService.findById(id); // unknown IDs still lead to the not-found page
            return showForm(model);
        }
        try {
            Workshop saved = workshopService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Changes to workshop " + saved.getCode() + " were saved.");
            return "redirect:/workshops";
        } catch (FieldValidationException ex) {
            FormErrors.apply(result, ex);
        } catch (DataIntegrityViolationException ex) {
            FormErrors.apply(result, IntegrityViolationTranslator.translate(ex));
        }
        return showForm(model);
    }

    /** Instructor choices always come from the database, including after a failed submit. */
    private String showForm(Model model) {
        model.addAttribute("instructors", instructorService.findAll());
        return FORM_VIEW;
    }
}
