/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncompeticioncarrera;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo valdes
 */
public class Corredor  extends Thread {

    private static final String IPSERVIDOR = "localhost";
    private static final int PUERTOSERVIDOR = 2000;
    private static String ipMulticast="233.0.0.1";
    private static int puertoMulticast=2001;
    private String nombHilo;
    private CountDownLatch cuentaAtras;

    public Corredor(int numHilo, CountDownLatch cuentaAtras) {
        nombHilo = "Corredor" + numHilo;
        this.cuentaAtras = cuentaAtras;
    }

    @Override
    public void run() {
        String mensaje;

        try {
            cuentaAtras.await(); //espera hasta que el contador de cuenta atrás sea 0
        } catch (InterruptedException ex) {
            Logger.getLogger(Corredor.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //Envio TCP

            Socket skCliente = new Socket(IPSERVIDOR, PUERTOSERVIDOR);
            // Creo el flujo de entrada y salida
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());

            flujo_salida.writeUTF(nombHilo); //hace la solicitud al servidor

            flujo_salida.close();
            skCliente.close();
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
            mensaje = new String();
            while (!mensaje.equals("FIN")) {

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
                if (!mensaje.equals("FIN")) System.out.println(mensaje);
            }
            System.out.println(mensaje);
            s.leaveGroup(group);
            // Cerramos el socket:

            s.close();

        } catch (IOException ex) {
            Logger.getLogger(Corredor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
