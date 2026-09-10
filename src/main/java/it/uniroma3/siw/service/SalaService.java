package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.RisorsaNonTrovataException;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {

    @Autowired
    private SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<Sala> findAll() {
        return salaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Sala findById(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Sala non trovata con id: " + id));
    }

    @Transactional
    public Sala save(Sala sala) {
        return salaRepository.save(sala);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new RisorsaNonTrovataException("Impossibile cancellare: sala non trovata con id: " + id);
        }
        salaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Sala sala) {
        return salaRepository.existsByNomeAndIndirizzo(sala.getNome(), sala.getIndirizzo());
    }
}
