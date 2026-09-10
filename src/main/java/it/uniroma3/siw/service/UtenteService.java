package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Transactional(readOnly = true)
    public Optional<Utente> findById(Long id) {
        return utenteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    @Transactional
    public Utente save(Utente utente) {
        return utenteRepository.save(utente);
    }

    @Transactional(readOnly = true)
    public List<Utente> findAll() {
        return utenteRepository.findAll();
    }
}
