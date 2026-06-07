package batallanaval;

import java.io.IOException;
import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║         MODO MULTIJUGADOR EN RED  🌐                     ║
 * ║                                                          ║
 * ║  Jugador 1 (Servidor):  java Main  → Opción 3           ║
 * ║                         → Opción 1 "Crear partida"       ║
 * ║                                                          ║
 * ║  Jugador 2 (Cliente):   java Main  → Opción 3           ║
 * ║                         → Opción 2 "Unirse" → escribe IP ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Flujo por turnos:
 *   Servidor dispara primero.
 *
 *   TURNO DEL JUGADOR LOCAL:
 *     1. Pide coordenadas al usuario
 *     2. Envía Mensaje.disparo(fila, col) al rival
 *     3. Espera Mensaje.resultado(...) del rival
 *     4. Muestra resultado en pantalla
 *
 *   TURNO DEL RIVAL:
 *     1. Espera Mensaje.disparo(...) del rival
 *     2. Procesa el disparo en tu tablero local
 *     3. Envía Mensaje.resultado(...) con el resultado
 *     4. Muestra qué disparó el rival
 */
public class JuegoRed {

    private static final int TURNOS_PARA_MOVER = 3;

    private Jugador   jugadorLocal;
    private Tablero   tableroRival;   // Solo guardamos los disparos que hicimos contra el rival
    private Conexion  conexion;
    private Musica    musica;
    private Scanner   scanner;
    private boolean   esServidor;     // true = soy servidor (disparo primero)
    private boolean   juegoActivo;
    private int       contadorTurnos;

    // ── Constructor ─────────────────────────────────────────
    public JuegoRed(String nombreJugador, Conexion conexion,
                    boolean esServidor, Scanner scanner, Musica musica) {
        this.jugadorLocal  = new Jugador(nombreJugador, scanner);
        this.tableroRival  = new Tablero();   // solo para llevar el mapa de disparos
        this.conexion      = conexion;
        this.esServidor    = esServidor;
        this.scanner       = scanner;
        this.musica        = musica;
        this.juegoActivo   = true;
        this.contadorTurnos = 0;
    }

    // ── Iniciar partida ─────────────────────────────────────
    public void iniciar() {
        mostrarIntroduccion();

        // 1. Colocar barcos
        System.out.println("\n  ⚓ Coloca tu flota antes de empezar...");
        jugadorLocal.colocarBarcosAutomatico();

        // 2. Sincronizar: esperar que el rival también termine de colocar
        sincronizarFlota();

        // 3. Iniciar música y bucle de juego
        musica.tocarBatalla();
        System.out.println("\n  ¡La batalla comienza!");
        pausar(1000);

        // El servidor dispara primero
        boolean miTurno = esServidor;

        while (juegoActivo) {
            contadorTurnos++;

            // Mover barcos cada N turnos
            if (contadorTurnos % TURNOS_PARA_MOVER == 0) {
                jugadorLocal.getTablero().moverBarcos();
                System.out.println("  ⚓ ¡Tus barcos se han movido!");
                pausar(700);
            }

            if (miTurno) {
                miTurno = ejecutarMiTurno();    // devuelve false si el juego terminó
            } else {
                miTurno = esperarTurnoRival();  // devuelve true cuando termina el turno rival
            }
        }
    }

    // ── Mi turno: disparo al rival ──────────────────────────
    /**
     * @return false si el juego terminó (gané), true para continuar
     */
    private boolean ejecutarMiTurno() {
        limpiarPantalla();
        mostrarTableros();

        int faltan = TURNOS_PARA_MOVER - (contadorTurnos % TURNOS_PARA_MOVER);
        System.out.printf("  ⚓ Barcos se mueven en: %d turno(s)%n", faltan);
        System.out.println("  ═══════════════════════════════════════");
        System.out.printf ("       🎯  TU TURNO: %s%n", jugadorLocal.getNombre().toUpperCase());
        System.out.println("  ═══════════════════════════════════════");
        System.out.println("  (Escribe 'R' para rendirte)");

        // ¿Rendirse?
        if (jugadorSeRinde()) {
            try {
                conexion.enviar(Mensaje.fin("rendicion"));
            } catch (IOException e) {
                System.out.println("  ⚠ Error al enviar rendición.");
            }
            juegoActivo = false;
            mostrarRendicion();
            return false;
        }

        // Pedir disparo (reutiliza realizarDisparo() de Jugador,
        // que ya valida que no se repita en grillaEnemigo del jugador local)
        int[] coords = pedirDisparoSinRepetir();
        int fila    = coords[0];
        int columna = coords[1];

        // Enviar disparo al rival
        try {
            conexion.enviar(Mensaje.disparo(fila, columna));
            System.out.println("\n  📡 Disparo enviado, esperando respuesta...");

            // Esperar resultado
            Mensaje respuesta = conexion.recibir();

            if (respuesta.tipo == Mensaje.Tipo.FIN) {
                // El rival se rindió o perdió
                juegoActivo = false;
                musica.tocarVictoria();
                mostrarVictoria("¡El rival abandonó la partida!");
                return false;
            }

            // Registrar resultado en el mapa de disparos del jugador local
            jugadorLocal.registrarResultado(fila, columna, respuesta.resultado);

            mostrarResultado(respuesta.resultado, fila, columna);
            reproducirSonido(respuesta.resultado);
            pausar(1400);

            // ¿Gané? (hundí los 5 barcos del rival)
            if (tableroRival.getBarcosHundidos() >= 5
                    || (respuesta.tipo == Mensaje.Tipo.FIN)) {
                juegoActivo = false;
                musica.tocarVictoria();
                mostrarVictoria("¡Hundiste toda la flota enemiga!");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("  ⚠ Conexión perdida con el rival.");
            juegoActivo = false;
            return false;
        }

        return false; // no es mi turno ahora → pasa al rival
    }

    // ── Turno del rival: espero su disparo ──────────────────
    /**
     * @return true cuando termina el turno rival (para que sea mi turno)
     */
    private boolean esperarTurnoRival() {
        System.out.println("\n  ═══════════════════════════════════════");
        System.out.println("       ⏳  TURNO DEL RIVAL, ESPERA...");
        System.out.println("  ═══════════════════════════════════════");

        try {
            Mensaje m = conexion.recibir();

            if (m.tipo == Mensaje.Tipo.FIN) {
                juegoActivo = false;
                musica.tocarVictoria();
                mostrarVictoria("¡El rival se rindió!");
                return false;
            }

            if (m.tipo == Mensaje.Tipo.DISPARO) {
                // Procesar el disparo en MI tablero
                int resultado = jugadorLocal.getTablero().recibirDisparo(m.fila, m.columna);
                char col = (char)('A' + m.columna);
                System.out.printf("  🎯 El rival disparó en: %c%d%n", col, m.fila + 1);
                mostrarResultado(resultado, m.fila, m.columna);
                reproducirSonido(resultado);

                // Enviar resultado de vuelta
                conexion.enviar(Mensaje.resultado(m.fila, m.columna, resultado));
                pausar(1400);

                // ¿Perdí?
                if (jugadorLocal.getTablero().todosBarcosHundidos()) {
                    conexion.enviar(Mensaje.fin("victoria_rival"));
                    juegoActivo = false;
                    musica.tocarDerrota();
                    mostrarDerrota();
                    return false;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("  ⚠ Conexión perdida con el rival.");
            juegoActivo = false;
            return false;
        }

        return true; // ahora es mi turno
    }

    // ── Sincronización de inicio ────────────────────────────
    private void sincronizarFlota() {
        System.out.println("\n  📡 Sincronizando con el rival...");
        try {
            conexion.enviar(Mensaje.listo());
            Mensaje m = conexion.recibir();
            if (m.tipo == Mensaje.Tipo.LISTO) {
                System.out.println("  ✅ ¡Rival listo! La batalla puede comenzar.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("  ⚠ Error de sincronización.");
        }
        pausar(800);
    }

    // ── Pedir disparo sin repetir ───────────────────────────
    private int[] pedirDisparoSinRepetir() {
        // Usamos la grillaEnemigo del tablero del jugador local para saber
        // qué celdas ya disparamos (registrarResultado las marca ahí)
        int fila, columna;
        Tablero t = jugadorLocal.getTablero();
        do {
            char colLetra = pedirColumna();
            columna = colLetra - 'A';
            fila    = pedirFila() - 1;
            if (t.yaDisparoEn(fila, columna)) {
                System.out.println("  ✗ ¡Ya disparaste ahí! Elige otra celda.");
            } else {
                break;
            }
        } while (true);
        return new int[]{fila, columna};
    }

    private char pedirColumna() {
        while (true) {
            System.out.print("  ► Columna (A-J): ");
            String e = scanner.nextLine().trim().toUpperCase();
            if (e.length() == 1 && e.charAt(0) >= 'A' && e.charAt(0) <= 'J')
                return e.charAt(0);
            System.out.println("  ✗ Ingresa una letra entre A y J.");
        }
    }

    private int pedirFila() {
        while (true) {
            System.out.print("  ► Fila    (1-10): ");
            String e = scanner.nextLine().trim();
            try {
                int f = Integer.parseInt(e);
                if (f >= 1 && f <= 10) return f;
            } catch (NumberFormatException ex) { /* ignorar */ }
            System.out.println("  ✗ Ingresa un número entre 1 y 10.");
        }
    }

    // ── Pantallas ───────────────────────────────────────────
    private void mostrarTableros() {
        System.out.printf("%n  ┌──── TU FLOTA (%s) ────┐%n", jugadorLocal.getNombre());
        jugadorLocal.getTablero().mostrarTableroPropio();
        System.out.printf("  Barcos propios hundidos: %d/5%n",
                jugadorLocal.getTablero().getBarcosHundidos());

        System.out.println("\n  ┌──── MAPA DE DISPARO (rival) ────┐");
        jugadorLocal.getTablero().mostrarTableroEnemigo();
    }

    private void mostrarResultado(int resultado, int fila, int columna) {
        char col = (char)('A' + columna);
        System.out.println("\n  ┌──────────────────────────────────┐");
        switch (resultado) {
            case 0: System.out.printf("  │  💧  AGUA   → %c%d              │%n", col, fila+1); break;
            case 1: System.out.printf("  │  💥  IMPACTO→ %c%d              │%n", col, fila+1); break;
            case 2: System.out.printf("  │  🔥  HUNDIDO→ %c%d              │%n", col, fila+1); break;
            case -1: System.out.println("  │  ⚠   Celda ya disparada        │"); break;
        }
        System.out.println("  └──────────────────────────────────┘");
    }

    private void reproducirSonido(int resultado) {
        if (resultado == 0) musica.sonarDisparoAgua();
        else if (resultado == 1) musica.sonarDisparoBarco();
        else if (resultado == 2) musica.sonarHundido();
    }

    private void mostrarIntroduccion() {
        limpiarPantalla();
        System.out.println("  ╔════════════════════════════════════════╗");
        System.out.println("  ║   ⚓  B A T A L L A   N A V A L  ⚓   ║");
        System.out.println("  ╠════════════════════════════════════════╣");
        System.out.println("  ║  Modo: 🌐  MULTIJUGADOR EN RED         ║");
        System.out.printf ("  ║  Rol:  %-32s║%n", esServidor ? "SERVIDOR (disparas primero)" : "CLIENTE");
        System.out.println("  ╠════════════════════════════════════════╣");
        System.out.println("  ║  ~ Agua  O Fallo  X Impacto  # Hundido ║");
        System.out.println("  ╚════════════════════════════════════════╝");
        System.out.println("  Presiona ENTER para continuar...");
        scanner.nextLine();
    }

    private void mostrarVictoria(String motivo) {
        limpiarPantalla();
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║   🏆  ¡VICTORIA!  🏆                 ║");
        System.out.printf ("  ║   %-38s║%n", motivo);
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println("\n  📊 TUS ESTADÍSTICAS:");
        jugadorLocal.mostrarEstadisticas();
        System.out.println("\n  Presiona ENTER...");
        scanner.nextLine();
    }

    private void mostrarDerrota() {
        limpiarPantalla();
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║   💀  ¡DERROTA!  💀                  ║");
        System.out.println("  ║   El rival hundió toda tu flota...    ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println("\n  📊 TUS ESTADÍSTICAS:");
        jugadorLocal.mostrarEstadisticas();
        System.out.println("\n  Presiona ENTER...");
        scanner.nextLine();
    }

    private void mostrarRendicion() {
        limpiarPantalla();
        musica.tocarDerrota();
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.println("  ║   🏳  TE HAS RENDIDO, ALMIRANTE          ║");
        System.out.println("  ║   A veces la retirada es la mejor         ║");
        System.out.println("  ║   estrategia...                           ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println("\n  Presiona ENTER para volver al menú...");
        scanner.nextLine();
    }

    private boolean jugadorSeRinde() {
        System.out.print("  ► ¿Te rindes? (S = Sí / cualquier tecla = No): ");
        String r = scanner.nextLine().trim().toUpperCase();
        if (!r.equals("S")) return false;
        System.out.print("  ► ¿Seguro? Perderás la partida (S = Confirmar): ");
        return scanner.nextLine().trim().toUpperCase().equals("S");
    }

    private void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 35; i++) System.out.println();
        }
    }

    private void pausar(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
