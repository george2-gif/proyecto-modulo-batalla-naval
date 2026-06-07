package batallanaval;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

    // ── Colores ANSI ─────────────────────────────────────────
    private static final String RESET       = "\u001B[0m";
    private static final String NEGRITA     = "\u001B[1m";
    private static final String AZUL_OSCURO = "\u001B[34m";
    private static final String CIAN        = "\u001B[36m";
    private static final String CIAN_CLARO  = "\u001B[96m";
    private static final String BLANCO      = "\u001B[97m";
    private static final String AMARILLO    = "\u001B[93m";
    private static final String VERDE       = "\u001B[92m";
    private static final String ROJO        = "\u001B[91m";
    private static final String FONDO_AZUL  = "\u001B[44m";

    private Scanner scanner;
    private Musica  musica;
    private boolean ejecutando;
    private int     jugadas, ganadas, perdidas;

    public Menu() {
        scanner    = new Scanner(System.in);
        musica     = new Musica();
        ejecutando = true;
    }

    public void mostrar() {
        musica.tocarMenuPrincipal();
        while (ejecutando) {
            mostrarPantalla();
            procesarOpcion(leerOpcion(1, 7));
        }
        musica.detener();
        scanner.close();
    }

    private void mostrarPantalla() {
        limpiarPantalla();
        System.out.println();

        // ── Borde superior ──────────────────────────────────
        System.out.println(CIAN + "  ╔══════════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║" + RESET
                + "                                          "
                + CIAN + "║" + RESET);

        // ── Título ──────────────────────────────────────────
        System.out.println(CIAN + "  ║  " + RESET
                + FONDO_AZUL + CIAN_CLARO + NEGRITA
                + "  ⚓  B A T A L L A   N A V A L  ⚓    "
                + RESET + CIAN + "  ║" + RESET);

        // ── Subtítulo ────────────────────────────────────────
        System.out.println(CIAN + "  ║  " + RESET
                + AZUL_OSCURO + "     Instituto Nacional de Sonzacate    "
                + RESET + CIAN + "  ║" + RESET);

        System.out.println(CIAN + "  ║" + RESET
                + "                                          "
                + CIAN + "║" + RESET);

        // ── Separador ────────────────────────────────────────
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        System.out.println(CIAN + "  ║" + RESET
                + "                                          "
                + CIAN + "║" + RESET);

        // ── Opciones ─────────────────────────────────────────
        opcion("1", "🤖", "1 Jugador vs Computadora     ", CIAN_CLARO);
        opcion("2", "⚔ ", "2 Jugadores (mismo teclado)  ", CIAN_CLARO);
        opcion("3", "🌐", "Multijugador en Red           ", AMARILLO);
        opcion("4", "📖", "Instrucciones                 ", BLANCO);
        opcion("5", "📊", "Estadísticas de sesión        ", BLANCO);
        opcion("6", "🔊", "Música: " + estadoMusica() + "                    ", BLANCO);
        opcion("7", "🚪", "Salir                         ", ROJO);

        System.out.println(CIAN + "  ║" + RESET
                + "                                          "
                + CIAN + "║" + RESET);

        // ── Borde inferior ───────────────────────────────────
        System.out.println(CIAN + "  ╚══════════════════════════════════════════╝" + RESET);

        // ── Prompt ───────────────────────────────────────────
        System.out.print(CIAN_CLARO + "\n  ► Opción (1-7): " + RESET);
    }

    /** Imprime una línea de opción con color personalizado. */
    private void opcion(String num, String icono, String texto, String colorTexto) {
        System.out.println(
            CIAN + "  ║   " + RESET
            + AMARILLO + NEGRITA + num + RESET
            + ".  " + icono + "  "
            + colorTexto + texto + RESET
            + CIAN + "║" + RESET
        );
    }

    private String estadoMusica() {
        return musica.isMusicaActiva()
            ? VERDE  + "ON " + RESET
            : ROJO   + "OFF" + RESET;
    }

    private void procesarOpcion(int op) {
        switch (op) {
        case 1: iniciar1Jugador();     break;
        case 2: iniciar2Jugadores();   break;
        case 3: iniciarModoRed();      break;
        case 4: mostrarInstrucciones(); break;
        case 5: mostrarEstadisticas(); break;
        case 6: toggleMusica();        break;
        case 7: salir();               break;
        }
    }

    private void iniciar1Jugador() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + CIAN_CLARO + NEGRITA
                + "  🤖  JUGADOR vs COMPUTADORA        " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╚══════════════════════════════════════╝" + RESET);
        String nombre = pedirNombre("Almirante");
        musica.detener();
        Juego juego = new Juego(nombre, scanner, musica);
        juego.iniciar();
        jugadas++;
        registrarResultado();
        musica.tocarMenuPrincipal();
    }

    private void iniciar2Jugadores() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + CIAN_CLARO + NEGRITA
                + "  ⚔   MODO 2 JUGADORES              " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ║  " + RESET
                + "    Turnan el teclado en cada turno   "
                + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╚══════════════════════════════════════╝" + RESET);
        System.out.println();
        String nombre1 = pedirNombre("Jugador 1");
        String nombre2 = pedirNombre("Jugador 2");
        musica.detener();
        Juego juego = new Juego(nombre1, nombre2, scanner, musica);
        juego.iniciar();
        jugadas++;
        musica.tocarMenuPrincipal();
    }

    private void iniciarModoRed() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + AMARILLO + NEGRITA
                + "  🌐  MODO MULTIJUGADOR EN RED          " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        System.out.println(CIAN + "  ║   " + RESET + CIAN_CLARO + "1. " + BLANCO
                + "Crear partida " + AZUL_OSCURO + "(soy el Servidor)    " + RESET + CIAN + " ║" + RESET);
        System.out.println(CIAN + "  ║   " + RESET + CIAN_CLARO + "2. " + BLANCO
                + "Unirse a partida " + AZUL_OSCURO + "(soy el Cliente)  " + RESET + CIAN + " ║" + RESET);
        System.out.println(CIAN + "  ║   " + RESET + ROJO + "3. Volver al menú                        " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╚══════════════════════════════════════════╝" + RESET);
        System.out.print(CIAN_CLARO + "\n  ► Opción (1-3): " + RESET);

        int op = leerOpcion(1, 3);
        if (op == 3) return;

        String nombre   = pedirNombre("Almirante");
        Conexion conexion = null;
        boolean esServidor = (op == 1);

        try {
            if (esServidor) {
                conexion = Conexion.comoServidor();
            } else {
                System.out.print(CIAN_CLARO + "\n  ► IP del servidor (ej: 192.168.1.5): " + RESET);
                String ip = scanner.nextLine().trim();
                conexion = Conexion.comoCliente(ip);
            }
            musica.detener();
            JuegoRed juego = new JuegoRed(nombre, conexion, esServidor, scanner, musica);
            juego.iniciar();
            jugadas++;
            musica.tocarMenuPrincipal();

        } catch (IOException e) {
            System.out.println(ROJO + "\n  ⚠ No se pudo conectar: " + e.getMessage() + RESET);
            System.out.println("  Verifica que la IP sea correcta y el servidor esté esperando.");
            System.out.println("  Presiona ENTER...");
            scanner.nextLine();
        } finally {
            if (conexion != null) conexion.cerrar();
        }
    }

    private void mostrarInstrucciones() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + CIAN_CLARO + NEGRITA
                + "          📖 INSTRUCCIONES              " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        lineaInfo("OBJETIVO:", AMARILLO);
        lineaInfo("Hundir los 5 barcos enemigos primero.   ", BLANCO);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        lineaInfo("TABLERO: 10x10 (A-J cols, 1-10 filas)  ", AMARILLO);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        lineaInfo("FLOTA:", AMARILLO);
        lineaInfo("P = Portaaviones  (1 celda)             ", BLANCO);
        lineaInfo("A = Acorazado     (1 celda)             ", BLANCO);
        lineaInfo("C = Crucero       (1 celda)             ", BLANCO);
        lineaInfo("S = Submarino     (1 celda)             ", BLANCO);
        lineaInfo("D = Destructor    (1 celda)             ", BLANCO);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        lineaInfo("SÍMBOLOS:", AMARILLO);
        lineaInfo("~ = Agua sin disparar                   ", AZUL_OSCURO);
        lineaInfo("O = Disparo fallido (agua)              ", BLANCO);
        lineaInfo("X = Impacto en barco                    ", ROJO);
        lineaInfo("# = Barco hundido                       ", ROJO);
        System.out.println(CIAN + "  ╠══════════════════════════════════════════╣" + RESET);
        lineaInfo("BARCOS EN MOVIMIENTO:", AMARILLO);
        lineaInfo("Cada 3 turnos los barcos se mueven      ", BLANCO);
        lineaInfo("1 celda. Barcos dañados no se mueven.   ", BLANCO);
        System.out.println(CIAN + "  ╚══════════════════════════════════════════╝" + RESET);
        System.out.println(CIAN_CLARO + "  Presiona ENTER..." + RESET);
        scanner.nextLine();
    }

    /** Imprime una línea de información dentro del recuadro. */
    private void lineaInfo(String texto, String color) {
        System.out.println(CIAN + "  ║  " + RESET + color + texto + RESET + CIAN + "║" + RESET);
    }

    private void mostrarEstadisticas() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + CIAN_CLARO + NEGRITA
                + "    📊 ESTADÍSTICAS DE SESIÓN        " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╠══════════════════════════════════════╣" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + BLANCO
                + String.format("Partidas jugadas  : %-17d", jugadas) + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + VERDE
                + String.format("Partidas ganadas  : %-17d", ganadas) + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + ROJO
                + String.format("Partidas perdidas : %-17d", perdidas) + CIAN + "║" + RESET);
        if (jugadas > 0)
            System.out.println(CIAN + "  ║  " + RESET + AMARILLO
                    + String.format("Tasa de victoria  : %-14.1f%%  ",
                            (double) ganadas / jugadas * 100) + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╚══════════════════════════════════════╝" + RESET);
        System.out.println(CIAN_CLARO + "\n  Presiona ENTER..." + RESET);
        scanner.nextLine();
    }

    private void toggleMusica() {
        musica.toggleMusica();
        if (musica.isMusicaActiva())
            musica.tocarMenuPrincipal();
        pausar(600);
    }

    private void salir() {
        limpiarPantalla();
        System.out.println(CIAN + "  ╔══════════════════════════════════════╗" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + CIAN_CLARO
                + "  ¡Hasta la próxima, Almirante!      " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ║  " + RESET + AZUL_OSCURO
                + "  Instituto Nacional de Sonzacate     " + RESET + CIAN + "║" + RESET);
        System.out.println(CIAN + "  ╚══════════════════════════════════════╝" + RESET);
        ejecutando = false;
    }

    private String pedirNombre(String etiqueta) {
        String nombre;
        do {
            System.out.printf(CIAN_CLARO + "  ► Nombre de %s: " + RESET, etiqueta);
            nombre = scanner.nextLine().trim();
            if (nombre.isEmpty())
                System.out.println(ROJO + "  ✗ El nombre no puede estar vacío." + RESET);
        } while (nombre.isEmpty());
        return nombre.length() > 14 ? nombre.substring(0, 14) : nombre;
    }

    private void registrarResultado() {
        System.out.print(CIAN_CLARO + "\n  ¿Ganaste la partida? (S/N): " + RESET);
        String r = scanner.nextLine().trim().toUpperCase();
        if (r.equals("S")) ganadas++;
        else               perdidas++;
    }

    private int leerOpcion(int min, int max) {
        while (true) {
            String e = scanner.nextLine().trim();
            try {
                int op = Integer.parseInt(e);
                if (op >= min && op <= max) return op;
            } catch (NumberFormatException ex) { }
            System.out.printf(ROJO + "  ✗ Ingresa un número entre %d y %d: " + RESET, min, max);
        }
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
            for (int i = 0; i < 40; i++) System.out.println();
        }
    }

    private void pausar(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
