package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    List<Recensione> findByFilm(Film film);

    @Query("SELECT r FROM Recensione r JOIN FETCH r.autore WHERE r.film = :film ORDER BY r.data DESC")
    List<Recensione> findByFilmWithAutore(@Param("film") Film film);

    List<Recensione> findByAutore(Utente autore);

    Optional<Recensione> findByFilmAndAutore(Film film, Utente autore);

    boolean existsByFilmAndAutore(Film film, Utente autore);
}
