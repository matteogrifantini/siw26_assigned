package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<Credentials> getCredentials(String username) {
        return credentialsRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return credentialsRepository.existsByUsername(username);
    }

    @Transactional
    public Credentials registraUtente(Utente utente, Credentials credentials) {
        // Cifratura della password in chiaro con algoritmo BCrypt
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        // Assegnazione del ruolo predefinito di utente semplice
        if (credentials.getRuolo() == null || credentials.getRuolo().isBlank()) {
            credentials.setRuolo(Credentials.DEFAULT_ROLE);
        }
        // Associazione bidirezionale tra credenziali e anagrafica utente
        credentials.setUtente(utente);
        return credentialsRepository.save(credentials);
    }

    @Transactional
    public Credentials save(Credentials credentials) {
        return credentialsRepository.save(credentials);
    }
}
