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
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.TiqServerMain;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Carles Fugarolas
 */
public final class SystemUtils {
    
     
    public static final String AES = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final boolean ACTIVAR_ENCRIPTACIO = true;

     /**
     * Aquest mètode genera un nou registre a l'arxiu de log's del programa.
     * @param log
     * @throws java.io.IOException
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
     * Aquest mètode  static agafaa la data i la hor del sistema i
     * el torna la data i la hora en un String
     * @return una cadena amb la data i la hora del sistema
     */
    public static String agafarDataHoraSistemaTiq() {
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy;HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String data = dateFormat.format(date);
   //     String[] campsData = data.split(",");
        
        return data;
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
    public static String formatLlistatTiq(String selectBasic, String[] missatge) throws IOException {

        SystemUtils.escriuNouLog("Util format QUERY's");
        //Select amb la base de la consulta
        String sql = selectBasic;
    
        //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
        switch (missatge[2]) {
            case "0":
                sql = sql + "order by id_tiq";         
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
 
     /**
      * 
      * @param pText
      * @param key
      * @return 
     * @throws java.io.IOException 
      */     
   /*   public static String encryptedText(String pText, byte[] key) throws IOException {
          
        if (ACTIVAR_ENCRIPTACIO){  
       
            try {
                byte[] iv = new byte[GCM_IV_LENGTH];
                (new SecureRandom()).nextBytes(iv);

                Cipher cipher = Cipher.getInstance(AES);
                GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);

                byte[] cipherText = cipher.doFinal(pText.getBytes(StandardCharsets.UTF_8));
                byte[] encrypted = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, encrypted, 0, iv.length);
                System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

                String pTextEncrypted = Base64.getEncoder().encodeToString(encrypted);
         //     SystemUtils.escriuNouLog("value : " + pTextEncrypted);
                
                return pTextEncrypted;
                
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                    NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
       
        } else{
            return pText;
        }    
    }
    */
    /**
     * 
     * @param pTextEncrypted
     * @param key
     * @return 
     */
 /*   public static String decryptedText(String pTextEncrypted, byte[] key) {
      
        if (ACTIVAR_ENCRIPTACIO) {
            try {

                byte[] decoded = Base64.getDecoder().decode(pTextEncrypted);

                byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);

                Cipher cipher = Cipher.getInstance(AES);
                GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);

                byte[] cipherText = cipher.doFinal(decoded, GCM_IV_LENGTH, decoded.length - GCM_IV_LENGTH);

                return new String(cipherText, StandardCharsets.UTF_8);
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                    | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        } else {
            return pTextEncrypted;
        }
    }*/
 /**
  * 
  * @param clientPublicKeyUTF
  * @return
  * @throws IOException 
  */
    /*
  public static String clausServer(String clientPublicKeyUTF) throws IOException{
        
        BigInteger base       = new BigInteger("2");
        BigInteger prime      = new BigInteger("195180477495478597250447434252857037807");
        BigInteger secret     = new BigInteger(128, new Random());
        BigInteger public_key = base.modPow(secret, prime);

 //       System.out.println("Valor de clientPublicKeyUTF : " + clientPublicKeyUTF );
        BigInteger client_public_key = new BigInteger(clientPublicKeyUTF);
        BigInteger shared_secret = client_public_key.modPow(secret, prime);

        while (secret.toByteArray().length != 16 || 
                    public_key.toByteArray().length != 16 ||
                          shared_secret.toByteArray().length != 16) {
            
            secret = new BigInteger(128, new Random());
            public_key = base.modPow(secret, prime);
            shared_secret = client_public_key.modPow(secret, prime);
    //      SystemUtils.escriuNouLog("Secret: " + secret.toByteArray().length);
    //      SystemUtils.escriuNouLog("Public: " + public_key.toByteArray().length);
    //      SystemUtils.escriuNouLog("Shared Server: " + shared_secret.toByteArray().length);
            
        }

  //    System.out.println("Client public key abans de convertir  : " + public_key);
  //    System.out.println("Shared secret abans de convertir      : " + shared_secret);
  
        String claus_publica_share_server = String.valueOf(public_key)+","+String.valueOf(shared_secret);
        
    //  System.out.println("Servidor  public key           : " + public_key);
    //  System.out.println("Shared secret del servidor     : " + shared_secret);      
        return claus_publica_share_server;
        
    }
  */
 /* 
    public static String clauPublicaClient() throws IOException {

        //Enviaem la clau publica del client
        BigInteger base = new BigInteger("2");
        BigInteger prime = new BigInteger("195180477495478597250447434252857037807");
        BigInteger secret = new BigInteger(128, new Random());
        BigInteger public_key = base.modPow(secret, prime);

        while (secret.toByteArray().length != 16 || public_key.toByteArray().length != 16) {

            secret = new BigInteger(128, new Random());
            public_key = base.modPow(secret, prime);
         // SystemUtils.escriuNouLog("Secret: " + secret.toByteArray().length);
        //  SystemUtils.escriuNouLog("Public: " + public_key.toByteArray().length);

        }
  
        String clau_publica_client_secret = String.valueOf(public_key) + "," + String.valueOf(secret);   
        
        return clau_publica_client_secret;
    }
 
 */
    /*
      public static BigInteger calculClauCompartida(String publicKeyServer,String secretString) throws IOException {
    
          BigInteger prime = new BigInteger("195180477495478597250447434252857037807");
          BigInteger secret = new BigInteger(secretString);

          BigInteger server_public_key = new BigInteger(publicKeyServer);
    //    SystemUtils.escriuNouLog("Valor server_public_key part client rebuda del servidor: " + server_public_key);

          BigInteger shared_secret = server_public_key.modPow(secret, prime);

    //    SystemUtils.escriuNouLog("Server public key : " + server_public_key);
          SystemUtils.escriuNouLog("Shared secret client     : " + shared_secret);

          return shared_secret;
      }
      
           */ 
    
}
