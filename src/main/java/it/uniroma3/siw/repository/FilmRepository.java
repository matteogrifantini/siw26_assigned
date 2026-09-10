package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    boolean existsByTitoloAndAnno(String titolo, Integer anno);

    List<Film> findByTitoloContainingIgnoreCase(String titolo);

    List<Film> findByGenereIgnoreCase(String genere);

    @Query("SELECT f FROM Film f JOIN f.festival fest WHERE fest = :festival")
    List<Film> findByFestival(@Param("festival") Festival festival);

    @Query("SELECT DISTINCT f FROM Film f JOIN f.festival fest JOIN FETCH f.regista WHERE fest = :festival")
    List<Film> findByFestivalWithRegistaJoinFetch(@Param("festival") Festival festival);

    @Query("SELECT DISTINCT f FROM Film f JOIN FETCH f.regista")
    List<Film> findAllWithRegistaJoinFetch();
}
