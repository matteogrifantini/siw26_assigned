package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FestivalRepository extends JpaRepository<Festival, Long> {

    List<Festival> findAllByOrderByAnnoDesc();

    List<Festival> findAllByOrderByNomeAsc();

    boolean existsByNomeAndAnno(String nome, Integer anno);
}
