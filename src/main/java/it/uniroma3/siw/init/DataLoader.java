package it.uniroma3.siw.init;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import it.uniroma3.siw.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private RegistaRepository registaRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Se esistono già credenziali nel database, non reinserire i dati demo
        if (credentialsRepository.count() > 0) {
            return;
        }

        System.out.println("Inizializzazione dati dimostrativi in corso...");

        // 1. Creazione Utente Amministratore
        Utente adminUser = new Utente("Mario", "Rossi", "admin@festival.it");
        Credentials adminCreds = new Credentials("admin", "admin", Credentials.ADMIN_ROLE, adminUser);
        credentialsService.registraUtente(adminUser, adminCreds);

        // 2. Creazione Utente Standard
        Utente standardUser = new Utente("Luca", "Bianchi", "luca@gmail.com");
        Credentials userCreds = new Credentials("user", "user", Credentials.DEFAULT_ROLE, standardUser);
        credentialsService.registraUtente(standardUser, userCreds);

        // 3. Creazione Registi
        Regista nolan = new Regista("Christopher", "Nolan", LocalDate.of(1970, 7, 30), "Britannica",
                "Regista, sceneggiatore e produttore britannico celebre per il suo stile narrativo visionario, la predilezione per la pellicola 70mm e capolavori che esplorano il tempo e la memoria umana.",
                "/images/registi/nolan.jpg");

        Regista villeneuve = new Regista("Denis", "Villeneuve", LocalDate.of(1967, 10, 3), "Canadese",
                "Regista canadese acclamato a livello internazionale per la maestria visiva, il controllo atmosferico e la profondità filosofica delle sue grandi narrazioni fantascientifiche.",
                "/images/registi/villeneuve.jpg");

        Regista gerwig = new Regista("Greta", "Gerwig", LocalDate.of(1983, 8, 4), "Statunitense",
                "Regista e sceneggiatrice statunitense candidata agli Oscar, autrice di opere di straordinario successo che esplorano con ironia e sensibilità l'identità femminile e le relazioni.",
                "/images/registi/gerwig.jpg");

        Regista sorrentino = new Regista("Paolo", "Sorrentino", LocalDate.of(1970, 5, 31), "Italiana",
                "Regista e sceneggiatore italiano vincitore del Premio Oscar, noto per la sua raffinata estetica barocca, il tono malinconico e il racconto visivo della bellezza e della decadenza.",
                "/images/registi/sorrentino.jpg");

        registaRepository.save(nolan);
        registaRepository.save(villeneuve);
        registaRepository.save(gerwig);
        registaRepository.save(sorrentino);

        // 4. Creazione Film
        Film oppenheimer = new Film("Oppenheimer", 2023, 180, "Biografico/Drammatico", "USA",
                "La storia del fisico teorico J. Robert Oppenheimer e del suo ruolo centrale alla guida del Progetto Manhattan, il programma scientifico che portò alla creazione della prima bomba atomica.",
                "/images/film/oppenheimer.jpg",
                nolan);

        Film dune2 = new Film("Dune: Parte Due", 2024, 166, "Fantascienza/Avventura", "USA",
                "Paul Atreides si unisce a Chani e ai Fremen sul pianeta desertico Arrakis per vendicarsi dei cospiratori che hanno sterminato la sua famiglia, affrontando una scelta drammatica tra amore e destino.",
                "/images/film/dune2.jpg",
                villeneuve);

        Film barbie = new Film("Barbie", 2023, 114, "Commedia/Fantastico", "USA",
                "Nel colorato mondo di Barbie Land, Barbie e Ken si avventurano nel mondo reale per scoprire la verità su se stessi, affrontando le complessità e le sfide dell'autenticità umana.",
                "/images/film/barbie.jpg",
                gerwig);

        Film parthenope = new Film("Parthenope", 2024, 136, "Drammatico", "Italia",
                "Il lungo e affascinante viaggio nella vita di Parthenope, dalla giovinezza nel 1950 fino ai giorni nostri, alla ricerca inesausta della libertà, dell'amore e della conoscenza a Napoli.",
                "/images/film/parthenope.jpg",
                sorrentino);

        Film odissea = new Film("Odissea", 2026, 175, "Avventura/Epico", "USA",
                "L'adattamento cinematografico del poema omerico diretto da Christopher Nolan: lo spettacolare e tormentato viaggio di Odisseo tra creature mitologiche, insidie divine e la ricerca del ritorno a Itaca.",
                "/images/film/odissea.jpg",
                nolan);

        filmRepository.save(oppenheimer);
        filmRepository.save(odissea);
        filmRepository.save(dune2);
        filmRepository.save(barbie);
        filmRepository.save(parthenope);

        // 5. Creazione Sale Cinematografiche
        Sala salaGrande = new Sala("Sala Grande", "Lungomare Marconi, Venezia Lido", 1032);
        Sala salaVolpi = new Sala("Sala Volpi", "Palazzo del Cinema, Venezia", 149);
        Sala salaDarsena = new Sala("Sala Darsena", "Piazzale del Casinò, Venezia", 1400);
        Sala lumiere = new Sala("Grand Théâtre Lumière", "Boulevard de la Croisette, Cannes", 2300);

        salaRepository.save(salaGrande);
        salaRepository.save(salaVolpi);
        salaRepository.save(salaDarsena);
        salaRepository.save(lumiere);

        // 6. Creazione Festival
        Festival venezia = new Festival(
                "Mostra Internazionale d'Arte Cinematografica di Venezia",
                2026,
                "Venezia",
                LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 12),
                "La Mostra Internazionale d'Arte Cinematografica della Biennale di Venezia è uno dei più prestigiosi festival cinematografici al mondo.",
                "/images/festivals/venezia.jpg"
        );

        Festival cannes = new Festival(
                "Festival di Cannes",
                2026,
                "Cannes",
                LocalDate.of(2026, 5, 12),
                LocalDate.of(2026, 5, 23),
                "Celebre festival cinematografico annuale che si tiene al Palais des Festivals et des Congrès di Cannes.",
                "/images/festivals/cannes.jpg"
        );

        // Associazione Film ai Festival
        venezia.addFilm(oppenheimer);
        venezia.addFilm(dune2);
        venezia.addFilm(parthenope);

        cannes.addFilm(odissea);
        cannes.addFilm(barbie);
        cannes.addFilm(parthenope);

        festivalRepository.save(venezia);
        festivalRepository.save(cannes);

        // 7. Creazione Proiezioni con orari non sovrapposti
        Proiezione p1 = new Proiezione(LocalDate.of(2026, 9, 3), LocalTime.of(16, 30), StatoProiezione.SCHEDULED, venezia, oppenheimer, salaGrande);
        Proiezione p2 = new Proiezione(LocalDate.of(2026, 9, 3), LocalTime.of(20, 30), StatoProiezione.SCHEDULED, venezia, dune2, salaGrande);
        Proiezione p3 = new Proiezione(LocalDate.of(2026, 9, 4), LocalTime.of(18, 0), StatoProiezione.SCHEDULED, venezia, parthenope, salaVolpi);
        Proiezione p4 = new Proiezione(LocalDate.of(2026, 5, 15), LocalTime.of(21, 0), StatoProiezione.SCHEDULED, cannes, odissea, lumiere);

        proiezioneRepository.save(p1);
        proiezioneRepository.save(p2);
        proiezioneRepository.save(p3);
        proiezioneRepository.save(p4);

        // 8. Creazione Recensioni dimostrative
        Recensione r1 = new Recensione("Un capolavoro assoluto della regia contemporanea. Ritmo perfetto e montaggio eccezionale.", 5, LocalDate.of(2026, 9, 4), oppenheimer, standardUser);
        Recensione r2 = new Recensione("Un'epopea visiva straordinaria, Nolan reinterpreta il mito omerico con una potenza cinematografica senza precedenti.", 5, LocalDate.of(2026, 5, 16), odissea, standardUser);
        recensioneRepository.save(r1);
        recensioneRepository.save(r2);

        System.out.println("Dati dimostrativi inseriti con successo!");
    }
}
