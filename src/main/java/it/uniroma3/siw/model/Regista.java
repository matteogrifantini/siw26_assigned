package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "registi")
public class Regista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome del regista è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome del regista è obbligatorio")
    @Column(nullable = false)
    private String cognome;

    @NotNull(message = "La data di nascita è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dataNascita;

    @NotBlank(message = "La nazionalità è obbligatoria")
    @Column(nullable = false)
    private String nazionalita;

    @OneToMany(mappedBy = "regista", cascade = CascadeType.ALL)
    private List<Film> filmDiretti = new ArrayList<>();

    public Regista() {
    }

    public Regista(String nome, String cognome, LocalDate dataNascita, String nazionalita) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.nazionalita = nazionalita;
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

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita;
    }

    public List<Film> getFilmDiretti() {
        return filmDiretti;
    }

    public void setFilmDiretti(List<Film> filmDiretti) {
        this.filmDiretti = filmDiretti;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Regista regista)) return false;
        return Objects.equals(nome, regista.nome) &&
                Objects.equals(cognome, regista.cognome) &&
                Objects.equals(dataNascita, regista.dataNascita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, dataNascita);
    }

    @Override
    public String toString() {
        return getNomeCompleto() + " (" + nazionalita + ")";
    }
}
