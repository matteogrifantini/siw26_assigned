package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.RegistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private RegistaRepository registaRepository;

    @Transactional(readOnly = true)
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Film findById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Film non trovato con id: " + id));
    }

    @Transactional
    public Film salvaConRegista(Film film, Long registaId) {
        if (registaId != null) {
            Regista regista = registaRepository.findById(registaId)
                    .orElseThrow(() -> new RisorsaNonTrovataException("Regista non trovato con id: " + registaId));
            film.setRegista(regista);
        }
        return filmRepository.save(film);
    }

    @Transactional
    public Film save(Film film) {
        return filmRepository.save(film);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new RisorsaNonTrovataException("Impossibile cancellare: film non trovato con id: " + id);
        }
        filmRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Film> findByFestival(Festival festival) {
        return filmRepository.findByFestival(festival);
    }

    @Transactional(readOnly = true)
    public List<Film> findByFestivalWithRegista(Festival festival) {
        return filmRepository.findByFestivalWithRegistaJoinFetch(festival);
    }

    @Transactional(readOnly = true)
    public List<Film> searchByTitolo(String query) {
        if (query == null || query.isBlank()) {
            return filmRepository.findAll();
        }
        return filmRepository.findByTitoloContainingIgnoreCase(query);
    }

    @Transactional(readOnly = true)
    public boolean exists(Film film) {
        return filmRepository.existsByTitoloAndAnno(film.getTitolo(), film.getAnno());
    }
}
