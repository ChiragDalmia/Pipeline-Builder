package ca.communitycentre.workshops.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ca.communitycentre.workshops.service.WorkshopService;

@Controller
public class OverviewController {

    private final WorkshopService workshopService;

    public OverviewController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/overview";
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        model.addAttribute("rows", workshopService.getOverview());
        return "overview";
    }
}
