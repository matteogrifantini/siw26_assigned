package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.OperazioneNonAutorizzataException;
import it.uniroma3.siw.exception.RecensioneGiaEsistenteException;
import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Transactional(readOnly = true)
    public List<Recensione> findByFilm(Film film) {
        return recensioneRepository.findByFilmWithAutore(film);
    }

    @Transactional(readOnly = true)
    public List<Recensione> findByAutore(Utente autore) {
        return recensioneRepository.findByAutore(autore);
    }

    @Transactional(readOnly = true)
    public Recensione findById(Long id) {
        return recensioneRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Recensione non trovata con id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Recensione> findByFilmAndAutore(Film film, Utente autore) {
        return recensioneRepository.findByFilmAndAutore(film, autore);
    }

    @Transactional(readOnly = true)
    public boolean haGiaRecensito(Film film, Utente autore) {
        return recensioneRepository.existsByFilmAndAutore(film, autore);
    }

    @Transactional
    public Recensione aggiungiRecensione(Long filmId, Utente autore, String testo, Integer voto) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Film non trovato con id: " + filmId));

        if (recensioneRepository.existsByFilmAndAutore(film, autore)) {
            throw new RecensioneGiaEsistenteException("Hai già inserito una recensione per questo film.");
        }

        Recensione recensione = new Recensione(testo, voto, LocalDate.now(), film, autore);
        return recensioneRepository.save(recensione);
    }

    @Transactional
    public Recensione modificaRecensione(Long recensioneId, Utente utenteRichiedente, String nuovoTesto, Integer nuovoVoto) {
        Recensione recensione = findById(recensioneId);

        // Controllo di sicurezza a livello applicativo
        if (!recensione.getAutore().getId().equals(utenteRichiedente.getId())) {
            throw new OperazioneNonAutorizzataException("Non hai i permessi per modificare questa recensione.");
        }

        recensione.setTesto(nuovoTesto);
        recensione.setVoto(nuovoVoto);
        recensione.setData(LocalDate.now()); // aggiorna la data all'ultima modifica
        return recensioneRepository.save(recensione);
    }

    @Transactional
    public void eliminaRecensione(Long recensioneId, Utente utenteRichiedente, boolean isAdmin) {
        Recensione recensione = findById(recensioneId);

        if (!isAdmin && !recensione.getAutore().getId().equals(utenteRichiedente.getId())) {
            throw new OperazioneNonAutorizzataException("Non hai i permessi per cancellare questa recensione.");
        }

        recensioneRepository.delete(recensione);
    }
}
