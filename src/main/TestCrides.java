/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilitats.SystemUtils;

/**
 *
 * @author ADC Team
 */
public class TestCrides {

    public static void main(String[] args) throws IOException {

        //llistat();
        alta();
    }
    
    public static void llistat(){
        
         //Instaciar el fitxer de logs per accedir-hi
         File f = new File("logs.txt");
 
     
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
            
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
           
           SystemUtils.escriuNouLog(f, "Resposta_svr:"+ resposta_svr);
                            
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           //Executo la consulta de la crida per sortir
           out.writeUTF("QUERY_ALL_USERS");
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public  static void alta(){
        
        
         //Instaciar el fitxer de logs per accedir-hi
         File f = new File("logs.txt");
         
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
            
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
           
           SystemUtils.escriuNouLog(f, "Resposta_svr:"+ resposta_svr);
                            
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           //Executo la consulta de la crida per sortir
           out.writeUTF("NEW_USER");
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }

}
