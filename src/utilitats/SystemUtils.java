/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package utilitats;

import classes.ServerFilUsuaris;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.TiqServerMain;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Carles Fugarolas
 */
public final class SystemUtils {

     /**
     * Aquest mètode genera un nou registre a l'arxiu de log's del programa.
     */
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
        //Afageix una nova linia a l'arxiu
         FileWriter fw = new FileWriter(f, true);
         fw.write(agafarDataHoraSistema()[0] + ";" + agafarDataHoraSistema()[1] + " - " + log + "\r\n");
         System.out.println(log);
         fw.close();
    }
   
    /**
     * Aquest mètode  static agafaa la data i la hor del sistema i
     * el torna la data i la hora en un String
     * @return una cadena amb la data i la hora del sistema
     */
    public static String[] agafarDataHoraSistema() {
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String data = dateFormat.format(date);
        String[] campsData = data.split(",");
        
        return campsData;
    }
    
    /**
     * Aquest mètode l static que genera un número aleatori entre un interval definit
     * en els paràmetres d'entrada s'utilitza per genera el id_sessio al fer el login
     * @param minim enter positiu més petit que el parametre maxim.
     * @param maxim enter positiu més gran  que el parametre minim.
     * @return un mumero enter aleatori que està entre el valor minim i el maxim
     */
    public static int generaNumAleatorio(int minim, int maxim) {
        int num = (int) Math.floor((Math.random() * (maxim - minim + 1) + (minim)));
        return num;
    }
    
    /**
     * Aquest mètode llegueix l'arxiu de configuració del servidor en format texte
     * @return d'un String amb l'adreça ip del servidor de Bd's el seu format  xxx.xxx.xxx.xxx 
     */
    public static String obtenirIpConfig(){
        File fileCfg = new File("config.txt");
        String ip="";
        //Si l'arxiu no existeix el genera de nou buit
        //faltaria generar una ip amb format vàlit
        if (!fileCfg.exists()) {
            try {
                escriuNouLog("SERVER_CREATE_NEW_CONFIG_INI_FILE");
                fileCfg.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Llegim la ip del servidor bd's
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileCfg));
            String configIp;
            while ((configIp = br.readLine()) != null) {
                ip = configIp;
            }
            //Tancar l'arxiu
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Retorna el texte de l'adreça IP 
        return ip;
    }
    
     /**
     * Aquest mètode és l'encarregat d'encriptar la contrasenya aplicant
     * SHA-256 de la contrasenya original i ser guardada en aquest format a la Bd's
     * @author Carles Fugarolas
     * @param password la contrasenya en texta pla introduida a la hora de fer 
     * l'alta d'un nou usuari
     * @return de la contrasenya del resultat de SHA-256
     */
    public static String convertirSHA256(String password) {
	MessageDigest md = null;
	try {
		md = MessageDigest.getInstance("SHA-256");
	} 
	catch (NoSuchAlgorithmException e) {		
		e.printStackTrace();
		return null;
	}
	    
	byte[] hash = md.digest(password.getBytes());
	StringBuffer sb = new StringBuffer();
	    
	for(byte b : hash) {        
		sb.append(String.format("%02x", b));
	}
	    
	return sb.toString();
}
    
/**
     * 
     * @param selectBasic sql bàsica de la consulta afegir els paràmetres
     * @param missatge paràmetres introduits del client en la crida
     * @return la setencia sql formatejada correctament amb els paràmetres rebuts
     * @throws IOException 
     */
    public static String formatLlistat(String selectBasic, String[] missatge) throws IOException {

        SystemUtils.escriuNouLog("Util format QUERY's");
        //Select amb la base de la consulta
        String sql = selectBasic;
    
        //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
        switch (missatge[2]) {
            case "0":
                sql = sql + "order by id";         
                break;
            case "1":              
                sql = sql + "where " + missatge[3]; 
                break;
            case "2":
                sql = sql + "order by " + missatge[3];
                break;
            case "3":
                sql = sql + " where " + missatge[3] + " order by " + missatge[4];          
                break;
            default:
        }
        SystemUtils.escriuNouLog("Resultat de la setencia final SQL : " + sql);

        return sql;
    }    
    
    
}
