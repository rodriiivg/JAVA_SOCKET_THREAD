/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacionbingo;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author rodrigo valdes
 */
public class Bingo {
      private static final String IPSERVIDOR = "localhost";
    private static final int PUERTOSERVIDOR = 2000;
    private static final String IPMULTICAST="233.0.0.1";
    private static final int PUERTOMULTICAST=2001;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Carton c;
        CountDownLatch cuentaAtras = new CountDownLatch(1);        
        for (int i = 1; i <= 10; i++) {
            c=new Carton("Carton"+i,IPMULTICAST,PUERTOMULTICAST,IPSERVIDOR,PUERTOSERVIDOR,cuentaAtras);
            c.start();
        }
        cuentaAtras.countDown(); //solo basta con descontar uno (se pondrán en marcha todos los hilos        
    }
    
    
}
