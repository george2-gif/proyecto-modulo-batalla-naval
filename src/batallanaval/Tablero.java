package batallanaval;

import java.util.Random;

public class Tablero {

	public static final int TAMANIO = 10;
	public static final char AGUA = '~';
	public static final char IMPACTO = 'X';
	public static final char FALLO = 'O';
	public static final char HUNDIDO = '#';

	private char[][] grilla;
	private char[][] grillaEnemigo;
	private Barco[] barcos;
	private boolean[] horizontal;
	private int totalBarcos;
	private int barcosHundidos;
	private Random random;

	public Tablero() {
		grilla = new char[TAMANIO][TAMANIO];
		grillaEnemigo = new char[TAMANIO][TAMANIO];
		barcos = new Barco[5];
		horizontal = new boolean[5];
		totalBarcos = 0;
		barcosHundidos = 0;
		random = new Random();
		inicializar();
	}

	private void inicializar() {
		for (int i = 0; i < TAMANIO; i++)
			for (int j = 0; j < TAMANIO; j++) {
				grilla[i][j] = AGUA;
				grillaEnemigo[i][j] = AGUA;
			}
	}

	public boolean colocarBarco(Barco barco, int fila, int columna, boolean esHorizontal) {
		int tam = barco.getTamanio();
		if (esHorizontal && columna + tam > TAMANIO)
			return false;
		if (!esHorizontal && fila + tam > TAMANIO)
			return false;

		for (int i = 0; i < tam; i++) {
			int f = esHorizontal ? fila : fila + i;
			int c = esHorizontal ? columna + i : columna;
			if (grilla[f][c] != AGUA)
				return false;
		}

		int[] filas = new int[tam];
		int[] columnas = new int[tam];
		for (int i = 0; i < tam; i++) {
			int f = esHorizontal ? fila : fila + i;
			int c = esHorizontal ? columna + i : columna;
			grilla[f][c] = barco.getSimbolo().charAt(0);
			filas[i] = f;
			columnas[i] = c;
		}
		barco.colocarEn(filas, columnas);
		horizontal[totalBarcos] = esHorizontal;
		barcos[totalBarcos++] = barco;
		return true;
	}

	public void moverBarcos() {
		for (int idx = 0; idx < totalBarcos; idx++) {
			Barco b = barcos[idx];
			if (b == null || b.isHundido())
				continue;
			if (b.getImpactos() > 0)
				continue; 

			boolean horiz = horizontal[idx];
			int dir = b.getDireccionMovimiento(); // +1 o -1

			
			int[] filasActuales = b.getFilasOcupadas();
			int[] colsActuales = b.getColumnasOcupadas();
			int tam = b.getTamanio();

			int[] nuevasFil = new int[tam];
			int[] nuevasCol = new int[tam];
			for (int i = 0; i < tam; i++) {
				nuevasFil[i] = horiz ? filasActuales[i] : filasActuales[i] + dir;
				nuevasCol[i] = horiz ? colsActuales[i] + dir : colsActuales[i];
			}

			if (!posicionValida(nuevasFil, nuevasCol, idx)) {

				dir = -dir;
				b.setDireccionMovimiento(dir);
				for (int i = 0; i < tam; i++) {
					nuevasFil[i] = horiz ? filasActuales[i] : filasActuales[i] + dir;
					nuevasCol[i] = horiz ? colsActuales[i] + dir : colsActuales[i];
				}
				if (!posicionValida(nuevasFil, nuevasCol, idx))
					continue;
			}

			for (int i = 0; i < tam; i++)
				grilla[filasActuales[i]][colsActuales[i]] = AGUA;

			for (int i = 0; i < tam; i++)
				grilla[nuevasFil[i]][nuevasCol[i]] = b.getSimbolo().charAt(0);

			b.colocarEn(nuevasFil, nuevasCol);
		}
	}

	private boolean posicionValida(int[] filas, int[] cols, int idxBarcoActual) {
		for (int i = 0; i < filas.length; i++) {
			int f = filas[i], c = cols[i];
			if (f < 0 || f >= TAMANIO || c < 0 || c >= TAMANIO)
				return false;
			char celda = grilla[f][c];

			if (celda != AGUA && celda != FALLO) {

				boolean esMismo = false;
				Barco b = barcos[idxBarcoActual];
				if (b.ocupaCelda(f, c))
					esMismo = true;
				if (!esMismo)
					return false;
			}
		}
		return true;
	}

	public int recibirDisparo(int fila, int columna) {
		char celda = grilla[fila][columna];
		if (celda == IMPACTO || celda == FALLO || celda == HUNDIDO)
			return -1;

		if (celda == AGUA) {
			grilla[fila][columna] = FALLO;
			return 0;
		}

		grilla[fila][columna] = IMPACTO;
		for (int i = 0; i < totalBarcos; i++) {
			if (barcos[i] != null && barcos[i].ocupaCelda(fila, columna)) {
				boolean hundido = barcos[i].recibirImpacto();
				if (hundido) {
					marcarHundido(barcos[i]);
					barcosHundidos++;
					return 2;
				}
				return 1;
			}
		}
		return 1;
	}

	private void marcarHundido(Barco barco) {
		for (int f = 0; f < TAMANIO; f++)
			for (int c = 0; c < TAMANIO; c++)
				if (barco.ocupaCelda(f, c))
					grilla[f][c] = HUNDIDO;
	}

	public void registrarDisparoPropio(int fila, int columna, int resultado) {
		if (resultado == 0)
			grillaEnemigo[fila][columna] = FALLO;
		else if (resultado == 1)
			grillaEnemigo[fila][columna] = IMPACTO;
		else if (resultado == 2)
			grillaEnemigo[fila][columna] = HUNDIDO;
	}

	public boolean yaDisparoEn(int fila, int columna) {
		char c = grillaEnemigo[fila][columna];
		return c == IMPACTO || c == FALLO || c == HUNDIDO;
	}

	public boolean todosBarcosHundidos() {
		return barcosHundidos >= totalBarcos && totalBarcos > 0;
	}

	public void mostrarTableroPropio() {
		imprimirGrilla(grilla, false);
	}

	public void mostrarTableroSoloUnBarco(Barco barcoVisible) {
		char[][] vista = new char[TAMANIO][TAMANIO];
		for (int f = 0; f < TAMANIO; f++)
			for (int c = 0; c < TAMANIO; c++) {
				char celda = grilla[f][c];

				if (celda != AGUA && !barcoVisible.ocupaCelda(f, c)) {
					vista[f][c] = AGUA;
				} else {
					vista[f][c] = celda;
				}
			}
		imprimirGrilla(vista, false);
	}

	public void mostrarTableroEnemigo() {
		imprimirGrilla(grillaEnemigo, false);
	}

	private void imprimirGrilla(char[][] g, boolean ocultarBarcos) {
		System.out.println("     A   B   C   D   E   F   G   H   I   J");
		System.out.println("   ╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗");
		for (int i = 0; i < TAMANIO; i++) {
			System.out.printf(" %2d ║", i + 1);
			for (int j = 0; j < TAMANIO; j++) {
				char c = g[i][j];
				System.out.print(" " + colorCelda(c) + " ║");
			}
			System.out.println();
			if (i < TAMANIO - 1)
				System.out.println("   ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣");
		}
		System.out.println("   ╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝");
	}

	private String colorCelda(char c) {
		switch (c) {
		case AGUA:
			return "\u001B[34m~\u001B[0m";
		case IMPACTO:
			return "\u001B[31mX\u001B[0m";
		case FALLO:
			return "\u001B[37mO\u001B[0m";
		case HUNDIDO:
			return "\u001B[35m#\u001B[0m";
		default:
			return "\u001B[32m" + c + "\u001B[0m";
		}
	}

	public int getTotalBarcos() {
		return totalBarcos;
	}

	public int getBarcosHundidos() {
		return barcosHundidos;
	}

	public char[][] getGrilla() {
		return grilla;
	}

	public char[][] getGrillaEnemigo() {
		return grillaEnemigo;
	}

	public Barco[] getBarcos() {
		return barcos;
	}
}