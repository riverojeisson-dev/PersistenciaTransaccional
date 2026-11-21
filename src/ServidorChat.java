import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorChat {
    private static final int PUERTO = 59420;
    // Mapa para guardar: nombreUsuario -> canal de salida hacia ese cliente
    private static Map<String, PrintWriter> clientes = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado y contestando OK");

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = servidor.accept();
                new Thread(new ManejadorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    // Muestra en la consola del servidor quiénes están conectados actualmente
    private static synchronized void mostrarConectados() {
        System.out.println("Usuarios conectados actualmente: " + clientes.keySet());
    }

    private static class ManejadorCliente implements Runnable {
        private Socket socket;
        private String usuario;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // 1. Recibir nombre del usuario y guardarlo
                usuario = entrada.readLine();
                synchronized (clientes) {
                    clientes.put(usuario, salida);
                }
                System.out.println("Usuario \"" + usuario + "\" conectado");
                mostrarConectados();

                // 2. Bucle para leer destinatario y mensaje
                while (true) {
                    // Enviar lista actual al cliente para que sepa a quién escribirle
                    salida.println("Conectados: " + clientes.keySet());

                    String destinatario = entrada.readLine();
                    if (destinatario == null || destinatario.equalsIgnoreCase("chao")) {
                        break;
                    }

                    String mensaje = entrada.readLine();
                    if (mensaje == null || mensaje.equalsIgnoreCase("chao")) {
                        break;
                    }

                    // 3. Buscar al destinatario y reenviarle el mensaje
                    PrintWriter salidaDestino = clientes.get(destinatario);
                    if (salidaDestino != null) {
                        salidaDestino.println("\n[Mensaje de " + usuario + "]: " + mensaje);
                        salida.println("-> Enviado a " + destinatario);
                        System.out.println(usuario + " -> " + destinatario + ": " + mensaje);
                    } else {
                        salida.println("-> El usuario \"" + destinatario + "\" no existe o no está conectado.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Se perdió conexión con " + usuario);
            } finally {
                // 4. Eliminar de la lista cuando salga
                if (usuario != null) {
                    synchronized (clientes) {
                        clientes.remove(usuario);
                    }
                    System.out.println("El usuario \"" + usuario + "\" abandonó");
                    mostrarConectados();
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}