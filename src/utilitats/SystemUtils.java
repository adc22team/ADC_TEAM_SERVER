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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import main.TiqServerMain;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author cfuga
 */
public final class SystemUtils {
    
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
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
    
    
    public static String obtenirIpConfig(){
        File fileCfg = new File("config.txt");
        String ip="";

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

        return ip;
    }
    
    
    public static String encryptedText(String pText) {

        String encryptedBase64 = null;

        /*
     * key is 16 zero bytes
         */
        SecretKey secretKey = new SecretKeySpec(new byte[16], "AES");

        try {

            byte[] iv = new byte[GCM_IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] cipherText = cipher.doFinal(pText.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

            encryptedBase64 = Base64.encodeBase64String(encrypted);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | NoSuchPaddingException e) {
            // Do nothing
        }

        return encryptedBase64;

    }

    public static String decryptedText(String pTextEncrypted) {

        String decryptedBase64 = null;

        /*
     * key is 16 zero bytes
         */
        SecretKey secretKey = new SecretKeySpec(new byte[16], "AES");

        try {

            byte[] decoded = Base64.decodeBase64(pTextEncrypted);

            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] cipherText = cipher.doFinal(decoded, GCM_IV_LENGTH, decoded.length - GCM_IV_LENGTH);

            decryptedBase64 = new String(cipherText, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | NoSuchPaddingException e) {
            // Do nothing
        }

        return decryptedBase64;

    }
    
}
