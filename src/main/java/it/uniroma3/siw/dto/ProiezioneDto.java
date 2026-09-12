package it.uniroma3.siw.dto;

import it.uniroma3.siw.model.Proiezione;

public class ProiezioneDto {
    private Long id;
    private String data;
    private String ora;
    private String oraFine;
    private String stato;
    private String statoEtichetta;
    private Long festivalId;
    private String festivalNome;
    private Long filmId;
    private String filmTitolo;
    private Integer filmDurata;
    private String filmGenere;
    private String registaNome;
    private Long salaId;
    private String salaNome;
    private String salaIndirizzo;
    private String filmLocandinaUrl;

    public ProiezioneDto() {
    }

    public ProiezioneDto(Proiezione p) {
        if (p != null) {
            this.id = p.getId();
            this.data = p.getData() != null ? p.getData().toString() : "";
            this.ora = p.getOra() != null ? p.getOra().toString() : "";
            this.oraFine = p.getOraFine() != null ? p.getOraFine().toString() : "";
            this.stato = p.getStato() != null ? p.getStato().name() : "";
            this.statoEtichetta = p.getStato() != null ? p.getStato().getEtichetta() : "";

            if (p.getFestival() != null) {
                this.festivalId = p.getFestival().getId();
                this.festivalNome = p.getFestival().getNome();
            }

            if (p.getFilm() != null) {
                this.filmId = p.getFilm().getId();
                this.filmTitolo = p.getFilm().getTitolo();
                this.filmDurata = p.getFilm().getDurata();
                this.filmGenere = p.getFilm().getGenere();
                this.filmLocandinaUrl = p.getFilm().getLocandinaUrl();
                if (p.getFilm().getRegista() != null) {
                    this.registaNome = p.getFilm().getRegista().getNomeCompleto();
                }
            }

            if (p.getSala() != null) {
                this.salaId = p.getSala().getId();
                this.salaNome = p.getSala().getNome();
                this.salaIndirizzo = p.getSala().getIndirizzo();
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getOra() { return ora; }
    public void setOra(String ora) { this.ora = ora; }

    public String getOraFine() { return oraFine; }
    public void setOraFine(String oraFine) { this.oraFine = oraFine; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getStatoEtichetta() { return statoEtichetta; }
    public void setStatoEtichetta(String statoEtichetta) { this.statoEtichetta = statoEtichetta; }

    public Long getFestivalId() { return festivalId; }
    public void setFestivalId(Long festivalId) { this.festivalId = festivalId; }

    public String getFestivalNome() { return festivalNome; }
    public void setFestivalNome(String festivalNome) { this.festivalNome = festivalNome; }

    public Long getFilmId() { return filmId; }
    public void setFilmId(Long filmId) { this.filmId = filmId; }

    public String getFilmTitolo() { return filmTitolo; }
    public void setFilmTitolo(String filmTitolo) { this.filmTitolo = filmTitolo; }

    public Integer getFilmDurata() { return filmDurata; }
    public void setFilmDurata(Integer filmDurata) { this.filmDurata = filmDurata; }

    public String getFilmGenere() { return filmGenere; }
    public void setFilmGenere(String filmGenere) { this.filmGenere = filmGenere; }

    public String getRegistaNome() { return registaNome; }
    public void setRegistaNome(String registaNome) { this.registaNome = registaNome; }

    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }

    public String getSalaNome() { return salaNome; }
    public void setSalaNome(String salaNome) { this.salaNome = salaNome; }

    public String getSalaIndirizzo() { return salaIndirizzo; }
    public void setSalaIndirizzo(String salaIndirizzo) { this.salaIndirizzo = salaIndirizzo; }

    public String getFilmLocandinaUrl() { return filmLocandinaUrl; }
    public void setFilmLocandinaUrl(String filmLocandinaUrl) { this.filmLocandinaUrl = filmLocandinaUrl; }
}
