/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacionbingo;

import java.io.DataInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo valdes
 */
public class Bombo extends Thread{
     private static final int PUERTOSERVIDOR = 2000;
    private static final String IPMULTICAST = "233.0.0.1";
    private static final int PUERTOMULTICAST = 2001;
    private static boolean[] numSacados = new boolean[100];
    private static boolean parar = false;

    public Bombo() {
        
    }
    
    public static void main(String[] args) {

        ServerSocket skServidor;
        String mensaje="";
        for (int i = 0; i < 100; i++) {
            numSacados[i] = false;
        }
        new Bombo().start();
        try {
            // Inicio el servidor en el puerto
            skServidor = new ServerSocket(PUERTOSERVIDOR);
            System.out.println("Escucho el puerto " + PUERTOSERVIDOR);
            while (!parar) {
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                parar=true;
                // ATENDER PETICIÓN DEL CLIENTE
                System.out.println("ACEPTO LA CONEXIÓN DEL CLIENTE ....");
                // Creo los flujos de entrada y salida
                DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
                mensaje = flujo_entrada.readUTF();
                System.out.println("Bingo cantado por: " + mensaje);
                // Se cierra la conexión
                flujo_entrada.close();
                skCliente.close();
            }
            mensaje="Bingo cantado por "+mensaje;
            //Creamos el MulticastSocket sin especificar puerto.
            MulticastSocket s = new MulticastSocket();
            // Creamos el grupo multicast:
            InetAddress group = InetAddress.getByName(IPMULTICAST);
            // Creamos un datagrama vacío en principio:
            byte[] buffer = mensaje.getBytes();
            DatagramPacket dgp = new DatagramPacket(buffer, buffer.length, group, PUERTOMULTICAST);
            //Y por último enviamos:
            s.send(dgp);

            // Cerramos el socket.
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Bombo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        try {
            int num;
            String mensaje;
            Random rand = new Random();
            //ahora mando por multicast a todos los clientes el mensaje
            System.out.println("Arrancando el servidor multicast...");
            //Creamos el MulticastSocket sin especificar puerto.
            MulticastSocket s = new MulticastSocket();
            // Creamos el grupo multicast:
            InetAddress group = InetAddress.getByName(IPMULTICAST);
            // Creamos un datagrama vacío en principio:
            byte[] vacio = new byte[0];
            DatagramPacket dgp = new DatagramPacket(vacio, 0, group, PUERTOMULTICAST);
            while (!parar) {
                //numero aleatorio no repetido
                do {
                    num = (int) rand.nextInt(100) + 1;
                } while (numSacados[num-1]);
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Bombo.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(num);
                mensaje = Integer.toString(num);
                // Creamos el buffer a enviar
                byte[] buffer = mensaje.getBytes();
                //Pasamos los datos al datagrama
                dgp.setData(buffer);
                //Establecemos la longitud
                dgp.setLength(buffer.length);
                //Y por último enviamos:
                s.send(dgp);
            }

            // Cerramos el socket.
            s.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
