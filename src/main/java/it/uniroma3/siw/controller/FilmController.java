package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.RecensioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/film")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping
    public String getAllFilm(@RequestParam(required = false) String q, Model model) {
        List<Film> films = filmService.searchByTitolo(q);
        model.addAttribute("films", films);
        model.addAttribute("query", q);
        return "film/films";
    }

    @GetMapping("/{id}")
    public String getFilmDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Film film = filmService.findById(id);
        List<Recensione> recensioni = recensioneService.findByFilm(film);

        model.addAttribute("film", film);
        model.addAttribute("recensioni", recensioni);

        // Verifica dello stato dell'utente autenticato per la recensione
        boolean haGiaRecensito = false;
        Recensione propriaRecensione = null;

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String username = authentication.getName();
            Optional<Credentials> credsOpt = credentialsService.getCredentials(username);
            if (credsOpt.isPresent()) {
                Utente utente = credsOpt.get().getUtente();
                Optional<Recensione> recOpt = recensioneService.findByFilmAndAutore(film, utente);
                if (recOpt.isPresent()) {
                    haGiaRecensito = true;
                    propriaRecensione = recOpt.get();
                }
                model.addAttribute("utenteCorrente", utente);
            }
        }

        model.addAttribute("haGiaRecensito", haGiaRecensito);
        model.addAttribute("propriaRecensione", propriaRecensione);
        model.addAttribute("nuovaRecensione", new Recensione());

        return "film/film";
    }
}
