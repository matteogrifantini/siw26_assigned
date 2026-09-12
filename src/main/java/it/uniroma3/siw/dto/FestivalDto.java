package it.uniroma3.siw.dto;

import it.uniroma3.siw.model.Festival;

public class FestivalDto {
    private Long id;
    private String nome;
    private Integer anno;
    private String citta;
    private String dataInizio;
    private String dataFine;
    private String descrizione;
    private String immagineUrl;
    private int numeroFilm;
    private int numeroProiezioni;

    public FestivalDto() {
    }

    public FestivalDto(Festival f) {
        if (f != null) {
            this.id = f.getId();
            this.nome = f.getNome();
            this.anno = f.getAnno();
            this.citta = f.getCitta();
            this.dataInizio = f.getDataInizio() != null ? f.getDataInizio().toString() : "";
            this.dataFine = f.getDataFine() != null ? f.getDataFine().toString() : "";
            this.descrizione = f.getDescrizione();
            this.immagineUrl = f.getImmagineUrl();
            this.numeroFilm = f.getFilmPartecipanti() != null ? f.getFilmPartecipanti().size() : 0;
            this.numeroProiezioni = f.getProiezioni() != null ? f.getProiezioni().size() : 0;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getDataInizio() { return dataInizio; }
    public void setDataInizio(String dataInizio) { this.dataInizio = dataInizio; }

    public String getDataFine() { return dataFine; }
    public void setDataFine(String dataFine) { this.dataFine = dataFine; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String immagineUrl) { this.immagineUrl = immagineUrl; }

    public int getNumeroFilm() { return numeroFilm; }
    public void setNumeroFilm(int numeroFilm) { this.numeroFilm = numeroFilm; }

    public int getNumeroProiezioni() { return numeroProiezioni; }
    public void setNumeroProiezioni(int numeroProiezioni) { this.numeroProiezioni = numeroProiezioni; }
}
