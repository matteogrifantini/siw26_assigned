package it.uniroma3.siw.controller;

import it.uniroma3.siw.dto.FestivalDto;
import it.uniroma3.siw.dto.ProiezioneDto;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.ProiezioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private ProiezioneService proiezioneService;

    @GetMapping("/festivals")
    public List<FestivalDto> getAllFestivals() {
        List<Festival> festivals = festivalService.findAll();
        List<FestivalDto> dtos = new ArrayList<>();
        for (Festival f : festivals) {
            dtos.add(new FestivalDto(f));
        }
        return dtos;
    }

    @GetMapping("/screenings")
    public List<ProiezioneDto> getAllScreenings(@RequestParam(required = false) Long festivalId) {
        List<Proiezione> proiezioni;
        if (festivalId != null) {
            Festival festival = festivalService.findById(festivalId);
            proiezioni = (festival != null) ? proiezioneService.findByFestival(festival) : new ArrayList<>();
        } else {
            proiezioni = proiezioneService.findAll();
        }

        List<ProiezioneDto> dtos = new ArrayList<>();
        for (Proiezione p : proiezioni) {
            dtos.add(new ProiezioneDto(p));
        }
        return dtos;
    }
}
