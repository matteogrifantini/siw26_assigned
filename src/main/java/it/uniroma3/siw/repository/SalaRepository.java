package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findAllByOrderByNomeAsc();

    boolean existsByNomeAndIndirizzo(String nome, String indirizzo);
}
