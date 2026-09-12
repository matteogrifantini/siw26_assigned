package it.uniroma3.siw;

import it.uniroma3.siw.exception.SalaNonDisponibileException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.SalaRepository;
import it.uniroma3.siw.service.ProiezioneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProiezioneConsistenzaTest {

    @Autowired
    private ProiezioneService proiezioneService;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Test
    @DisplayName("Verifica divieto di sovrapposizione oraria per proiezioni nella stessa sala")
    void testDivietoSovrapposizioneOrariaStessaSala() {
        Festival festival = festivalRepository.findAll().get(0);
        Film film = filmRepository.findAll().get(0); // durata es. 180 min
        Sala sala = salaRepository.findAll().get(0);

        LocalDate dataTest = LocalDate.of(2027, 1, 10);
        LocalTime oraTest = LocalTime.of(15, 0);

        // 1. Programmazione della prima proiezione (dalle 15:00 alle 18:00)
        proiezioneService.programmaProiezione(festival.getId(), film.getId(), sala.getId(), dataTest, oraTest);

        // 2. Tentativo di programmare una seconda proiezione nella stessa sala che si sovrappone (es. alle 16:00)
        LocalTime oraSovrapposta = LocalTime.of(16, 0);

        assertThrows(SalaNonDisponibileException.class, () -> {
            proiezioneService.programmaProiezione(festival.getId(), film.getId(), sala.getId(), dataTest, oraSovrapposta);
        }, "Il sistema deve impedire la programmazione di due proiezioni sovrapposte nella stessa sala");
    }
}
