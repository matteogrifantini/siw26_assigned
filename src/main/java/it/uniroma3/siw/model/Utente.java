package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Column(nullable = false)
    private String cognome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "autore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni = new ArrayList<>();

    // Costruttore vuoto richiesto da JPA
    public Utente() {
    }

    public Utente(String nome, String cognome, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    // equals e hashCode compatibili con proxy Hibernate usando i getter
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Utente utente)) return false;
        if (this.getId() != null && utente.getId() != null) {
            return Objects.equals(this.getId(), utente.getId());
        }
        return Objects.equals(this.getEmail(), utente.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId() != null ? getId() : getEmail());
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + email + ")";
    }
}
