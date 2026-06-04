package batallanaval;

import java.util.Random;

/**
 * Clase que representa la inteligencia artificial del juego.
 * La IA tiene dos modos:
 *   - BÚSQUEDA: dispara aleatoriamente hasta encontrar un barco.
 *   - ATAQUE:   una vez que impacta, sigue disparando en dirección al barco.
 */
public class Computadora {

    // ── Constantes de modo ─────────────────────────────────
    private static final int MODO_BUSQUEDA = 0;
    private static final int MODO_ATAQUE   = 1;

    // ── Atributos ──────────────────────────────────────────
    private String  nombre;
    private Tablero tablero;
    private Random  random;
    private int     modo;

    // Para el modo ataque
    private int  ultimaFilaImpacto;
    private int  ultimaColImpacto;
    private int  primeraFilaImpacto;
    private int  primeraColImpacto;
    private int  direccion;       // 0=arriba, 1=abajo, 2=izq, 3=der
    private boolean direccionDefinida;

    // Historial de disparos
    private boolean[][] disparado;
    private int disparosTotales;
    private int disparosAcertados;

    // ── Constructor ────────────────────────────────────────
    public Computadora(String nombre) {
        this.nombre            = nombre;
        this.tablero           = new Tablero();
        this.random            = new Random();
        this.disparado         = new boolean[Tablero.TAMANIO][Tablero.TAMANIO];
        this.modo              = MODO_BUSQUEDA;
        this.direccionDefinida = false;
        this.disparosTotales   = 0;
        this.disparosAcertados = 0;
        colocarBarcosAutomatico();
    }

    // ── Colocación automática ──────────────────────────────

    /** Coloca los 5 barcos de la IA aleatoriamente. */
    private void colocarBarcosAutomatico() {
        Barco[] flota = {
            new Barco(Barco.PORTAAVIONES, 1),
            new Barco(Barco.ACORAZADO,    1),
            new Barco(Barco.CRUCERO,      1),
            new Barco(Barco.SUBMARINO,    1),
            new Barco(Barco.DESTRUCTOR,   1)
        };

        for (Barco barco : flota) {
            boolean colocado = false;
            int intentos = 0;
            while (!colocado && intentos < 1000) {
                int  fila       = random.nextInt(Tablero.TAMANIO);
                int  columna    = random.nextInt(Tablero.TAMANIO);
                boolean horiz   = random.nextBoolean();
                colocado = tablero.colocarBarco(barco, fila, columna, horiz);
                intentos++;
            }
        }
    }

    // ── Lógica de disparo ──────────────────────────────────

    /**
     * La IA elige dónde disparar según su modo actual.
     * @return arreglo {fila, columna}
     */
    public int[] elegirDisparo() {
        int[] celda;
        if (modo == MODO_ATAQUE) {
            celda = dispararModoAtaque();
        } else {
            celda = dispararModoBusqueda();
        }
        disparado[celda[0]][celda[1]] = true;
        disparosTotales++;
        return celda;
    }

    /** Disparo aleatorio con patrón de tablero de ajedrez (más eficiente). */
    private int[] dispararModoBusqueda() {
        // Primero intentar celdas del patrón de damero
        int intentos = 0;
        while (intentos < 200) {
            int fila    = random.nextInt(Tablero.TAMANIO);
            int columna = random.nextInt(Tablero.TAMANIO);
            // Patrón damero: (fila + columna) % 2 == 0
            if (!disparado[fila][columna] && (fila + columna) % 2 == 0) {
                return new int[]{fila, columna};
            }
            intentos++;
        }
        // Si no queda patrón de damero, disparar a cualquier celda libre
        return dispararAleatorio();
    }

    private int[] dispararAleatorio() {
        int fila, columna;
        int intentos = 0;
        do {
            fila    = random.nextInt(Tablero.TAMANIO);
            columna = random.nextInt(Tablero.TAMANIO);
            intentos++;
        } while (disparado[fila][columna] && intentos < 500);
        return new int[]{fila, columna};
    }

    /** Disparo inteligente: sigue la dirección del barco detectado. */
    private int[] dispararModoAtaque() {
        if (!direccionDefinida) {
            // Probar las 4 direcciones desde el último impacto
            return probarDirecciones();
        } else {
            // Seguir en la dirección definida
            return seguirDireccion();
        }
    }

    private int[] probarDirecciones() {
        int[][] candidatos = {
            {ultimaFilaImpacto - 1, ultimaColImpacto},  // Arriba
            {ultimaFilaImpacto + 1, ultimaColImpacto},  // Abajo
            {ultimaFilaImpacto, ultimaColImpacto - 1},  // Izquierda
            {ultimaFilaImpacto, ultimaColImpacto + 1}   // Derecha
        };
        for (int d = 0; d < 4; d++) {
            int f = candidatos[d][0];
            int c = candidatos[d][1];
            if (esCeldaValida(f, c) && !disparado[f][c]) {
                direccion = d;
                return new int[]{f, c};
            }
        }
        // Sin opciones: volver a búsqueda
        modo = MODO_BUSQUEDA;
        return dispararModoBusqueda();
    }

    private int[] seguirDireccion() {
        int fila    = ultimaFilaImpacto;
        int columna = ultimaColImpacto;
        int df = 0, dc = 0;
        switch (direccion) {
            case 0: df = -1; break; // Arriba
            case 1: df =  1; break; // Abajo
            case 2: dc = -1; break; // Izquierda
            case 3: dc =  1; break; // Derecha
        }
        int nf = fila + df;
        int nc = columna + dc;
        if (esCeldaValida(nf, nc) && !disparado[nf][nc]) {
            return new int[]{nf, nc};
        }
        // Cambiar dirección opuesta desde el primer impacto
        direccionDefinida = false;
        ultimaFilaImpacto = primeraFilaImpacto;
        ultimaColImpacto  = primeraColImpacto;
        return probarDirecciones();
    }

    /**
     * Recibe el resultado del último disparo de la IA.
     * @param fila     Fila disparada.
     * @param columna  Columna disparada.
     * @param resultado 0=agua, 1=impacto, 2=hundido.
     */
    public void procesarResultado(int fila, int columna, int resultado) {
        tablero.registrarDisparoPropio(fila, columna, resultado);
        if (resultado == 1) {
            // Impacto: activar modo ataque
            disparosAcertados++;
            if (modo == MODO_BUSQUEDA) {
                modo                = MODO_ATAQUE;
                primeraFilaImpacto  = fila;
                primeraColImpacto   = columna;
                direccionDefinida   = false;
            } else {
                direccionDefinida = true;
            }
            ultimaFilaImpacto = fila;
            ultimaColImpacto  = columna;
        } else if (resultado == 2) {
            // Hundido: volver a modo búsqueda
            disparosAcertados++;
            modo              = MODO_BUSQUEDA;
            direccionDefinida = false;
        }
    }

    private boolean esCeldaValida(int fila, int columna) {
        return fila >= 0 && fila < Tablero.TAMANIO
            && columna >= 0 && columna < Tablero.TAMANIO;
    }

    // ── Estadísticas ───────────────────────────────────────

    public double getPrecision() {
        if (disparosTotales == 0) return 0.0;
        return (double) disparosAcertados / disparosTotales * 100.0;
    }

    // ── Getters ────────────────────────────────────────────
    public String  getNombre()            { return nombre; }
    public Tablero getTablero()           { return tablero; }
    public int     getDisparosTotales()   { return disparosTotales; }
    public int     getDisparosAcertados() { return disparosAcertados; }
}
