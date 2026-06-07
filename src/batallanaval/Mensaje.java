package batallanaval;

import java.io.Serializable;

/**
 * Objeto que viaja por la red entre los dos jugadores.
 * Serializable = Java puede convertirlo en bytes para enviarlo por socket.
 *
 * Tipos de mensaje:
 *   DISPARO   → un jugador envía sus coordenadas de ataque
 *   RESULTADO → el receptor procesa el disparo y devuelve el resultado
 *   FIN       → alguien ganó / se rindió
 *   LISTO     → señal de que el jugador ya colocó sus barcos
 */
public class Mensaje implements Serializable {

    // Necesario para que Java serialice correctamente el objeto
    private static final long serialVersionUID = 1L;

    // ── Tipos de mensaje ────────────────────────────────────
    public enum Tipo { DISPARO, RESULTADO, FIN, LISTO }

    // ── Campos ─────────────────────────────────────────────
    public Tipo    tipo;
    public int     fila;
    public int     columna;
    public int     resultado;   // 0=agua, 1=impacto, 2=hundido
    public String  texto;       // mensaje de texto libre (para FIN, etc.)

    // ── Constructores ───────────────────────────────────────

    /** Crea un mensaje de tipo DISPARO con las coordenadas. */
    public static Mensaje disparo(int fila, int columna) {
        Mensaje m = new Mensaje();
        m.tipo    = Tipo.DISPARO;
        m.fila    = fila;
        m.columna = columna;
        return m;
    }

    /** Crea un mensaje de tipo RESULTADO con el valor devuelto por recibirDisparo(). */
    public static Mensaje resultado(int fila, int columna, int resultado) {
        Mensaje m = new Mensaje();
        m.tipo      = Tipo.RESULTADO;
        m.fila      = fila;
        m.columna   = columna;
        m.resultado = resultado;
        return m;
    }

    /** Crea un mensaje de FIN de partida. */
    public static Mensaje fin(String motivo) {
        Mensaje m = new Mensaje();
        m.tipo  = Tipo.FIN;
        m.texto = motivo;
        return m;
    }

    /** Señal de que el jugador terminó de colocar sus barcos. */
    public static Mensaje listo() {
        Mensaje m = new Mensaje();
        m.tipo = Tipo.LISTO;
        return m;
    }

    private Mensaje() {}
}
