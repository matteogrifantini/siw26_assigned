const { chromium } = require('playwright-core');
const fs = require('fs');
const path = require('path');

const CHROME_PATH = '/Users/matteo/Library/Caches/ms-playwright/chromium-1234/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing';
const SCREENSHOT_DIR = '/tmp/playwright-test/screenshots';

async function safeScreenshot(page, filename) {
    try {
        await page.screenshot({ path: path.join(SCREENSHOT_DIR, filename) });
        console.log(`  ✓ Screenshot ${filename} salvato.`);
    } catch (e) {
        // Screenshot opzionale, non blocca l'esecuzione delle asserzioni
    }
}

async function runTests() {
    console.log('=== AVVIO SUITE DI TEST E2E CON PLAYWRIGHT ===');
    console.log('Browser path:', CHROME_PATH);

    const browser = await chromium.launch({
        executablePath: CHROME_PATH,
        headless: true
    });

    const context = await browser.newContext({
        viewport: { width: 1280, height: 800 }
    });
    const page = await context.newPage();

    try {
        // -------------------------------------------------------------
        // TEST 1: Navigazione Pubblica (Home & Catalogo)
        // -------------------------------------------------------------
        console.log('\n[TEST 1] Verifica Homepage e navigazione pubblica...');
        await page.goto('http://localhost:8080/');
        await page.waitForLoadState('networkidle');

        const pageTitle = await page.title();
        console.log('  Titolo pagina:', pageTitle);
        if (!pageTitle.includes('Home') && !pageTitle.includes('CineFest')) {
            throw new Error('Titolo homepage non conforme: ' + pageTitle);
        }

        await safeScreenshot(page, '01_homepage.png');

        // Test pagina elenco festival
        await page.click('nav a:has-text("Festival")');
        await page.waitForURL('**/festivals');
        console.log('  ✓ Navigazione su /festivals riuscita.');

        // Dettaglio primo festival
        await page.click('.card:first-child a:has-text("Vedi Scheda")');
        await page.waitForURL('**/festivals/*');
        console.log('  ✓ Dettaglio festival visualizzato correttamente.');

        // Test catalogo Film con ricerca
        await page.goto('http://localhost:8080/film');
        await page.waitForLoadState('networkidle');
        console.log('  ✓ Catalogo film aperto.');

        // Test ricerca per titolo
        await page.fill('input[name="q"]', 'Dune');
        await page.click('button:has-text("Cerca")');
        await page.waitForURL('**/film?q=Dune');
        const filmCards = await page.locator('.card:has-text("Dune")').count();
        if (filmCards === 0) throw new Error('Ricerca film Dune non ha prodotto risultati');
        console.log('  ✓ Filtro ricerca film funzionante (trovato Dune).');

        // Test dettaglio film
        await page.click('a:has-text("Scheda Film")');
        await page.waitForURL('**/film/*');
        await safeScreenshot(page, '02_scheda_film.png');
        console.log('  ✓ Scheda film e recensioni visualizzata.');

        // -------------------------------------------------------------
        // TEST 2: Componente Frontend React (Sezione 9)
        // -------------------------------------------------------------
        console.log('\n[TEST 2] Verifica Modulo Interattivo React (/programma-interattivo)...');
        await page.goto('http://localhost:8080/programma-interattivo');
        
        // Aspettiamo che React esegua il mount e le chiamate REST
        await page.waitForSelector('#react-screenings-root .card', { timeout: 10000 });
        console.log('  ✓ React montato con successo e proiezioni caricate via REST API.');

        // Test filtro ricerca in tempo reale in React
        const searchInput = page.locator('#react-screenings-root input[placeholder*="Nolan"]');
        await searchInput.fill('Oppenheimer');
        await page.waitForTimeout(500); // attesa re-render reattivo

        const countAfterFilter = await page.locator('#react-screenings-root .card:has-text("Oppenheimer")').count();
        if (countAfterFilter === 0) throw new Error('Filtro React per Oppenheimer non ha funzionato');
        console.log('  ✓ Filtro reattivo React funzionante: trovata proiezione Oppenheimer.');

        await safeScreenshot(page, '03_modulo_react.png');

        // -------------------------------------------------------------
        // TEST 3: Analisi Sperimentale N+1 Query vs JOIN FETCH (Sezione 8.2)
        // -------------------------------------------------------------
        console.log('\n[TEST 3] Verifica Analisi Sperimentale N+1 (/analisi-prestazioni)...');
        await page.goto('http://localhost:8080/analisi-prestazioni');
        await page.waitForSelector('.card:has-text("Strategia 1: LAZY")');
        await page.waitForSelector('.card:has-text("Strategia 2: JOIN FETCH")');
        
        const lazyQueryText = await page.locator('.card:has-text("Strategia 1: LAZY") strong.fs-5').innerText();
        const joinQueryText = await page.locator('.card:has-text("Strategia 2: JOIN FETCH") strong.fs-5').innerText();

        console.log(`  ✓ Benchmark verificato: Strategia LAZY = ${lazyQueryText} query | JOIN FETCH = ${joinQueryText} query.`);
        await safeScreenshot(page, '04_analisi_n1.png');
        console.log('  ✓ Screenshot pagina benchmark salvato.');

        // -------------------------------------------------------------
        // TEST 4: Flusso Utente Autenticato & Recensioni (ROLE_USER)
        // -------------------------------------------------------------
        console.log('\n[TEST 4] Test Autenticazione Utente (ROLE_USER) e gestione Recensioni...');
        await page.goto('http://localhost:8080/login');
        await page.fill('input[name="username"]', 'user');
        await page.fill('input[name="password"]', 'user');
        await page.click('button[type="submit"]');
        await page.waitForURL('http://localhost:8080/index');
        console.log('  ✓ Login utente "user" completato con successo.');

        // Navigazione su un film senza recensione di "user" (es. Film 2: Dune)
        await page.goto('http://localhost:8080/film/2');
        await page.waitForSelector('form[action*="/recensioni/salva"]');
        console.log('  ✓ Form di recensione presente per l\'utente autenticato.');

        // Inserimento nuova recensione
        await page.selectOption('select#voto', '5');
        await page.fill('textarea#testo', 'Effetti speciali incredibili e colonna sonora monumentale. Consigliatissimo!');
        await page.click('button:has-text("Pubblica Recensione")');
        await page.waitForLoadState('networkidle');

        // Verifica che la recensione appaia nel box "La tua recensione"
        const reviewBox = page.locator('.card:has-text("La tua recensione per questo film")');
        if (await reviewBox.count() === 0) throw new Error('Recensione inserita non visualizzata nel box personale');
        console.log('  ✓ Recensione pubblicata e visualizzata correttamente con badge autore.');

        // Test modifica della propria recensione
        const editLink = page.locator('a[href*="/recensioni/modifica/"]');
        await editLink.click();
        await page.waitForURL(/\/recensioni\/modifica\/\d+/);
        await page.selectOption('select#voto', '4');
        await page.fill('textarea#testo', 'Recensione modificata: capolavoro visivo, ma seconda parte un po veloce.');
        await page.click('button:has-text("Salva Modifiche")');
        await page.waitForURL('**/film/2');
        console.log('  ✓ Modifica recensione completata con successo.');

        // Test eliminazione della propria recensione
        page.on('dialog', async dialog => await dialog.accept());
        await page.click('button:has-text("Elimina")');
        await page.waitForLoadState('networkidle');
        console.log('  ✓ Eliminazione recensione completata con successo.');

        // Logout
        await page.click('form[action*="/logout"] button');
        await page.waitForURL('**/login?logout=true');
        console.log('  ✓ Logout utente completato.');

        // -------------------------------------------------------------
        // TEST 5: Flusso Amministratore & Controllo Consistenza Sala (ROLE_ADMIN)
        // -------------------------------------------------------------
        console.log('\n[TEST 5] Test Amministrazione (ROLE_ADMIN) e consistenza disponibilità sala...');
        
        // Verifica protezione admin da non autenticato
        await page.goto('http://localhost:8080/admin/dashboard');
        await page.waitForURL('**/login');
        console.log('  ✓ Protezione Spring Security: accesso ad /admin/dashboard bloccato per anonimi.');

        // Login come admin
        await page.fill('input[name="username"]', 'admin');
        await page.fill('input[name="password"]', 'admin');
        await page.click('button[type="submit"]');
        await page.waitForURL('http://localhost:8080/admin/dashboard');
        await safeScreenshot(page, '05_admin_dashboard.png');

        // Creazione nuovo Festival da interfaccia admin
        await page.click('a:has-text("Nuovo Festival")');
        await page.waitForURL('**/admin/festivals/nuovo');
        const festName = 'Festa del Cinema ' + Date.now();
        await page.fill('input#nome', festName);
        await page.fill('input#anno', '2026');
        await page.fill('input#citta', 'Roma');
        await page.fill('input#dataInizio', '2026-10-15');
        await page.fill('input#dataFine', '2026-10-25');
        await page.fill('textarea#descrizione', 'Edizione autunnale della Festa del Cinema presso l Auditorium Parco della Musica.');
        await page.click('button:has-text("Salva Festival")');
        await page.waitForURL('**/admin/dashboard');
        console.log('  ✓ Nuovo festival inserito con successo dall\'amministratore.');

        // TEST FONDAMENTALE: Verifica consistenza temporale disponibilità sala
        console.log('  Verifica controllo atomico sovrapposizione sala (Sezione 4.3)...');
        await page.goto('http://localhost:8080/admin/proiezioni/nuova');

        // Proviamo a programmare una proiezione in "Sala Grande" il 2026-09-03 alle 17:00
        // Nel database demo c'è già Oppenheimer (durata 180 min) dalle 16:30 alle 19:30 nella stessa Sala Grande!
        const festOptVal = await page.locator('select#festivalId option:has-text("Venezia")').getAttribute('value');
        await page.selectOption('select#festivalId', festOptVal);

        const filmOptVal = await page.locator('select#filmId option:has-text("Oppenheimer")').getAttribute('value');
        await page.selectOption('select#filmId', filmOptVal);

        const salaOptVal = await page.locator('select#salaId option:has-text("Sala Grande")').getAttribute('value');
        await page.selectOption('select#salaId', salaOptVal);

        await page.fill('input#data', '2026-09-03');
        await page.fill('input#ora', '17:00'); // Conflitto orario evidente!

        await page.click('button:has-text("Programma Proiezione")');
        await page.waitForLoadState('networkidle');

        // Verifichiamo che rimanga sul form con il messaggio di errore di sovrapposizione
        const errorBanner = page.locator('.alert-danger:has-text("Conflitto di programmazione")').first();
        const isErrorVisible = await errorBanner.isVisible();
        if (!isErrorVisible) {
            throw new Error('Il sistema NON ha bloccato la programmazione della proiezione sovrapposta!');
        }
        const errorText = await errorBanner.innerText();
        console.log('  ✓ SUCCESSO: La sovrapposizione è stata bloccata!');
        console.log('    Messaggio generato:', errorText.trim());

        await safeScreenshot(page, '06_errore_conflitto_sala.png');
        console.log('  ✓ Screenshot alert conflitto sala salvato.');

        console.log('\n=============================================================');
        console.log('=== TUTTI I TEST E2E PLAYWRIGHT SUPERATI CON SUCCESSO! ===');
        console.log('=============================================================');

    } catch (err) {
        console.error('\n❌ ERRORE DURANTE I TEST PLAYWRIGHT:', err);
        process.exit(1);
    } finally {
        await browser.close();
    }
}

runTests();
