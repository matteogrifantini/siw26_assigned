package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.ProiezioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/festivals")
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private ProiezioneService proiezioneService;

    @GetMapping
    public String getAllFestivals(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        return "festival/festivals";
    }

    @GetMapping("/{id}")
    public String getFestivalDetails(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id);
        List<Film> filmList = filmService.findByFestival(festival);
        List<Proiezione> proiezioni = proiezioneService.findByFestival(festival);

        model.addAttribute("festival", festival);
        model.addAttribute("filmList", filmList);
        model.addAttribute("proiezioni", proiezioni);

        return "festival/festival";
    }
}
