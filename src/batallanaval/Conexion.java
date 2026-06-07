package batallanaval;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Maneja la conexión TCP entre las dos computadoras.
 * La misma clase sirve tanto para el Servidor como para el Cliente,
 * solo cambia cómo se ABRE la conexión.
 *
 * USO:
 *   // Computadora 1 (Servidor):
 *   Conexion c = Conexion.comoServidor(5555);
 *
 *   // Computadora 2 (Cliente):
 *   Conexion c = Conexion.comoCliente("192.168.1.5", 5555);
 *
 *   // Ambos usan igual después:
 *   c.enviar(Mensaje.disparo(3, 5));
 *   Mensaje m = c.recibir();
 *   c.cerrar();
 */
public class Conexion {

    private static final int PUERTO_DEFAULT = 5555;

    private Socket             socket;
    private ServerSocket       serverSocket; // Solo lo usa el servidor
    private ObjectOutputStream out;
    private ObjectInputStream  in;

    // ── Constructor privado ─────────────────────────────────
    private Conexion() {}

    // ── Abrir como SERVIDOR ─────────────────────────────────

    /**
     * Espera a que el otro jugador se conecte.
     * @param puerto Puerto TCP (usa 5555 si no sabes cuál poner)
     * @return Conexion lista para usar
     */
    public static Conexion comoServidor(int puerto) throws IOException {
        Conexion c = new Conexion();
        System.out.println("\n  ⚓ Esperando oponente en puerto " + puerto + "...");
        System.out.println("  (Dile a tu rival tu IP para que se conecte)");
        mostrarMiIP();

        c.serverSocket = new ServerSocket(puerto);
        c.socket       = c.serverSocket.accept();  // ← se queda aquí hasta que alguien se conecte

        // IMPORTANTE: primero out, luego in (si no, Java se bloquea)
        c.out = new ObjectOutputStream(c.socket.getOutputStream());
        c.out.flush();
        c.in  = new ObjectInputStream(c.socket.getInputStream());

        System.out.println("  ✅ ¡Oponente conectado desde "
                + c.socket.getInetAddress().getHostAddress() + "!");
        return c;
    }

    /** Versión con puerto por defecto 5555. */
    public static Conexion comoServidor() throws IOException {
        return comoServidor(PUERTO_DEFAULT);
    }

    // ── Abrir como CLIENTE ──────────────────────────────────

    /**
     * Se conecta al servidor (la otra computadora).
     * @param ip    IP de la computadora servidor (ej: "192.168.1.5")
     * @param puerto Puerto TCP (el mismo que usó el servidor)
     * @return Conexion lista para usar
     */
    public static Conexion comoCliente(String ip, int puerto) throws IOException {
        Conexion c = new Conexion();
        System.out.println("\n  🔌 Conectando a " + ip + ":" + puerto + " ...");

        c.socket = new Socket(ip, puerto);

        // IMPORTANTE: primero out, luego in
        c.out = new ObjectOutputStream(c.socket.getOutputStream());
        c.out.flush();
        c.in  = new ObjectInputStream(c.socket.getInputStream());

        System.out.println("  ✅ ¡Conectado al servidor!");
        return c;
    }

    /** Versión con puerto por defecto 5555. */
    public static Conexion comoCliente(String ip) throws IOException {
        return comoCliente(ip, PUERTO_DEFAULT);
    }

    // ── Enviar / Recibir ────────────────────────────────────

    /**
     * Envía un Mensaje al otro jugador.
     */
    public void enviar(Mensaje m) throws IOException {
        out.writeObject(m);
        out.flush();
        out.reset(); // Importante: evita que Java cachée el objeto
    }

    /**
     * Espera y recibe el próximo Mensaje del otro jugador.
     * Este método BLOQUEA hasta que llegue algo.
     */
    public Mensaje recibir() throws IOException, ClassNotFoundException {
        return (Mensaje) in.readObject();
    }

    // ── Cerrar ──────────────────────────────────────────────

    public void cerrar() {
        try {
            if (in           != null) in.close();
            if (out          != null) out.close();
            if (socket       != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            // ignorar errores al cerrar
        }
    }

    public boolean estaConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    // ── Utilidad: mostrar la IP local ───────────────────────
    private static void mostrarMiIP() {
        try {
            java.net.InetAddress ip = java.net.InetAddress.getLocalHost();
            System.out.println("  💻 Tu IP local: " + ip.getHostAddress());
        } catch (Exception e) {
            System.out.println("  (No se pudo detectar tu IP automáticamente)");
        }
    }
}
