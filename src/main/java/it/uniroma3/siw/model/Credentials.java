package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "credentials")
public class Credentials {

    public static final String DEFAULT_ROLE = "ROLE_USER";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Lo username è obbligatorio")
    @Size(min = 3, max = 30, message = "Lo username deve avere tra 3 e 30 caratteri")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String ruolo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    public Credentials() {
        this.ruolo = DEFAULT_ROLE;
    }

    public Credentials(String username, String password, String ruolo, Utente utente) {
        this.username = username;
        this.password = password;
        this.ruolo = ruolo != null ? ruolo : DEFAULT_ROLE;
        this.utente = utente;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    // equals e hashCode basati su username univoco
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credentials that)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
