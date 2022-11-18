/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import classes.EncrypDecrypCli;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import test.TestCridesUsuaris;
import utilitats.SystemUtils;

/**
 *
 * @author carles
 */
public class JUnitTestCridesUsuaris {
    
    //Variables definines per fer el joc de proves
    private static int rol;
    private static int resposta_svr_id;
    
    public JUnitTestCridesUsuaris() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
     @Test
     public void loginCorrecte() throws InterruptedException{  //comoprova un login incorrecte

        assertEquals( testSimulacioLoginCorrecte(0, "carles","pwdcarles"),1);
    }
     
     @Test
     public void loginCorrecteIncorrecte() throws InterruptedException{  //comoprova un login incorrecte

        assertNotEquals( testSimulacioLoginCorrecte(0, "carles","pwdcarless"),1);
    } 
     
     @Test
     public void logOut() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals(testSimulacioLogOut(resposta_svr_id),1);
    } 
    
     
     public  int testSimulacioLoginCorrecte(int id_conn,String usuari, String contrasenya) throws InterruptedException {

        Socket sc;
        int resultat=0;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
       
            //Cálcul clau pública client
            edc.clauPublicaClient();
            //Enviem la clau pública del client al servidor
            out.writeUTF(edc.getClauPublicaClient());
            //llegim la clau pública del servidor i generem la clau compartida
            edc.calculClauCompartida(in.readUTF());
           
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",LOGIN," + usuari + "," + contrasenya ,edc.getShare_key_client().toByteArray()));
            //Recullim el id_sessio vàlit
            resposta_svr_id = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
              
            SystemUtils.escriuNouLog("Fem el login amb l'usuari " + usuari + "i contrasenya  correcte :" + contrasenya + " - El resulta és CORRECTE  ");
            SystemUtils.escriuNouLog("resposta servidor  es un id  valit    : " + resposta_svr_id);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (resposta_svr_id != 0) {
                
                resultat =1;
                rol = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultat;
    }
    
     
       /**
     * Mètode que simula elLogOut d'un usuari
     * @param id_conn 
     * @throws java.io.IOException 
     */
     public int testSimulacioLogOut(int id_conn) throws IOException {

        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        SystemUtils.escriuNouLog("Ara femt el logOut ....... ");

        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            //Cálcul clau pública client
            edc.clauPublicaClient();
            //Enviem la clau pública del client al servidor
            out.writeUTF(edc.getClauPublicaClient());
            //llegim la clau pública del servidor i generem la clau compartida
            edc.calculClauCompartida(in.readUTF());
            
           //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_EXIT",edc.getShare_key_client().toByteArray()));
            
            SystemUtils.escriuNouLog("LogOut realitzat correctament proves usuaris ");

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 1;
    }
    
}
