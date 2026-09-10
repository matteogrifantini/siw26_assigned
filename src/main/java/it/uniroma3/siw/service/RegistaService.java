package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.RegistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistaService {

    @Autowired
    private RegistaRepository registaRepository;

    @Transactional(readOnly = true)
    public List<Regista> findAll() {
        return registaRepository.findAllByOrderByCognomeAscNomeAsc();
    }

    @Transactional(readOnly = true)
    public Regista findById(Long id) {
        return registaRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Regista non trovato con id: " + id));
    }

    @Transactional
    public Regista save(Regista regista) {
        return registaRepository.save(regista);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!registaRepository.existsById(id)) {
            throw new RisorsaNonTrovataException("Impossibile cancellare: regista non trovato con id: " + id);
        }
        registaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Regista regista) {
        return registaRepository.existsByNomeAndCognomeAndDataNascita(
                regista.getNome(), regista.getCognome(), regista.getDataNascita());
    }
}
