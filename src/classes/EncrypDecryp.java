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
public class EncrypDecryp {

    public static final String AES = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final boolean ACTIVAR_ENCRIPTACIO = true;
    private static final boolean DEBUG = false;

    private String clauPublicaServidor;
    private String clauPublicaClient;
    private BigInteger share_key_server;
   

    public BigInteger getShare_key_server() {
        return share_key_server;
    }

    public void setShare_key_server(BigInteger share_key_server) {
        this.share_key_server = share_key_server;
    }

    public String getClauPublicaServidor() {
        return clauPublicaServidor;
    }

    public void setClauPublicaServidor(String clauPublicaServidor) {
        this.clauPublicaServidor = clauPublicaServidor;
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

    /**
     * Mètode que genera la clau pública del servidor i calcula la clau
     * compartida
     * @throws java.io.IOException
     */
    public void clausServer() throws IOException  {

        BigInteger base       = new BigInteger("2");
        BigInteger prime      = new BigInteger("195180477495478597250447434252857037807");
        BigInteger secret     = new BigInteger(128, new Random());
        BigInteger public_key = base.modPow(secret, prime);

        BigInteger client_public_key = new BigInteger(clauPublicaClient);
        share_key_server             = client_public_key.modPow(secret, prime);

        while (secret.toByteArray().length != 16
                || public_key.toByteArray().length != 16
                || share_key_server.toByteArray().length != 16) {

            secret            = new BigInteger(128, new Random());
            public_key        = base.modPow(secret, prime);
            share_key_server  = client_public_key.modPow(secret, prime);
            if(DEBUG) {
                System.out.println("Secret       : " + secret.toByteArray().length);
                System.out.println("Public       : " + public_key.toByteArray().length);
                System.out.println("Shared Server: " + share_key_server.toByteArray().length);
            }
        }
        
        clauPublicaServidor = String.valueOf(public_key);
        if(DEBUG) SystemUtils.escriuNouLog("Valor share_key en el  server      : " + share_key_server);
        
    }

}
