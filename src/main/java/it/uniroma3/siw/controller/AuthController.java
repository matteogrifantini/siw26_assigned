package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UtenteService utenteService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("utente", new Utente());
        model.addAttribute("credentials", new Credentials());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("utente") Utente utente,
            BindingResult utenteBindingResult,
            @Valid @ModelAttribute("credentials") Credentials credentials,
            BindingResult credentialsBindingResult,
            Model model) {

        // Controllo unicità email
        if (utenteService.findByEmail(utente.getEmail()).isPresent()) {
            utenteBindingResult.rejectValue("email", "error.utente", "Questa email è già associata a un account.");
        }

        // Controllo unicità username
        if (credentialsService.existsByUsername(credentials.getUsername())) {
            credentialsBindingResult.rejectValue("username", "error.credentials", "Questo username è già in uso. Scegline un altro.");
        }

        // Se sono presenti errori di validazione, ricarica la vista con i messaggi di errore
        if (utenteBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
            return "register";
        }

        // Registrazione atomica con password cifrata
        credentialsService.registraUtente(utente, credentials);

        return "redirect:/login?registered=true";
    }
}
