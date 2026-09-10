package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "festival")
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome del festival è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "L'anno di edizione è obbligatorio")
    @Min(value = 1900, message = "Anno non valido")
    @Max(value = 2100, message = "Anno non valido")
    @Column(nullable = false)
    private Integer anno;

    @NotBlank(message = "La città è obbligatoria")
    @Column(nullable = false)
    private String citta;

    @NotNull(message = "La data di inizio è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(length = 3000)
    private String descrizione;

    @Column(length = 500)
    private String immagineUrl;

    @ManyToMany
    @JoinTable(
        name = "festival_film",
        joinColumns = @JoinColumn(name = "festival_id"),
        inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> filmPartecipanti = new ArrayList<>();

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proiezione> proiezioni = new ArrayList<>();

    public Festival() {
    }

    public Festival(String nome, Integer anno, String citta, LocalDate dataInizio, LocalDate dataFine, String descrizione) {
        this.nome = nome;
        this.anno = anno;
        this.citta = citta;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.descrizione = descrizione;
    }

    public Festival(String nome, Integer anno, String citta, LocalDate dataInizio, LocalDate dataFine, String descrizione, String immagineUrl) {
        this.nome = nome;
        this.anno = anno;
        this.citta = citta;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.descrizione = descrizione;
        this.immagineUrl = immagineUrl;
    }

    // Metodi di utilità per associare/dissociare film
    public void addFilm(Film film) {
        if (!this.filmPartecipanti.contains(film)) {
            this.filmPartecipanti.add(film);
            film.getFestival().add(this);
        }
    }

    public void removeFilm(Film film) {
        if (this.filmPartecipanti.contains(film)) {
            this.filmPartecipanti.remove(film);
            film.getFestival().remove(this);
        }
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

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagineUrl() {
        return immagineUrl;
    }

    public void setImmagineUrl(String immagineUrl) {
        this.immagineUrl = immagineUrl;
    }

    public List<Film> getFilmPartecipanti() {
        return filmPartecipanti;
    }

    public void setFilmPartecipanti(List<Film> filmPartecipanti) {
        this.filmPartecipanti = filmPartecipanti;
    }

    public List<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(List<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Festival festival)) return false;
        return Objects.equals(nome, festival.nome) && Objects.equals(anno, festival.anno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, anno);
    }

    @Override
    public String toString() {
        return nome + " " + anno + " (" + citta + ")";
    }
}
