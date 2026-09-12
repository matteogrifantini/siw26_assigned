package it.uniroma3.siw.analysis;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataFetchAnalysisService {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FilmRepository filmRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public static class RisultatoStrategia {
        private String nome;
        private int filmCaricati;
        private int querySql;
        private long tempoMs;

        public RisultatoStrategia(String nome, int filmCaricati, int querySql, long tempoMs) {
            this.nome = nome;
            this.filmCaricati = filmCaricati;
            this.querySql = querySql;
            this.tempoMs = tempoMs;
        }

        public String getNome() { return nome; }
        public int getFilmCaricati() { return filmCaricati; }
        public int getQuerySql() { return querySql; }
        public long getTempoMs() { return tempoMs; }
    }

    public static class RisultatoAnalisi {
        private RisultatoStrategia strategiaLazy;
        private RisultatoStrategia strategiaJoinFetch;

        public RisultatoAnalisi(RisultatoStrategia lazy, RisultatoStrategia joinFetch) {
            this.strategiaLazy = lazy;
            this.strategiaJoinFetch = joinFetch;
        }

        public RisultatoStrategia getStrategiaLazy() { return strategiaLazy; }
        public RisultatoStrategia getStrategiaJoinFetch() { return strategiaJoinFetch; }
    }

    @Transactional
    public RisultatoAnalisi eseguiBenchmark(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElse(null);

        if (festival == null) {
            return null;
        }

        System.out.println("\n=======================================================");
        System.out.println("=== Test accesso ai film del festival: " + festival.getNome() + " ===");
        System.out.println("=======================================================");

        // Puliamo la cache di primo livello (EntityManager session) prima del test LAZY
        entityManager.clear();

        // STRATEGIA 1: Accesso con associazione LAZY
        long startLazy = System.currentTimeMillis();

        // Query 1: carica la lista di film appartenenti al festival
        List<Film> filmLazy = filmRepository.findByFestival(festival);
        int filmCount = filmLazy.size();

        // Accesso alle associazioni: per ciascun film accediamo alle proprietà del regista
        // Poiché la relazione ManyToOne è LAZY e la sessione è aperta, Hibernate genera
        // una query SELECT aggiuntiva per ogni film -> Problema N+1 Query!
        for (Film f : filmLazy) {
            if (f.getRegista() != null) {
                // Toccando una proprietà del proxy del regista viene scatenata la query al DB
                String nomeRegista = f.getRegista().getNomeCompleto();
            }
        }

        long tempoLazy = System.currentTimeMillis() - startLazy;
        // In LAZY, se non ci sono registi duplicati in session cache, le query SQL sono 1 (film) + N (registi)
        int queryLazy = 1 + filmCount;

        RisultatoStrategia risLazy = new RisultatoStrategia("LAZY", filmCount, queryLazy, tempoLazy);

        System.out.println("\nStrategia 1: LAZY");
        System.out.println("Film caricati: " + filmCount);
        System.out.println("Query SQL: " + queryLazy);
        System.out.println("Tempo: " + tempoLazy + " ms");

        // Puliamo nuovamente la sessione per non avvantaggiare la seconda strategia con la cache di primo livello
        entityManager.clear();

        // STRATEGIA 2: Accesso con JOIN FETCH (Ottimizzazione)
        long startJoin = System.currentTimeMillis();

        // Esegue una sola query JPQL: SELECT f FROM Film f JOIN FETCH f.regista WHERE ...
        List<Film> filmJoinFetch = filmRepository.findByFestivalWithRegistaJoinFetch(festival);

        for (Film f : filmJoinFetch) {
            if (f.getRegista() != null) {
                // I dati del regista sono già stati recuperati in memoria nella prima query
                String nomeRegista = f.getRegista().getNomeCompleto();
            }
        }

        long tempoJoin = System.currentTimeMillis() - startJoin;
        // Con JOIN FETCH la query è esattamente 1
        int queryJoin = 1;

        RisultatoStrategia risJoin = new RisultatoStrategia("JOIN FETCH", filmJoinFetch.size(), queryJoin, tempoJoin);

        System.out.println("\nStrategia 2: JOIN FETCH");
        System.out.println("Film caricati: " + filmJoinFetch.size());
        System.out.println("Query SQL: " + queryJoin);
        System.out.println("Tempo: " + tempoJoin + " ms");
        System.out.println("=======================================================\n");

        return new RisultatoAnalisi(risLazy, risJoin);
    }
}
