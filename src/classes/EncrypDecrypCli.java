/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import utilitats.SystemUtils;

/**
 *
 * @author carles
 */
public class EncrypDecrypCli {

    public static final String AES = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final boolean ACTIVAR_ENCRIPTACIO = true;
    private static final boolean DEBUG = false;

    private String clauPublicaClient;
    private String secretClient;
    private BigInteger share_key_client;

    public BigInteger getShare_key_client() {
        return share_key_client;
    }

    public void setShare_key_client(BigInteger share_key_client) {
        this.share_key_client = share_key_client;
    }

    public String getClauPublicaClient() {
        return clauPublicaClient;
    }

    public void setClauPublicaClient(String clauPublicaClient) {
        this.clauPublicaClient = clauPublicaClient;
    }

    /**
     *
     * @param pText
     * @param key
     * @return
     * @throws java.io.IOException
     */
    public static String encryptedText(String pText, byte[] key) throws IOException {

        if (ACTIVAR_ENCRIPTACIO) {

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

            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                    | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        } else {
            return pText;
        }
    }

    /**
     *
     * @param pTextEncrypted
     * @param key
     * @return
     */
    public static String decryptedText(String pTextEncrypted, byte[] key) {

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
    }

    public void clauPublicaClient() throws IOException {

        //Enviaem la clau publica del client
        BigInteger base       = new BigInteger("2");
        BigInteger prime      = new BigInteger("195180477495478597250447434252857037807");
        BigInteger secret     = new BigInteger(128, new Random());
        BigInteger public_key = base.modPow(secret, prime);

        while (secret.toByteArray().length != 16 || public_key.toByteArray().length != 16) {

            secret = new BigInteger(128, new Random());
            public_key = base.modPow(secret, prime);
            
            if(DEBUG){
                SystemUtils.escriuNouLog("Secret: " + secret.toByteArray().length);
                SystemUtils.escriuNouLog("Public: " + public_key.toByteArray().length);
            }

        }

        clauPublicaClient = String.valueOf(public_key);
        secretClient      = String.valueOf(secret);     
    }
 
 
      public void calculClauCompartida(String publicKeyServer) throws IOException {
    
          BigInteger prime = new BigInteger("195180477495478597250447434252857037807");
          BigInteger secret = new BigInteger(secretClient);

          BigInteger server_public_key = new BigInteger(publicKeyServer);

          share_key_client = server_public_key.modPow(secret, prime);

          if(DEBUG) SystemUtils.escriuNouLog("Shared secret client     : " + share_key_client);

      }

}
