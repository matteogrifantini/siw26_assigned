package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il titolo è obbligatorio")
    @Column(nullable = false)
    private String titolo;

    @NotNull(message = "L'anno di uscita è obbligatorio")
    @Min(value = 1895, message = "L'anno non può essere antecedente alla nascita del cinema (1895)")
    @Max(value = 2100, message = "Anno non valido")
    @Column(nullable = false)
    private Integer anno;

    @NotNull(message = "La durata in minuti è obbligatoria")
    @Min(value = 1, message = "La durata deve essere di almeno 1 minuto")
    @Column(nullable = false)
    private Integer durata; // Durata espressa in minuti

    @NotBlank(message = "Il genere è obbligatorio")
    @Column(nullable = false)
    private String genere;

    @NotBlank(message = "Il paese di produzione è obbligatorio")
    @Column(nullable = false)
    private String paeseProduzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regista_id")
    private Regista regista;

    @ManyToMany(mappedBy = "filmPartecipanti")
    private List<Festival> festival = new ArrayList<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proiezione> proiezioni = new ArrayList<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni = new ArrayList<>();

    public Film() {
    }

    public Film(String titolo, Integer anno, Integer durata, String genere, String paeseProduzione, Regista regista) {
        this.titolo = titolo;
        this.anno = anno;
        this.durata = durata;
        this.genere = genere;
        this.paeseProduzione = paeseProduzione;
        this.regista = regista;
    }

    // Calcolo della media dei voti delle recensioni
    public Double getMediaVoti() {
        if (recensioni == null || recensioni.isEmpty()) {
            return 0.0;
        }
        double somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getVoto();
        }
        return Math.round((somma / recensioni.size()) * 10.0) / 10.0;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getDurata() {
        return durata;
    }

    public void setDurata(Integer durata) {
        this.durata = durata;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getPaeseProduzione() {
        return paeseProduzione;
    }

    public void setPaeseProduzione(String paeseProduzione) {
        this.paeseProduzione = paeseProduzione;
    }

    public Regista getRegista() {
        return regista;
    }

    public void setRegista(Regista regista) {
        this.regista = regista;
    }

    public List<Festival> getFestival() {
        return festival;
    }

    public void setFestival(List<Festival> festival) {
        this.festival = festival;
    }

    public List<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(List<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film film)) return false;
        return Objects.equals(titolo, film.titolo) && Objects.equals(anno, film.anno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo, anno);
    }

    @Override
    public String toString() {
        return titolo + " (" + anno + ", " + durata + " min)";
    }
}
