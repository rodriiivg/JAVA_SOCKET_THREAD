/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncompeticioncarrera;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo valdes
 */
public class Meta extends Thread {

    private static final int PUERTOSERVIDOR = 2000;

    private Socket skCliente;
    private Llegada llegada;

    public Meta(Socket s, Llegada llegada) {
        skCliente = s;
        this.llegada = llegada;
    }

    public static void main(String[] args) {
        // Inicio el servidor en el puerto para la comunicación por TCP
        ServerSocket skServidor;
        int cont = 0;
        Llegada llegada = new Llegada(0); //se llevara el control del orden de llegada de cada hilo
        try {
            skServidor = new ServerSocket(PUERTOSERVIDOR);
            System.out.println("Escucho el puerto " + PUERTOSERVIDOR);
            while (cont != 10) {
                cont++;
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                // Atiendo al cliente mediante un thread (para la concurrencia)
                new Meta(skCliente, llegada).start();
            }
            skServidor.close();

        } catch (IOException ex) {
            Logger.getLogger(Meta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        llegada.posicion(skCliente);           
    }
    
}
