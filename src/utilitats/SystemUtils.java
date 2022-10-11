/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package utilitats;

import classes.ServerFilUsuaris;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cfuga
 */
public final class SystemUtils {
    
    //Genera un nou registre a l'arxiu de log's del programa.
    public static void escriuNouLog(String log) throws IOException{
        
         File f = new File("logs.txt");
            if (!f.exists()) {
                try {
                    System.out.println("SERVER_CREATE_NEW_LOG_FILE_LOGS.TXT");
                    f.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
         FileWriter fw = new FileWriter(f, true);
         fw.write(agafarDataHoraSistema()[0] + ";" + agafarDataHoraSistema()[1] + " - " + log + "\r\n");
         System.out.println(log);
         fw.close();
    }
     
     
    //Agada la data del sistema i el torna la data i la hora en un String 
    //Separat per una ,
    public static String[] agafarDataHoraSistema() {
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String data = dateFormat.format(date);
        String[] campsData = data.split(",");
        
        return campsData;
    }
    
    
    public static int generaNumAleatorio(int minimo, int maximo) {
        int num = (int) Math.floor((Math.random() * (maximo - minimo + 1) + (minimo)));
        return num;
    }
}
