package batallanaval;

import java.util.Scanner;

public class Juego {

	private static final int TURNOS_PARA_MOVER = 3;

	private Jugador jugador1;
	private Jugador jugador2;
	private Computadora computadora;
	private Musica musica;
	private Scanner scanner;
	private boolean modo2Jugadores;
	private int turno; // 1 = J1, 2 = J2/CPU
	private boolean juegoActivo;
	private int contadorTurnos;

	public Juego(String nombreJugador, Scanner scanner, Musica musica) {
		this.jugador1 = new Jugador(nombreJugador, scanner);
		this.computadora = new Computadora("Almirante ROBOT");
		this.musica = musica;
		this.scanner = scanner;
		this.modo2Jugadores = false;
		this.turno = 1;
		this.juegoActivo = true;
		this.contadorTurnos = 0;
	}

	public Juego(String nombre1, String nombre2, Scanner scanner, Musica musica) {
		this.jugador1 = new Jugador(nombre1, scanner);
		this.jugador2 = new Jugador(nombre2, scanner);
		this.musica = musica;
		this.scanner = scanner;
		this.modo2Jugadores = true;
		this.turno = 1;
		this.juegoActivo = true;
		this.contadorTurnos = 0;
	}

	public void iniciar() {
		mostrarIntroduccion();

		if (modo2Jugadores) {

			System.out.println("\n  ╔══════════════════════════════════════╗");
			System.out.printf("  ║  TURNO DE: %-26s║%n", jugador1.getNombre());
			System.out.println("  ╚══════════════════════════════════════╝");
			jugador1.colocarBarcosAutomatico();

			pantallaPrivacidad(jugador2.getNombre());

			System.out.println("\n  ╔══════════════════════════════════════╗");
			System.out.printf("  ║  TURNO DE: %-26s║%n", jugador2.getNombre());
			System.out.println("  ╚══════════════════════════════════════╝");
			jugador2.colocarBarcosAutomatico();

		} else {
			jugador1.colocarBarcosAutomatico();
			System.out.println("\n  La computadora posiciona su flota...");
			pausar(1000);
			System.out.println("  ¡Flota enemiga lista!");
		}

		musica.tocarBatalla();

		while (juegoActivo) {
			contadorTurnos++;

			if (contadorTurnos % TURNOS_PARA_MOVER == 0) {
				moverTodosLosBarcos();
			}

			if (turno == 1) {
				turnoJugador(jugador1, modo2Jugadores ? jugador2.getTablero() : computadora.getTablero());
			} else {
				if (modo2Jugadores) {
					pantallaPrivacidad(jugador2.getNombre());
					turnoJugador(jugador2, jugador1.getTablero());
				} else {
					turnoComputadora();
				}
			}

			verificarFinJuego();
			turno = (turno == 1) ? 2 : 1;
		}
	}

	private void moverTodosLosBarcos() {
		if (modo2Jugadores) {
			jugador1.getTablero().moverBarcos();
			jugador2.getTablero().moverBarcos();
		} else {
			jugador1.getTablero().moverBarcos();
			computadora.getTablero().moverBarcos();
		}
		System.out.println("\n  ⚓ ¡Los barcos se han movido!");
		pausar(700);
	}

	private void turnoJugador(Jugador atacante, Tablero objetivoT) {
		limpiarPantalla();

	
		System.out.printf("%n  ┌──── TU FLOTA (%s) ────┐%n", atacante.getNombre());
		atacante.getTablero().mostrarTableroPropio();
		System.out.printf("  Barcos propios hundidos: %d/5%n", atacante.getTablero().getBarcosHundidos());

		System.out.println("\n  ┌──── MAPA DE DISPARO ────┐");
		atacante.getTablero().mostrarTableroEnemigo();
		System.out.printf("  Barcos enemigos hundidos: %d/5%n", objetivoT.getBarcosHundidos());

		int faltan = TURNOS_PARA_MOVER - (contadorTurnos % TURNOS_PARA_MOVER);
		System.out.printf("%n  ⚓ Barcos se mueven en: %d turno(s)%n", faltan);

		System.out.println("\n  ═══════════════════════════════════════");
		System.out.printf("       🎯  TURNO DE: %s%n", atacante.getNombre().toUpperCase());
		System.out.println("  ═══════════════════════════════════════");

		int[] disparo = atacante.realizarDisparo();
		int fila = disparo[0];
		int columna = disparo[1];
		int resultado = objetivoT.recibirDisparo(fila, columna);

		atacante.registrarResultado(fila, columna, resultado);
		mostrarResultado(resultado, fila, columna);
		reproducirSonido(resultado);
		pausar(1400);
	}

	private void turnoComputadora() {
		System.out.println("\n  ═══════════════════════════════════════");
		System.out.println("       🤖  TURNO DEL ALMIRANTE ROBOT");
		System.out.println("  ═══════════════════════════════════════");
		System.out.println("  La IA está apuntando...");
		pausar(1100);

		int[] disparo = computadora.elegirDisparo();
		int fila = disparo[0];
		int columna = disparo[1];
		int resultado = jugador1.getTablero().recibirDisparo(fila, columna);

		computadora.procesarResultado(fila, columna, resultado);

		char col = (char) ('A' + columna);
		System.out.printf("  El robot disparó en: %c%d%n", col, fila + 1);
		mostrarResultado(resultado, fila, columna);
		reproducirSonido(resultado);
		pausar(1400);
	}

	private void verificarFinJuego() {
		if (modo2Jugadores) {
			if (jugador2.getTablero().todosBarcosHundidos()) {
				juegoActivo = false;
				musica.tocarVictoria();
				mostrarVictoria(jugador1);
			} else if (jugador1.getTablero().todosBarcosHundidos()) {
				juegoActivo = false;
				musica.tocarVictoria();
				mostrarVictoria(jugador2);
			}
		} else {
			if (computadora.getTablero().todosBarcosHundidos()) {
				juegoActivo = false;
				musica.tocarVictoria();
				mostrarVictoriaVsCPU();
			} else if (jugador1.getTablero().todosBarcosHundidos()) {
				juegoActivo = false;
				musica.tocarDerrota();
				mostrarDerrotaVsCPU();
			}
		}
	}



	private void mostrarIntroduccion() {
		limpiarPantalla();
		System.out.println("  ╔════════════════════════════════════════╗");
		System.out.println("  ║   ⚓  B A T A L L A   N A V A L  ⚓   ║");
		System.out.println("  ║   Instituto Nacional de Sonzacate      ║");
		System.out.println("  ╠════════════════════════════════════════╣");
		if (modo2Jugadores) {
			System.out.printf("  ║  Modo: %-32s║%n", "2 JUGADORES ⚔");
		} else {
			System.out.printf("  ║  Modo: %-32s║%n", "vs COMPUTADORA 🤖");
		}
		System.out.println("  ╠════════════════════════════════════════╣");
		System.out.println("  ║  ~ Agua   O Fallo   X Impacto  # Hundido ║");
		System.out.printf("  ║  Barcos se mueven cada %-3d turnos      ║%n", TURNOS_PARA_MOVER);
		System.out.println("  ╚════════════════════════════════════════╝");
		System.out.println("  Presiona ENTER para continuar...");
		scanner.nextLine();
	}

	private void pantallaPrivacidad(String nombreSiguiente) {
		limpiarPantalla();
		System.out.println("\n\n\n");
		System.out.println("  ╔══════════════════════════════════════════╗");
		System.out.println("  ║                                          ║");
		System.out.println("  ║   🙈  PASA EL TECLADO AL SIGUIENTE       ║");
		System.out.printf("  ║   Turno de: %-29s║%n", nombreSiguiente);
		System.out.println("  ║                                          ║");
		System.out.println("  ║   Presiona ENTER cuando estés listo...   ║");
		System.out.println("  ║                                          ║");
		System.out.println("  ╚══════════════════════════════════════════╝");
		scanner.nextLine();
		limpiarPantalla();
	}

	private void mostrarResultado(int resultado, int fila, int columna) {
		char col = (char) ('A' + columna);
		System.out.println("\n  ┌──────────────────────────────────┐");
		switch (resultado) {
		case 0:
			System.out.printf("  │  💧  AGUA   → %c%d              │%n", col, fila + 1);
			break;
		case 1:
			System.out.printf("  │  💥  IMPACTO→ %c%d              │%n", col, fila + 1);
			break;
		case 2:
			System.out.printf("  │  🔥  HUNDIDO→ %c%d              │%n", col, fila + 1);
			break;
		case -1:
			System.out.println("  │  ⚠   Celda ya disparada        │");
			break;
		}
		System.out.println("  └──────────────────────────────────┘");
	}

	private void reproducirSonido(int resultado) {
		if (resultado == 0)
			musica.sonarDisparoAgua();
		else if (resultado == 1)
			musica.sonarDisparoBarco();
		else if (resultado == 2)
			musica.sonarHundido();
	}

	private void mostrarVictoria(Jugador ganador) {
		limpiarPantalla();
		System.out.println("\n  ╔══════════════════════════════════════╗");
		System.out.println("  ║   🏆  ¡VICTORIA!  🏆                 ║");
		System.out.printf("  ║   Ganador: %-26s║%n", ganador.getNombre());
		System.out.println("  ╚══════════════════════════════════════╝");
		System.out.println("\n  📊 ESTADÍSTICAS:");
		jugador1.mostrarEstadisticas();
		jugador2.mostrarEstadisticas();
		System.out.println("\n  Presiona ENTER...");
		scanner.nextLine();
	}

	private void mostrarVictoriaVsCPU() {
		limpiarPantalla();
		System.out.println("\n  ╔══════════════════════════════════════╗");
		System.out.println("  ║   🏆  ¡VICTORIA, ALMIRANTE!  🏆      ║");
		System.out.println("  ║   ¡Hundiste toda la flota enemiga!    ║");
		System.out.println("  ╚══════════════════════════════════════╝");
		System.out.println("\n  📊 TUS ESTADÍSTICAS:");
		jugador1.mostrarEstadisticas();
		System.out.println("  Presiona ENTER...");
		scanner.nextLine();
	}

	private void mostrarDerrotaVsCPU() {
		limpiarPantalla();
		System.out.println("\n  ╔══════════════════════════════════════╗");
		System.out.println("  ║   💀  ¡DERROTA, ALMIRANTE!  💀       ║");
		System.out.println("  ║   El Robot hundió toda tu flota...    ║");
		System.out.println("  ╚══════════════════════════════════════╝");
		System.out.println("\n  📊 TUS ESTADÍSTICAS:");
		jugador1.mostrarEstadisticas();
		System.out.println("  Presiona ENTER...");
		scanner.nextLine();
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
			for (int i = 0; i < 35; i++)
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