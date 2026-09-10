package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.ProiezioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private ProiezioneService proiezioneService;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("filmList", filmService.findAll());
        model.addAttribute("proiezioni", proiezioneService.findAll());
        return "index";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(Credentials.ADMIN_ROLE));
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/index";
    }
}
