package jugadorhumano;
import java.util.List;
import java.util.Scanner;

	public class JugadorHumano extends Jugador {

	    private static Scanner scanner = new Scanner(System.in);

	    private static final String[][] BARCOS_DISPONIBLES = {
	        {"Portaaviones", "5"},
	        {"Acorazado", "4"},
	        {"Crucero", "3"},
	        {"Submarino", "3"},
	        {"Destructor", "2"}
	    };

	    public JugadorHumano(String nombre) {
	        super(nombre);
	    }
	    public void colocarBarcos() {
	        System.out.println("\n=== COLOCACION DE BARCOS ===");
	        System.out.println("Jugador: " + nombre);
	        System.out.println("Barcos a colocar:");
	        for (String[] barco : BARCOS_DISPONIBLES) {
	            System.out.println("  - " + barco[0] + " (" + barco[1] + " casillas)");
	        }
	        System.out.println("\nDesea colocar los barcos:");
	        System.out.println("1. Manualmente");
	        System.out.println("2. Automaticamente (aleatorio)");
	        int opcion = Utilidades.leerOpcion(1, 2);
	        if (opcion == 2) {
	            colocarBarcosAutomaticamente();
	            System.out.println("\nBarcos colocados automaticamente. Mostrando tablero:");
	            mostrarTableroPropio();
	            Utilidades.pausa();
	            return;
	        }
	        colocarBarcosManual();
	    }

	    private void colocarBarcosManual() {
	        for (String[] barcoInfo : BARCOS_DISPONIBLES) {
	            String nombreBarco = barcoInfo[0];
	            int tamano = Integer.parseInt(barcoInfo[1]);
	            colocarBarcoManual(nombreBarco, tamano);
	            mostrarTableroPropio();
	        }
	        System.out.println("\nTodos los barcos han sido colocados exitosamente.");
	        Utilidades.pausa();
	    }

	    private void colocarBarcoManual(String nombreBarco, int tamano) {
	        boolean colocado = false;
	        while (!colocado) {
	            System.out.println("\nColocando: " + nombreBarco + " (" + tamano + " casillas)");
	            System.out.print("Ingrese la coordenada inicial (ej: A1, J10): ");
	            String entrada = scanner.nextLine().trim().toUpperCase();
	            Coordenada inicio = Utilidades.parsearCoordenada(entrada);
	            if (inicio == null) {
	                System.out.println("Error: Coordenada invalida. Use formato como A1, B5, J10.");
	                continue;
	            }
	            System.out.print("Ingrese la orientacion (H = Horizontal, V = Vertical): ");
	            String orientacion = scanner.nextLine().trim().toUpperCase();
	            boolean horizontal = orientacion.equals("H");
	            if (!horizontal && !orientacion.equals("V")) {
	                System.out.println("Error: Orientacion invalida. Use H o V.");
	                continue;
	            }
	            List<Coordenada> posiciones = tablero.generarPosiciones(inicio, horizontal, tamano);
	            if (posiciones == null) {
	                System.out.println("Error: El barco se sale del tablero. Intente de nuevo.");
	                continue;
	            }
	            Barco barco = new Barco(nombreBarco, tamano);
	            if (tablero.colocarBarco(barco, posiciones)) {
	                colocado = true;
	                System.out.println(nombreBarco + " colocado exitosamente.");
	            } else {
	                System.out.println("Error: Las posiciones estan ocupadas o son invalidas. Intente de nuevo.");
	            }
	        }
	    }

	    private void colocarBarcosAutomaticamente() {
	        for (String[] barcoInfo : BARCOS_DISPONIBLES) {
	            String nombreBarco = barcoInfo[0];
	            int tamano = Integer.parseInt(barcoInfo[1]);
	            colocarBarcoAleatorio(nombreBarco, tamano);
	        }
	    }

	    private void colocarBarcoAleatorio(String nombreBarco, int tamano) {
	        boolean colocado = false;
	        while (!colocado) {
	            int fila = (int) (Math.random() * Tablero.TAMANO);
	            int columna = (int) (Math.random() * Tablero.TAMANO);
	            boolean horizontal = Math.random() < 0.5;
	            Coordenada inicio = new Coordenada(fila, columna);
	            List<Coordenada> posiciones = tablero.generarPosiciones(inicio, horizontal, tamano);
	            if (posiciones != null) {
	                Barco barco = new Barco(nombreBarco, tamano);
	                colocado = tablero.colocarBarco(barco, posiciones);
	            }
	        }
	    }

	    public Coordenada obtenerDisparo() {
	        while (true) {
	            System.out.print("\n" + nombre + ", ingrese coordenada de disparo (ej: A1, J10): ");
	            String entrada = scanner.nextLine().trim().toUpperCase();
	            Coordenada coord = Utilidades.parsearCoordenada(entrada);
	            if (coord == null) {
	                System.out.println("Error: Coordenada invalida. Use formato como A1, B5, J10.");
	                continue;
	            }
	            return coord;
	        }
	    }