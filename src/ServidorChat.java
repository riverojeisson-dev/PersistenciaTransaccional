import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat {
    private static final int PUERTO = 59420;

    public static void main(String[] args) {
        System.out.println("Servidor iniciado y contestando OK");

        try (ServerSocket servidorSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = servidorSocket.accept();
                new Thread(new ManejadorCliente(socketCliente)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket socket;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
            ) {
                nombreUsuario = entrada.readLine();
                System.out.println("Usuario \"" + nombreUsuario + "\" conectado");

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    if (mensaje.equalsIgnoreCase("chao")) {
                        System.out.println("El usuario \"" + nombreUsuario + "\" abandonó");
                        break;
                    }
                    System.out.println(nombreUsuario + "--> " + mensaje);
                }
            } catch (IOException e) {
                System.out.println("Conexión interrumpida con el usuario.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
