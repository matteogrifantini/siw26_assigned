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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private CredentialsService credentialsService;

    @PostMapping("/salva/{filmId}")
    public String salvaRecensione(
            @PathVariable Long filmId,
            @RequestParam String testo,
            @RequestParam Integer voto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Utente autore = getUtenteLoggato(authentication);
        if (autore == null) {
            return "redirect:/login";
        }

        try {
            recensioneService.aggiungiRecensione(filmId, autore, testo, voto);
            redirectAttributes.addFlashAttribute("successMessage", "Recensione pubblicata con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/film/" + filmId;
    }

    @GetMapping("/modifica/{id}")
    public String mostraFormModifica(@PathVariable Long id, Model model, Authentication authentication) {
        Recensione recensione = recensioneService.findById(id);
        Utente utente = getUtenteLoggato(authentication);

        if (utente == null || !recensione.getAutore().getId().equals(utente.getId())) {
            return "redirect:/film/" + recensione.getFilm().getId();
        }

        model.addAttribute("recensione", recensione);
        return "recensione/modificaRecensione";
    }

    @PostMapping("/aggiorna/{id}")
    public String aggiornaRecensione(
            @PathVariable Long id,
            @RequestParam String testo,
            @RequestParam Integer voto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Utente utente = getUtenteLoggato(authentication);
        Recensione recensione = recensioneService.findById(id);
        Long filmId = recensione.getFilm().getId();

        try {
            recensioneService.modificaRecensione(id, utente, testo, voto);
            redirectAttributes.addFlashAttribute("successMessage", "Recensione modificata con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/film/" + filmId;
    }

    @PostMapping("/elimina/{id}")
    public String eliminaRecensione(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Utente utente = getUtenteLoggato(authentication);
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Credentials.ADMIN_ROLE));

        Recensione recensione = recensioneService.findById(id);
        Long filmId = recensione.getFilm().getId();

        try {
            recensioneService.eliminaRecensione(id, utente, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Recensione eliminata con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/film/" + filmId;
    }

    private Utente getUtenteLoggato(Authentication authentication) {
        if (authentication == null) return null;
        return credentialsService.getCredentials(authentication.getName())
                .map(Credentials::getUtente)
                .orElse(null);
    }
}
