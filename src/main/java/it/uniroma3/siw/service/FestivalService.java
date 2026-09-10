package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FestivalService {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Transactional(readOnly = true)
    public List<Festival> findAll() {
        return festivalRepository.findAllByOrderByAnnoDesc();
    }

    @Transactional(readOnly = true)
    public Festival findById(Long id) {
        return festivalRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Festival non trovato con id: " + id));
    }

    @Transactional
    public Festival save(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!festivalRepository.existsById(id)) {
            throw new RisorsaNonTrovataException("Impossibile cancellare: festival non trovato con id: " + id);
        }
        festivalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Festival festival) {
        return festivalRepository.existsByNomeAndAnno(festival.getNome(), festival.getAnno());
    }

    @Transactional
    public Festival associaFilm(Long festivalId, Long filmId) {
        Festival festival = findById(festivalId);
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Film non trovato con id: " + filmId));

        festival.addFilm(film);
        return festivalRepository.save(festival);
    }

    @Transactional
    public Festival rimuoviFilm(Long festivalId, Long filmId) {
        Festival festival = findById(festivalId);
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Film non trovato con id: " + filmId));

        festival.removeFilm(film);
        return festivalRepository.save(festival);
    }
}
