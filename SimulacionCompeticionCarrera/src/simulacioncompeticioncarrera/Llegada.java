/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncompeticioncarrera;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo valdes
 */
public class Llegada {
     private static String ipMulticast = "233.0.0.1";
    private static int puertoMulticast = 2001;
    private int ordenLlegada;

    public Llegada(int ordenLlegada) {
        this.ordenLlegada = ordenLlegada;
    }

    public synchronized void posicion(Socket skCliente) {
        String corredor;
        String mensaje="";
        String posicion = "";
        ordenLlegada++;
        DataInputStream flujo_entrada;
        try {
            flujo_entrada = new DataInputStream(skCliente.getInputStream());
            corredor = flujo_entrada.readUTF();
            switch (ordenLlegada) {
                case 1:
                    posicion = "primer";
                    break;
                case 2:
                    posicion = "segundo";
                    break;
                case 3:
                    posicion = "tercer";
                    break;
                case 4:
                    posicion = "cuarto";
                    break;
                case 5:
                    posicion = "quinto";
                    break;
                case 6:
                    posicion = "sexto";
                    break;
                case 7:
                    posicion = "septimo";
                    break;
                case 8:
                    posicion = "octavo";
                    break;
                case 9:
                    posicion = "noveno";
                    break;
                case 10:
                    posicion = "decimo";
                    break;

            }

            mensaje = "El " + corredor + " llego en " + posicion + " lugar.";
            System.out.println(mensaje);
            mensaje = corredor + " te has clasificado en " + posicion + " puesto.";

            // Se cierra la conexión
            flujo_entrada.close();
            skCliente.close();
            
            

        } catch (IOException ex) {
            Logger.getLogger(Llegada.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //Creamos el MulticastSocket.
            MulticastSocket s = new MulticastSocket();
            // Creamos el grupo multicast:
            InetAddress group = InetAddress.getByName(ipMulticast);
            // Creamos el buffer a enviar
            byte[] buffer = mensaje.getBytes();
            DatagramPacket dgp = new DatagramPacket(buffer, buffer.length, group, puertoMulticast);
            //Y por último enviamos:
            s.send(dgp);
            if (ordenLlegada == 10) { //llegaron todos los corredores
                //envio FIN por multicast para parar los corredores que esperan por multicast
                mensaje = "FIN";
                // Creamos el buffer a enviar
                buffer = mensaje.getBytes();
                //Pasamos los datos al datagrama
                dgp.setData(buffer);
                //Establecemos la longitud
                dgp.setLength(buffer.length);
                //Y por último enviamos:
                s.send(dgp);
            }
            // Cerramos el socket.
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Llegada.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
}
