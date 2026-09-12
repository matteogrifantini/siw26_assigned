package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.exception.SalaNonDisponibileException;
import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.ProiezioneRepository;
import it.uniroma3.siw.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ProiezioneService {

    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<Proiezione> findAll() {
        return proiezioneRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Proiezione findById(Long id) {
        return proiezioneRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Proiezione non trovata con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFestival(Festival festival) {
        return proiezioneRepository.findByFestivalWithDetails(festival);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByData(LocalDate data) {
        return proiezioneRepository.findByDataOrderByOraAsc(data);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Proiezione programmaProiezione(Long festivalId, Long filmId, Long salaId, LocalDate data, LocalTime ora) {
        // Passo 1: Recupero del festival
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Festival non trovato con id: " + festivalId));

        // Passo 2: Recupero del film
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Film non trovato con id: " + filmId));

        // Passo 3: Recupero della sala
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Sala non trovata con id: " + salaId));

        // Calcolo dell'intervallo temporale della nuova proiezione
        LocalTime oraInizioNuova = ora;
        LocalTime oraFineNuova = oraInizioNuova.plusMinutes(film.getDurata());

        // Passo 4: Verifica disponibilità della sala (nessuna sovrapposizione)
        verificaDisponibilitaSala(sala, data, oraInizioNuova, oraFineNuova, null);

        // Passo 5: Creazione della proiezione
        Proiezione nuovaProiezione = new Proiezione(data, ora, StatoProiezione.SCHEDULED, festival, film, sala);

        // Passo 6: Aggiornamento delle associazioni
        // Se il film non era ancora associato al festival, viene associato automaticamente
        if (!festival.getFilmPartecipanti().contains(film)) {
            festival.addFilm(film);
            festivalRepository.save(festival);
        }

        return proiezioneRepository.save(nuovaProiezione);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Proiezione modificaProiezione(Long proiezioneId, Long salaId, LocalDate data, LocalTime ora, StatoProiezione stato) {
        Proiezione proiezione = findById(proiezioneId);
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Sala non trovata con id: " + salaId));

        LocalTime oraInizio = ora;
        LocalTime oraFine = oraInizio.plusMinutes(proiezione.getFilm().getDurata());

        // Verifica disponibilità sala escludendo la proiezione corrente dal controllo
        verificaDisponibilitaSala(sala, data, oraInizio, oraFine, proiezioneId);

        proiezione.setSala(sala);
        proiezione.setData(data);
        proiezione.setOra(ora);
        if (stato != null) {
            proiezione.setStato(stato);
        }

        return proiezioneRepository.save(proiezione);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!proiezioneRepository.existsById(id)) {
            throw new RisorsaNonTrovataException("Impossibile cancellare: proiezione non trovata con id: " + id);
        }
        proiezioneRepository.deleteById(id);
    }

    // Requisito Sezione 4.3 & 7: Controllo consistenza disponibilità sala (blocco sovrapposizione orari)
    private void verificaDisponibilitaSala(Sala sala, LocalDate data, LocalTime inizio, LocalTime fine, Long proiezioneDaIgnorareId) {
        List<Proiezione> proiezioniGiorno = proiezioneRepository.findBySalaAndData(sala, data);

        for (Proiezione p : proiezioniGiorno) {
            // Ignora la proiezione stessa se stiamo effettuando una modifica
            if (proiezioneDaIgnorareId != null && p.getId().equals(proiezioneDaIgnorareId)) {
                continue;
            }
            // Ignora le proiezioni annullate
            if (p.getStato() == StatoProiezione.CANCELLED) {
                continue;
            }

            LocalTime pInizio = p.getOra();
            LocalTime pFine = p.getOraFine();

            // Controllo sovrapposizione
            boolean sovrapposizione = inizio.isBefore(pFine) && fine.isAfter(pInizio);
            if (sovrapposizione) {
                throw new SalaNonDisponibileException(
                        "Conflitto di programmazione: la sala '" + sala.getNome() +
                        "' è già occupata per la proiezione del film '" + p.getFilm().getTitolo() +
                        "' dalle ore " + pInizio + " alle " + pFine + ".");
            }
        }
    }
}
