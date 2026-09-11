package it.uniroma3.siw.controller;

import it.uniroma3.siw.service.FestivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProiezioneController {

    @Autowired
    private FestivalService festivalService;

    @GetMapping("/proiezioni")
    public String getProgrammaProiezioni(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        return "proiezione/proiezioni";
    }

    @GetMapping({"/programma", "/programma-interattivo"})
    public String redirectProgramma() {
        return "redirect:/proiezioni";
    }
}
