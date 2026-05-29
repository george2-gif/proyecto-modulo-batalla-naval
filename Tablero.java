package batallanaval;
import java.util.ArrayList;
import java.util.List;
public class Tablero {

	    public static final int TAMANO = 10;
	    public static final char AGUA = '~';
	    public static final char BARCO = 'B';
	    public static final char IMPACTO = 'X';
	    public static final char AGUA_DISPARADA = 'O';
	    public static final char HUNDIDO = 'H';

	    private char[][] matriz;
	    private List<Barco> barcos;
	    private List<Coordenada> disparosFallidos;
	    private List<Coordenada> disparosAcertados;

	    public Tablero() {
	        this.matriz = new char[TAMANO][TAMANO];
	        this.barcos = new ArrayList<>();
	        this.disparosFallidos = new ArrayList<>();
	        this.disparosAcertados = new ArrayList<>();
	        inicializar();
	    }

	    public void inicializar() {
	        for (int i = 0; i < TAMANO; i++) {
	            for (int j = 0; j < TAMANO; j++) {
	                matriz[i][j] = AGUA;
	            }
	        }
	    }

	    public char[][] getMatriz() {
	        return matriz;
	    }

	    public List<Barco> getBarcos() {
	        return barcos;
	    }

	    public boolean coordenadaValida(Coordenada coord) {
	        return coord.getFila() >= 0 && coord.getFila() < TAMANO &&
	               coord.getColumna() >= 0 && coord.getColumna() < TAMANO;
	    }

	    public boolean posicionDisponible(Coordenada coord) {
	        if (!coordenadaValida(coord)) return false;
	        return matriz[coord.getFila()][coord.getColumna()] == AGUA;
	    }

	    public boolean sePuedeColocar(List<Coordenada> posiciones) {
	        for (Coordenada coord : posiciones) {
	            if (!posicionDisponible(coord)) return false;
	        }
	        return true;
	    }

	    public boolean colocarBarco(Barco barco, List<Coordenada> posiciones) {
	        if (!sePuedeColocar(posiciones)) return false;
	        barco.setPosiciones(posiciones);
	        for (Coordenada coord : posiciones) {
	            matriz[coord.getFila()][coord.getColumna()] = BARCO;
	        }
	        barcos.add(barco);
	        return true;
	    }

	    public List<Coordenada> generarPosiciones(Coordenada inicio, boolean horizontal, int tamano) {
	        List<Coordenada> posiciones = new ArrayList<>();
	        int fila = inicio.getFila();
	        int columna = inicio.getColumna();
	        for (int i = 0; i < tamano; i++) {
	            int nuevaFila = horizontal ? fila : fila + i;
	            int nuevaColumna = horizontal ? columna + i : columna;
	            Coordenada coord = new Coordenada(nuevaFila, nuevaColumna);
	            if (!coordenadaValida(coord)) return null;
	            posiciones.add(coord);
	        }
	        return posiciones;
	    }

	    public int registrarDisparo(Coordenada coord) {
	        if (!coordenadaValida(coord)) return -1;
	        int fila = coord.getFila();
	        int columna = coord.getColumna();
	        char estado = matriz[fila][columna];
	        if (estado == IMPACTO || estado == AGUA_DISPARADA || estado == HUNDIDO) return 2;
	        if (estado == BARCO) {
	            matriz[fila][columna] = IMPACTO;
	            disparosAcertados.add(coord);
	            for (Barco barco : barcos) {
	                if (barco.contieneCoordenada(coord)) {
	                    barco.recibirImpacto(coord);
	                    if (barco.estaHundido()) {
	                        marcarBarcoHundido(barco);
	                        return 3;
	                    }
	                    break;
	                }
	            }
	            return 1;
	        }
	        matriz[fila][columna] = AGUA_DISPARADA;
	        disparosFallidos.add(coord);
	        return 0;
	    }

	    private void marcarBarcoHundido(Barco barco) {
	        for (Coordenada coord : barco.getPosiciones()) {
	            matriz[coord.getFila()][coord.getColumna()] = HUNDIDO;
	        }
	    }

	    public boolean todosHundidos() {
	        for (Barco barco : barcos) {
	            if (!barco.estaHundido()) return false;
	        }
	        return true;
	    }

	    public void mostrar(boolean revelarBarcos) {
	        System.out.println();
	        System.out.println("    1   2   3   4   5   6   7   8   9   10");
	        System.out.println("  +---+---+---+---+---+---+---+---+---+---+");
	        for (int i = 0; i < TAMANO; i++) {
	            char letraFila = (char) ('A' + i);
	            System.out.print(letraFila + " |");
	            for (int j = 0; j < TAMANO; j++) {
	                char celda = matriz[i][j];
	                if (!revelarBarcos && celda == BARCO) {
	                    System.out.print(" ~ |");
	                } else {
	                    System.out.print(" " + celda + " |");
	                }
	            }
	            System.out.println();
	            System.out.println("  +---+---+---+---+---+---+---+---+---+---+");
	        }
	        System.out.println();
	    }

	    public void mostrarConLeyenda(boolean revelarBarcos, String nombreJugador) {
	        System.out.println("=== Tablero de " + nombreJugador + " ===");
	        mostrar(revelarBarcos);
	        System.out.println("Leyenda: ~ = Agua | B = Barco | X = Impacto | O = Fallo | H = Hundido");
	        System.out.println();
	    }
	}


		   