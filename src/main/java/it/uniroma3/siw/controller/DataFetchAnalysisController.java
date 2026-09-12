package it.uniroma3.siw.controller;

import it.uniroma3.siw.analysis.DataFetchAnalysisService;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.service.FestivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DataFetchAnalysisController {

    @Autowired
    private DataFetchAnalysisService analysisService;

    @Autowired
    private FestivalService festivalService;

    @GetMapping("/analisi-prestazioni")
    public String showAnalysisPage(@RequestParam(required = false) Long festivalId, Model model) {
        List<Festival> festivals = festivalService.findAll();
        model.addAttribute("festivals", festivals);

        if (festivalId == null && !festivals.isEmpty()) {
            festivalId = festivals.get(0).getId();
        }

        if (festivalId != null) {
            Festival selectedFestival = festivalService.findById(festivalId);
            model.addAttribute("selectedFestival", selectedFestival);

            DataFetchAnalysisService.RisultatoAnalisi risultato = analysisService.eseguiBenchmark(festivalId);
            model.addAttribute("risultato", risultato);
        }

        return "analisi/prestazioni";
    }
}
