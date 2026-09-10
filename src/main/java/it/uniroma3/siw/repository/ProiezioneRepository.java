package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

    List<Proiezione> findByFestivalOrderByDataAscOraAsc(Festival festival);

    List<Proiezione> findByFilmOrderByDataAscOraAsc(Film film);

    List<Proiezione> findByDataOrderByOraAsc(LocalDate data);

    List<Proiezione> findBySalaAndData(Sala sala, LocalDate data);

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.festival JOIN FETCH p.film f JOIN FETCH f.regista JOIN FETCH p.sala ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findAllWithDetails();

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film f JOIN FETCH f.regista JOIN FETCH p.sala WHERE p.festival = :festival ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findByFestivalWithDetails(@Param("festival") Festival festival);
}
