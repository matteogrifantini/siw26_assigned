/**
 * Componente React per la visualizzazione dinamica del programma delle proiezioni.
 * Recupera i dati tramite fetch dalle API REST di Spring Boot (/api/screenings e /api/festivals).
 */

const { useState, useEffect } = React;

function ProgrammaProiezioni() {
    // Lista di proiezioni e festival caricati dal server
    const [proiezioni, setProiezioni] = useState([]);
    const [festivals, setFestivals] = useState([]);
    const [caricamento, setCaricamento] = useState(true);
    const [errore, setErrore] = useState(null);

    // Valori selezionati nei filtri
    const [filtroTesto, setFiltroTesto] = useState("");
    const [filtroFestival, setFiltroFestival] = useState("");
    const [filtroStato, setFiltroStato] = useState("");

    // All'avvio del componente carichiamo i dati con le API REST
    useEffect(() => {
        caricaDati();
    }, []);

    function caricaDati() {
        setCaricamento(true);
        setErrore(null);

        // Chiamata alle due API REST
        Promise.all([
            fetch("/api/screenings").then(res => res.json()),
            fetch("/api/festivals").then(res => res.json())
        ])
        .then(([dataScreenings, dataFestivals]) => {
            setProiezioni(dataScreenings);
            setFestivals(dataFestivals);
            setCaricamento(false);
        })
        .catch(err => {
            console.error("Errore nel caricamento:", err);
            setErrore("Impossibile caricare i dati del programma.");
            setCaricamento(false);
        });
    }

    // Filtraggio delle proiezioni in memoria
    const proiezioniFiltrate = proiezioni.filter(p => {
        const matchTesto = filtroTesto === "" ||
            (p.filmTitolo && p.filmTitolo.toLowerCase().includes(filtroTesto.toLowerCase())) ||
            (p.registaNome && p.registaNome.toLowerCase().includes(filtroTesto.toLowerCase()));

        const matchFestival = filtroFestival === "" ||
            (p.festivalId && p.festivalId.toString() === filtroFestival);

        const matchStato = filtroStato === "" || p.stato === filtroStato;

        return matchTesto && matchFestival && matchStato;
    });

    if (caricamento) {
        return (
            <div className="text-center py-5">
                <div className="spinner-border text-primary" role="status"></div>
                <p className="mt-2 text-muted">Caricamento in corso...</p>
            </div>
        );
    }

    if (errore) {
        return (
            <div className="alert alert-danger">
                {errore}
                <button className="btn btn-sm btn-outline-danger ms-3" onClick={caricaDati}>Riprova</button>
            </div>
        );
    }

    return (
        <div>
            {/* Box dei filtri */}
            <div className="card p-3 mb-4 bg-light border-0 shadow-sm">
                <div className="row g-3">
                    <div className="col-md-4">
                        <label className="form-label small fw-bold">Cerca Film o Regista</label>
                        <input
                            type="text"
                            className="form-control form-control-sm"
                            placeholder="es. Nolan, Oppenheimer..."
                            value={filtroTesto}
                            onChange={e => setFiltroTesto(e.target.value)}
                        />
                    </div>

                    <div className="col-md-4">
                        <label className="form-label small fw-bold">Filtra per Festival</label>
                        <select
                            className="form-select form-select-sm"
                            value={filtroFestival}
                            onChange={e => setFiltroFestival(e.target.value)}
                        >
                            <option value="">Tutti i festival</option>
                            {festivals.map(f => (
                                <option key={f.id} value={f.id}>{f.nome} ({f.anno})</option>
                            ))}
                        </select>
                    </div>

                    <div className="col-md-2">
                        <label className="form-label small fw-bold">Stato</label>
                        <select
                            className="form-select form-select-sm"
                            value={filtroStato}
                            onChange={e => setFiltroStato(e.target.value)}
                        >
                            <option value="">Tutti</option>
                            <option value="SCHEDULED">Programmate</option>
                            <option value="COMPLETED">Concluse</option>
                            <option value="CANCELLED">Annullate</option>
                        </select>
                    </div>

                    <div className="col-md-2 d-flex align-items-end">
                        <button
                            className="btn btn-outline-secondary btn-sm w-100"
                            onClick={() => { setFiltroTesto(""); setFiltroFestival(""); setFiltroStato(""); }}
                        >
                            Azzera filtri
                        </button>
                    </div>
                </div>
            </div>

            {/* Conteggio risultati */}
            <p className="text-muted small mb-3">
                Trovate <strong>{proiezioniFiltrate.length}</strong> proiezioni.
            </p>

            {/* Elenco card delle proiezioni */}
            {proiezioniFiltrate.length === 0 ? (
                <div className="alert alert-secondary text-center py-4">
                    Nessuna proiezione trovata con i filtri selezionati.
                </div>
            ) : (
                <div className="row g-3">
                    {proiezioniFiltrate.map(p => (
                        <div key={p.id} className="col-md-6 col-lg-4">
                            <div className="card h-100 shadow-sm border-0">
                                <div className="card-body d-flex flex-column">
                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <span className="badge bg-dark">
                                            {p.ora} - {p.oraFine}
                                        </span>
                                        <span className="badge bg-secondary">
                                            {p.statoEtichetta || p.stato}
                                        </span>
                                    </div>

                                    <div className="d-flex gap-3 mb-3">
                                        {p.filmLocandinaUrl ? (
                                            <img
                                                src={p.filmLocandinaUrl}
                                                alt={p.filmTitolo}
                                                className="film-poster-thumb shadow-sm"
                                            />
                                        ) : (
                                            <div className="film-poster-thumb bg-light d-flex align-items-center justify-content-center text-muted border">
                                                <i className="bi bi-film fs-3"></i>
                                            </div>
                                        )}
                                        <div className="flex-grow-1">
                                            <h5 className="card-title fw-bold mb-1 fs-6">
                                                <a href={`/film/${p.filmId}`} className="text-decoration-none text-dark">
                                                    {p.filmTitolo}
                                                </a>
                                            </h5>
                                            <p className="small text-muted mb-1">
                                                Regia: <strong>{p.registaNome || "N/D"}</strong>
                                            </p>
                                            <div>
                                                <span className="badge bg-light text-dark border me-1">{p.filmGenere}</span>
                                                <span className="badge bg-light text-dark border">{p.filmDurata} min</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="mt-auto pt-2 border-top small text-muted">
                                        <div>Festival: <strong>{p.festivalNome}</strong></div>
                                        <div>Sala: <strong>{p.salaNome}</strong></div>
                                        <div>Data: <strong>{p.data}</strong></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

// Montaggio del componente React nel div della pagina HTML
const rootElement = document.getElementById("react-screenings-root");
if (rootElement) {
    ReactDOM.createRoot(rootElement).render(<ProgrammaProiezioni />);
}
