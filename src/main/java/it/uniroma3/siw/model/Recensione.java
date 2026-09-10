package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
    name = "recensioni",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_recensione_film_autore",
        columnNames = {"film_id", "autore_id"}
    )
)
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il testo della recensione non può essere vuoto")
    @Column(nullable = false, length = 2500)
    private String testo;

    @NotNull(message = "Il voto è obbligatorio")
    @Min(value = 1, message = "Il voto minimo è 1")
    @Max(value = 5, message = "Il voto massimo è 5")
    @Column(nullable = false)
    private Integer voto; // Voto espresso da 1 a 5 stelle

    @NotNull(message = "La data è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autore_id", nullable = false)
    private Utente autore;

    public Recensione() {
        this.data = LocalDate.now();
    }

    public Recensione(String testo, Integer voto, LocalDate data, Film film, Utente autore) {
        this.testo = testo;
        this.voto = voto;
        this.data = data != null ? data : LocalDate.now();
        this.film = film;
        this.autore = autore;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recensione that)) return false;
        return Objects.equals(film, that.film) && Objects.equals(autore, that.autore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(film, autore);
    }

    @Override
    public String toString() {
        return "Recensione: voto " + voto + "/5 da " + (autore != null ? autore.getNome() : "") + " per " + (film != null ? film.getTitolo() : "");
    }
}
