package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Regista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistaRepository extends JpaRepository<Regista, Long> {

    List<Regista> findAllByOrderByCognomeAscNomeAsc();

    boolean existsByNomeAndCognomeAndDataNascita(String nome, String cognome, LocalDate dataNascita);
}
