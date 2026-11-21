import java.io.*;
import java.net.*;

public class ClienteChat {
    private static final String IP = "127.0.0.1";
    private static final int PUERTO = 59420;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(IP, PUERTO);
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.print("Ingresa tu nombre de usuario (ej. poli01): ");
            String miNombre = teclado.readLine();
            salida.println(miNombre);

            // Hilo en segundo plano para mostrar mensajes que lleguen de otros clientes
            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = entradaServidor.readLine()) != null) {
                        System.out.println(linea);
                    }
                } catch (IOException e) {
                    System.out.println("Desconectado del servidor.");
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            // Bucle principal para escribir
            while (true) {
                Thread.sleep(200); // Pequeña pausa para que se muestre la lista de conectados primero
                System.out.print("¿A quién le escribes? (o escribe 'chao' para salir): ");
                String destinatario = teclado.readLine();
                salida.println(destinatario);

                if (destinatario.equalsIgnoreCase("chao")) {
                    break;
                }

                System.out.print("Escribe el mensaje: ");
                String mensaje = teclado.readLine();
                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("chao")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}