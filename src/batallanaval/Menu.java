package batallanaval;

import java.util.Scanner;

public class Menu {

	private Scanner scanner;
	private Musica musica;
	private boolean ejecutando;

	private int jugadas, ganadas, perdidas;

	public Menu() {
		scanner = new Scanner(System.in);
		musica = new Musica();
		ejecutando = true;
	}

	public void mostrar() {
		musica.tocarMenuPrincipal();
		while (ejecutando) {
			mostrarPantalla();
			procesarOpcion(leerOpcion(1, 6));
		}
		musica.detener();
		scanner.close();
	}

	private void mostrarPantalla() {
		limpiarPantalla();
		System.out.println();
		System.out.println("  ╔══════════════════════════════════════════╗");
		System.out.println("  ║                                          ║");
		System.out.println("  ║   ⚓  B A T A L L A   N A V A L  ⚓     ║");
		System.out.println("  ║      Instituto Nacional de Sonzacate     ║");
		System.out.println("  ║                                          ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║                                          ║");
		System.out.println("  ║   1.  🤖  1 Jugador vs Computadora      ║");
		System.out.println("  ║   2.  ⚔   2 Jugadores (mismo teclado)   ║");
		System.out.println("  ║   3.  📖  Instrucciones                  ║");
		System.out.println("  ║   4.  📊  Estadísticas de sesión         ║");
		System.out.println("  ║   5.  🔊  Música: " + estadoMusica() + "              ║");
		System.out.println("  ║   6.  🚪  Salir                          ║");
		System.out.println("  ║                                          ║");
		System.out.println("  ╚══════════════════════════════════════════╝");
		System.out.print("\n  ► Opción (1-6): ");
	}

	private String estadoMusica() {
		return musica.isMusicaActiva() ? "ON  " : "OFF ";
	}

	private void procesarOpcion(int op) {
		switch (op) {
		case 1:
			iniciar1Jugador();
			break;
		case 2:
			iniciar2Jugadores();
			break;
		case 3:
			mostrarInstrucciones();
			break;
		case 4:
			mostrarEstadisticas();
			break;
		case 5:
			toggleMusica();
			break;
		case 6:
			salir();
			break;
		}
	}

	private void iniciar1Jugador() {
		limpiarPantalla();
		System.out.println("  ╔══════════════════════════════════════╗");
		System.out.println("  ║    🤖  JUGADOR vs COMPUTADORA        ║");
		System.out.println("  ╚══════════════════════════════════════╝");
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
		System.out.println("  ╔══════════════════════════════════════╗");
		System.out.println("  ║    ⚔   MODO 2 JUGADORES              ║");
		System.out.println("  ║    Turnan el teclado en cada turno   ║");
		System.out.println("  ╚══════════════════════════════════════╝");
		System.out.println();
		String nombre1 = pedirNombre("Jugador 1");
		String nombre2 = pedirNombre("Jugador 2");
		musica.detener();
		Juego juego = new Juego(nombre1, nombre2, scanner, musica);
		juego.iniciar();
		jugadas++;
		musica.tocarMenuPrincipal();
	}

	private void mostrarInstrucciones() {
		limpiarPantalla();
		System.out.println("  ╔══════════════════════════════════════════╗");
		System.out.println("  ║            📖 INSTRUCCIONES              ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  OBJETIVO:                               ║");
		System.out.println("  ║  Hundir los 5 barcos enemigos primero.   ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  TABLERO: 10x10 (A-J columnas, 1-10 fil)║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  FLOTA:                                  ║");
		System.out.println("  ║  P = Portaaviones  (1 celdas)           ║");
		System.out.println("  ║  A = Acorazado     (1 celdas)           ║");
		System.out.println("  ║  C = Crucero       (1 celdas)           ║");
		System.out.println("  ║  S = Submarino     (1 celdas)           ║");
		System.out.println("  ║  D = Destructor    (1 celdas)           ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  SÍMBOLOS:                               ║");
		System.out.println("  ║  ~ = Agua sin disparar                   ║");
		System.out.println("  ║  O = Disparo fallido (agua)              ║");
		System.out.println("  ║  X = Impacto en barco                    ║");
		System.out.println("  ║  # = Barco hundido                       ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  BARCOS EN MOVIMIENTO:                   ║");
		System.out.println("  ║  Cada 3 turnos los barcos se mueven      ║");
		System.out.println("  ║  1 celda. Los barcos dañados no se       ║");
		System.out.println("  ║  mueven. ¡Apunta bien!                   ║");
		System.out.println("  ╠══════════════════════════════════════════╣");
		System.out.println("  ║  MODO 2 JUGADORES:                       ║");
		System.out.println("  ║  Turnan el teclado. Una pantalla de      ║");
		System.out.println("  ║  privacidad oculta el tablero entre      ║");
		System.out.println("  ║  cada turno.                             ║");
		System.out.println("  ╚══════════════════════════════════════════╝");
		System.out.println("  Presiona ENTER...");
		scanner.nextLine();
	}

	private void mostrarEstadisticas() {
		limpiarPantalla();
		System.out.println("  ╔══════════════════════════════════════╗");
		System.out.println("  ║       📊 ESTADÍSTICAS DE SESIÓN      ║");
		System.out.println("  ╚══════════════════════════════════════╝");
		System.out.printf("  Partidas jugadas  : %d%n", jugadas);
		System.out.printf("  Partidas ganadas  : %d%n", ganadas);
		System.out.printf("  Partidas perdidas : %d%n", perdidas);
		if (jugadas > 0)
			System.out.printf("  Tasa de victoria  : %.1f%%%n", (double) ganadas / jugadas * 100);
		System.out.println("\n  Presiona ENTER...");
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
		System.out.println("  ╔══════════════════════════════════════╗");
		System.out.println("  ║  ¡Hasta la próxima, Almirante!       ║");
		System.out.println("  ║  Instituto Nacional de Sonzacate     ║");
		System.out.println("  ╚══════════════════════════════════════╝");
		ejecutando = false;
	}

	private String pedirNombre(String etiqueta) {
		String nombre;
		do {
			System.out.printf("  ► Nombre de %s: ", etiqueta);
			nombre = scanner.nextLine().trim();
			if (nombre.isEmpty())
				System.out.println("  ✗ El nombre no puede estar vacío.");
		} while (nombre.isEmpty());
		return nombre.length() > 14 ? nombre.substring(0, 14) : nombre;
	}

	private void registrarResultado() {
		System.out.print("\n  ¿Ganaste la partida? (S/N): ");
		String r = scanner.nextLine().trim().toUpperCase();
		if (r.equals("S"))
			ganadas++;
		else
			perdidas++;
	}

	private int leerOpcion(int min, int max) {
		while (true) {
			String e = scanner.nextLine().trim();
			try {
				int op = Integer.parseInt(e);
				if (op >= min && op <= max)
					return op;
			} catch (NumberFormatException ex) {
			}
			System.out.printf("  ✗ Ingresa un número entre %d y %d: ", min, max);
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
			for (int i = 0; i < 40; i++)
				System.out.println();
		}
	}

	private void pausar(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}