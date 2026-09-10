package it.uniroma3.siw.exception;

public class OperazioneNonAutorizzataException extends RuntimeException {
    public OperazioneNonAutorizzataException(String message) {
        super(message);
    }
}
