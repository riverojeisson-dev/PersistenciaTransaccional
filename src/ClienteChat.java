import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteChat {
    private static final String IP_SERVIDOR = "127.0.0.1";
    private static final int PUERTO = 59420;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(IP_SERVIDOR, PUERTO);
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entradaTeclado = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.print("Escriba su nombre de usuario (ej. poli01): ");
            String nombreUsuario = entradaTeclado.readLine();

            salida.println(nombreUsuario);
            System.out.println("Conectado. Escriba sus mensajes (escriba 'chao' para salir):");

            String mensajeTeclado;
            while (true) {
                System.out.print(nombreUsuario + "--> ");
                mensajeTeclado = entradaTeclado.readLine();

                salida.println(mensajeTeclado);

                if (mensajeTeclado.equalsIgnoreCase("chao")) {
                    System.out.println("Finalizando chat...");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}