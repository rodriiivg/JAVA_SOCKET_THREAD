/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncompeticioncarrera;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author rodrigo valdes
 */
public class Competicion {
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Gestionamos que entren a la vez todos los hilos (podría hacerse también con CyclicBarrier)
        CountDownLatch cuentaAtras = new CountDownLatch(1);
        for (int i = 1; i <= 10; i++) {
            Corredor c=new Corredor(i,cuentaAtras);
            c.start();
        }
        cuentaAtras.countDown(); //solo basta con descontar uno (se pondrán en marcha todos los hilos
    }
    
}
