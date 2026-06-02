package batallanaval;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║         BATALLA NAVAL - JAVA CONSOLE             ║
 * ║                                                  ║
 * ║   Instituto Nacional de Sonzacate                ║
 * ║   Proyecto: Resolución de Problemas              ║
 * ║   Prof. Kevin Antonio Valenzuela                 ║
 * ║                                                  ║
 * ║   Descripción:                                   ║
 * ║   Juego clásico de Batalla Naval para 1 jugador  ║
 * ║   contra la computadora. Tablero 10x10, 5        ║
 * ║   barcos por jugador, música MIDI integrada.     ║
 * ╚══════════════════════════════════════════════════╝
 *
 * Clases:
 *   Main         → Punto de entrada.
 *   Menu         → Menú principal y navegación.
 *   Juego        → Lógica y flujo de la partida.
 *   Jugador      → Jugador humano (colocación y disparos).
 *   Computadora  → IA enemiga con estrategia de ataque.
 *   Tablero      → Cuadrícula 10x10 con barcos y disparos.
 *   Barco        → Entidad barco con impactos y hundimiento.
 *   Musica       → Música MIDI y efectos de sonido.
 */
public class Main {

    public static void main(String[] args) {
        // Punto de entrada: crear y mostrar el menú principal
        Menu menu = new Menu();
        menu.mostrar();
    }
}
