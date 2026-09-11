package it.uniroma3.siw.controller;

import it.uniroma3.siw.exception.SalaNonDisponibileException;
import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private RegistaService registaService;

    @Autowired
    private SalaService salaService;

    @Autowired
    private ProiezioneService proiezioneService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("registi", registaService.findAll());
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("proiezioni", proiezioneService.findAll());
        return "admin/dashboard";
    }

    // SEZIONE GESTIONE FESTIVAL

    @GetMapping("/festivals/nuovo")
    public String formNuovoFestival(Model model) {
        model.addAttribute("festival", new Festival());
        return "admin/formFestival";
    }

    @PostMapping("/festivals/salva")
    public String salvaFestival(
            @Valid @ModelAttribute("festival") Festival festival,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (festival.getId() == null && festivalService.exists(festival)) {
            bindingResult.rejectValue("nome", "error.festival", "Esiste già un festival con questo nome e anno.");
        }

        if (bindingResult.hasErrors()) {
            return "admin/formFestival";
        }

        festivalService.save(festival);
        redirectAttributes.addFlashAttribute("successMessage", "Festival salvato con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/festivals/modifica/{id}")
    public String formModificaFestival(@PathVariable Long id, Model model) {
        model.addAttribute("festival", festivalService.findById(id));
        return "admin/formFestival";
    }

    @PostMapping("/festivals/elimina/{id}")
    public String eliminaFestival(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        festivalService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Festival eliminato con successo.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/festivals/{id}/associa-film")
    public String formAssociaFilm(@PathVariable Long id, Model model) {
        Festival festival = festivalService.findById(id);
        List<Film> tuttiFilm = filmService.findAll();
        // Escludiamo i film già presenti nel festival
        tuttiFilm.removeAll(festival.getFilmPartecipanti());

        model.addAttribute("festival", festival);
        model.addAttribute("filmDisponibili", tuttiFilm);
        return "admin/associaFilmFestival";
    }

    @PostMapping("/festivals/{festivalId}/associa-film")
    public String associaFilmAFestival(
            @PathVariable Long festivalId,
            @RequestParam Long filmId,
            RedirectAttributes redirectAttributes) {

        festivalService.associaFilm(festivalId, filmId);
        redirectAttributes.addFlashAttribute("successMessage", "Film associato al festival con successo!");
        return "redirect:/admin/festivals/" + festivalId + "/associa-film";
    }

    @PostMapping("/festivals/{festivalId}/rimuovi-film/{filmId}")
    public String rimuoviFilmDaFestival(
            @PathVariable Long festivalId,
            @PathVariable Long filmId,
            RedirectAttributes redirectAttributes) {

        festivalService.rimuoviFilm(festivalId, filmId);
        redirectAttributes.addFlashAttribute("successMessage", "Film rimosso dal festival.");
        return "redirect:/admin/festivals/" + festivalId + "/associa-film";
    }

    // SEZIONE GESTIONE FILM

    @GetMapping("/film/nuovo")
    public String formNuovoFilm(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("registi", registaService.findAll());
        return "admin/formFilm";
    }

    @PostMapping("/film/salva")
    public String salvaFilm(
            @Valid @ModelAttribute("film") Film film,
            BindingResult bindingResult,
            @RequestParam(required = false) Long registaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (registaId == null) {
            bindingResult.rejectValue("regista", "error.film", "Selezionare un regista per il film.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", registaService.findAll());
            return "admin/formFilm";
        }

        filmService.salvaConRegista(film, registaId);
        redirectAttributes.addFlashAttribute("successMessage", "Film salvato con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/film/modifica/{id}")
    public String formModificaFilm(@PathVariable Long id, Model model) {
        Film film = filmService.findById(id);
        model.addAttribute("film", film);
        model.addAttribute("registi", registaService.findAll());
        return "admin/formFilm";
    }

    @PostMapping("/film/elimina/{id}")
    public String eliminaFilm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        filmService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Film eliminato con successo.");
        return "redirect:/admin/dashboard";
    }

    // SEZIONE GESTIONE REGISTI

    @GetMapping("/registi/nuovo")
    public String formNuovoRegista(Model model) {
        model.addAttribute("regista", new Regista());
        return "admin/formRegista";
    }

    @PostMapping("/registi/salva")
    public String salvaRegista(
            @Valid @ModelAttribute("regista") Regista regista,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (regista.getId() == null && registaService.exists(regista)) {
            bindingResult.rejectValue("nome", "error.regista", "Un regista con questo nome, cognome e data di nascita è già presente.");
        }

        if (bindingResult.hasErrors()) {
            return "admin/formRegista";
        }

        registaService.save(regista);
        redirectAttributes.addFlashAttribute("successMessage", "Regista salvato con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/registi/modifica/{id}")
    public String formModificaRegista(@PathVariable Long id, Model model) {
        model.addAttribute("regista", registaService.findById(id));
        return "admin/formRegista";
    }

    @PostMapping("/registi/elimina/{id}")
    public String eliminaRegista(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        registaService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Regista eliminato con successo.");
        return "redirect:/admin/dashboard";
    }

    // SEZIONE GESTIONE SALE

    @GetMapping("/sale/nuova")
    public String formNuovaSala(Model model) {
        model.addAttribute("sala", new Sala());
        return "admin/formSala";
    }

    @PostMapping("/sale/salva")
    public String salvaSala(
            @Valid @ModelAttribute("sala") Sala sala,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (sala.getId() == null && salaService.exists(sala)) {
            bindingResult.rejectValue("nome", "error.sala", "Esiste già una sala con questo nome a questo indirizzo.");
        }

        if (bindingResult.hasErrors()) {
            return "admin/formSala";
        }

        salaService.save(sala);
        redirectAttributes.addFlashAttribute("successMessage", "Sala salvata con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/sale/modifica/{id}")
    public String formModificaSala(@PathVariable Long id, Model model) {
        model.addAttribute("sala", salaService.findById(id));
        return "admin/formSala";
    }

    @PostMapping("/sale/elimina/{id}")
    public String eliminaSala(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        salaService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sala eliminata con successo.");
        return "redirect:/admin/dashboard";
    }

    // SEZIONE PROGRAMMAZIONE PROIEZIONI

    @GetMapping("/proiezioni/nuova")
    public String formNuovaProiezione(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("sale", salaService.findAll());
        return "admin/formProiezione";
    }

    @PostMapping("/proiezioni/salva")
    public String salvaProiezione(
            @RequestParam Long festivalId,
            @RequestParam Long filmId,
            @RequestParam Long salaId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime ora,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            proiezioneService.programmaProiezione(festivalId, filmId, salaId, data, ora);
            redirectAttributes.addFlashAttribute("successMessage", "Proiezione programmata con successo!");
            return "redirect:/admin/dashboard";
        } catch (SalaNonDisponibileException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("festivals", festivalService.findAll());
            model.addAttribute("films", filmService.findAll());
            model.addAttribute("sale", salaService.findAll());
            model.addAttribute("selectedFestivalId", festivalId);
            model.addAttribute("selectedFilmId", filmId);
            model.addAttribute("selectedSalaId", salaId);
            model.addAttribute("selectedData", data);
            model.addAttribute("selectedOra", ora);
            return "admin/formProiezione";
        }
    }

    @PostMapping("/proiezioni/elimina/{id}")
    public String eliminaProiezione(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        proiezioneService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proiezione cancellata con successo.");
        return "redirect:/admin/dashboard";
    }
}
