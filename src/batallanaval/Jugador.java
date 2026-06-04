package batallanaval;

import java.util.Random;
import java.util.Scanner;

public class Jugador {

	private String nombre;
	private Tablero tablero;
	private int disparosTotales;
	private int disparosAcertados;
	private Scanner scanner;
	private Random random;

	public Jugador(String nombre, Scanner scanner) {
		this.nombre = nombre;
		this.tablero = new Tablero();
		this.disparosTotales = 0;
		this.disparosAcertados = 0;
		this.scanner = scanner;
		this.random = new Random();
	}

	public void colocarBarcosAutomatico() {
		Barco[] flota = crearFlota();

		System.out.println("\n  ╔══════════════════════════════════════╗");
		System.out.println("  ║   ⚓  COLOCANDO TU FLOTA...  ⚓      ║");
		System.out.println("  ╚══════════════════════════════════════╝");

		for (Barco barco : flota) {
			boolean colocado = false;
			int intentos = 0;
			while (!colocado && intentos < 1000) {
				int fila = random.nextInt(Tablero.TAMANIO);
				int col = random.nextInt(Tablero.TAMANIO);
				boolean horiz = random.nextBoolean();
				colocado = tablero.colocarBarco(barco, fila, col, horiz);
				intentos++;
			}

			System.out.println("\n  ✔ " + barco.getNombre() + " colocado  (tamaño: " + barco.getTamanio() + ")");
			tablero.mostrarTableroSoloUnBarco(barco);
			pausar(600);
		}

		System.out.println("\n  ¡Flota completa! Tablero final:");
		tablero.mostrarTableroPropio();
		System.out.println("\n  Presiona ENTER para iniciar la batalla...");
		scanner.nextLine();
	}

	public int[] realizarDisparo() {
		int fila, columna;
		boolean valido = false;

		do {
			char colLetra = pedirColumna();
			columna = colLetra - 'A';
			fila = pedirFila() - 1;

			if (tablero.yaDisparoEn(fila, columna)) {
				System.out.println("  ✗ ¡Ya disparaste ahí! Elige otra celda.");
			} else {
				valido = true;
			}
		} while (!valido);

		disparosTotales++;
		return new int[] { fila, columna };
	}

	public void registrarResultado(int fila, int columna, int resultado) {
		tablero.registrarDisparoPropio(fila, columna, resultado);
		if (resultado == 1 || resultado == 2)
			disparosAcertados++;
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
				if (f >= 1 && f <= 10)
					return f;
			} catch (NumberFormatException ex) {
				/* ignorar */ }
			System.out.println("  ✗ Ingresa un número entre 1 y 10.");
		}
	}

	public double getPrecision() {
		if (disparosTotales == 0)
			return 0.0;
		return (double) disparosAcertados / disparosTotales * 100.0;
	}

	public void mostrarEstadisticas() {
		System.out.println("  ┌──────────────────────────────────────┐");
		System.out.printf("  │  Jugador            : %-15s │%n", nombre);
		System.out.printf("  │  Disparos totales   : %-3d             │%n", disparosTotales);
		System.out.printf("  │  Disparos acertados : %-3d             │%n", disparosAcertados);
		System.out.printf("  │  Precisión          : %-6.1f%%         │%n", getPrecision());
		System.out.println("  └──────────────────────────────────────┘");
	}

	private static Barco[] crearFlota() {
		return new Barco[] { new Barco(Barco.PORTAAVIONES, 5), new Barco(Barco.ACORAZADO, 4),
				new Barco(Barco.CRUCERO, 3), new Barco(Barco.SUBMARINO, 3), new Barco(Barco.DESTRUCTOR, 2) };
	}

	private void pausar(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public String getNombre() {
		return nombre;
	}

	public Tablero getTablero() {
		return tablero;
	}

	public int getDisparosTotales() {
		return disparosTotales;
	}

	public int getDisparosAcertados() {
		return disparosAcertados;
	}
}