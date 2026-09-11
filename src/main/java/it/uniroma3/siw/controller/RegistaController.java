package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.service.RegistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registi")
public class RegistaController {

    @Autowired
    private RegistaService registaService;

    @GetMapping
    public String getAllRegisti(Model model) {
        model.addAttribute("registi", registaService.findAll());
        return "regista/registi";
    }

    @GetMapping("/{id}")
    public String getRegistaDetails(@PathVariable Long id, Model model) {
        Regista regista = registaService.findById(id);
        model.addAttribute("regista", regista);
        return "regista/regista";
    }
}
