/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacionbingo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo valdes
 */
public class Carton extends Thread{
     private String nombreCarton;
    private int[] numeros = new int[10];
    private static String ipMulticast;
    private static int puertoMulticast;
    private static String ipTcp;
    private static int puertoTcp;
    private int aciertos = 0;
    private CountDownLatch cuentaAtras;

    public Carton(String nombreCarton, String ip, int puerto, String ipTcp, int puertoTcp, CountDownLatch cuentaAtras) {
        Random rand = new Random();
        this.nombreCarton = nombreCarton;
        ipMulticast = ip;
        puertoMulticast = puerto;
        this.ipTcp = ipTcp;
        this.puertoTcp = puertoTcp;
        this.cuentaAtras=cuentaAtras;
        System.out.print(nombreCarton + ": ");
        for (int i = 0; i < 10; i++) {
            numeros[i] = (int) rand.nextInt(100) + 1;
            System.out.print(numeros[i] + " ");
        }
        System.out.println();

    }

    public void comprobarAciertos(String mum) {
        int numero = Integer.parseInt(mum);
        for (int i = 0; i < 10; i++) {
            if (numeros[i] == numero) {
                aciertos++;
                break;
            }
        }
    }

    @Override
    public void run() {
        try {

            try {
                cuentaAtras.await(); //espera hasta que el contador de cuenta atrás sea 0
            } catch (InterruptedException ex) {
                Logger.getLogger(Carton.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Creamos un socket multicast
            MulticastSocket s;

            s = new MulticastSocket(puertoMulticast);
            //Configuramos el grupo (IP) a la que nos conectaremos:
            InetAddress group = InetAddress.getByName(ipMulticast);
            //Nos unimos al grupo:
            s.joinGroup(group);
            //Leemos los paquetes enviados por el servidor multicast:
            // Los paquetes enviados son de 256 bytes de maximo (es adaptable)
            byte[] buffer = new byte[50];
            String mensaje = new String();
            while (mensaje.length() < 4) {

                //Creamos el datagrama en el que recibiremos el paquete
                //del socket:
                DatagramPacket dgp = new DatagramPacket(buffer, buffer.length);
                // Recibimos el paquete del socket:
                s.receive(dgp);
                // Adaptamos la información al tamaño de lo que se envió
                //(por si se envió menos de 256):
                byte[] buffer2 = new byte[dgp.getLength()];
                // Copiamos los datos en el nuevo array de tamaño adecuado:
                System.arraycopy(dgp.getData(), 0, buffer2, 0, dgp.getLength());
                //Vemos los datos recibidos por pantalla:
                mensaje = new String(buffer2);
                System.out.println(mensaje);
                if (mensaje.length() < 4) {
                    comprobarAciertos(mensaje);
                    if (aciertos == 10) { //tablero completado
                        //se manda por TCP el mensaje al servidor
                        Socket skCliente = new Socket(ipTcp, puertoTcp);

                        // Creo los flujos de entrada y salida
                        DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());

                        // TAREAS QUE REALIZA EL CLIENTE
                        flujo_salida.writeUTF(nombreCarton); //se conecta al servidor mandando el texto
                        flujo_salida.close();
                        skCliente.close();
                    }
                }
            }
            System.out.println(mensaje);
            s.leaveGroup(group);
            // Cerramos el socket:

            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Carton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
