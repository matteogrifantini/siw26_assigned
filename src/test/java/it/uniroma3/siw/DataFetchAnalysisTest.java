package it.uniroma3.siw;

import it.uniroma3.siw.analysis.DataFetchAnalysisService;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.repository.FestivalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataFetchAnalysisTest {

    @Autowired
    private DataFetchAnalysisService analysisService;

    @Autowired
    private FestivalRepository festivalRepository;

    @Test
    @DisplayName("Confronto sperimentale tra fetch LAZY e JOIN FETCH (Problema N+1 query)")
    void testConfrontoFetchLazyVsJoinFetch() {
        List<Festival> festivals = festivalRepository.findAll();
        assertFalse(festivals.isEmpty(), "Il database deve contenere almeno un festival di test");

        Festival festival = festivals.get(0);
        assertNotNull(festival);

        // Esecuzione del benchmark comparativo
        DataFetchAnalysisService.RisultatoAnalisi risultato = analysisService.eseguiBenchmark(festival.getId());

        assertNotNull(risultato, "Il risultato del benchmark non deve essere nullo");

        // 1. Entrambe le strategie devono operare sullo stesso insieme di dati
        assertEquals(risultato.getStrategiaLazy().getFilmCaricati(),
                     risultato.getStrategiaJoinFetch().getFilmCaricati(),
                     "Il numero di film caricati deve essere identico");

        // 2. Con JOIN FETCH viene eseguita esattamente 1 sola query SQL
        assertEquals(1, risultato.getStrategiaJoinFetch().getQuerySql(),
                     "La strategia JOIN FETCH deve effettuare una sola query SQL");

        // 3. Con la strategia LAZY standard, il numero di query deve essere N+1 (1 per i film + N per i registi)
        int nFilm = risultato.getStrategiaLazy().getFilmCaricati();
        assertEquals(1 + nFilm, risultato.getStrategiaLazy().getQuerySql(),
                     "La strategia LAZY deve effettuare 1 + N query SQL");

        System.out.println("Verifica del test superata con successo!");
    }
}
