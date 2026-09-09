package it.uniroma3.siw.model;

public enum StatoProiezione {
    SCHEDULED("Programmata"),
    COMPLETED("Completata"),
    CANCELLED("Annullata");

    private final String etichetta;

    StatoProiezione(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getEtichetta() {
        return etichetta;
    }
}
