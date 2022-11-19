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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import utilitats.SystemUtils;

/**
 *
 * @author carles
 */
@TestMethodOrder(OrderAnnotation.class)
public class JUnitTestCridesUsuaris {
    
    //Variables definines per fer el joc de proves
    private static int rol;
    private static int id_conn_correcte;
    private static int id_conn_erroni;
    private static int id_user;
    
    public JUnitTestCridesUsuaris() {
    }
    
 

    @Test
    @Order(1)
     public void t1_testSimulacioLoginCorrecteIncorrecte() throws InterruptedException{  //comoprova un login incorrecte

        assertNotEquals( testSimulacioLoginErroni(0, "carles","pwdcarless"),1);
    } 
    
    @Test
    @Order(2)
     public void t2_testSimulacioLoginCorrecte() throws InterruptedException{  //comoprova un login incorrecte

        assertEquals( testSimulacioLoginCorrecte(0, "carles","pwdcarles"),1);
    }
     
    @Test
    @Order(3)
     public void t3_alta() throws InterruptedException, IOException{  //comoprova un login incorrecte

         assertEquals( alta(id_conn_correcte,"silvia,pwdsilvia,silvia,olivar,1,1,1"),1);
    }  
    
    @Test
    @Order(4)
     public void t4_buscarIdUsuari() throws InterruptedException, IOException{  //comoprova un login incorrecte

        id_user =  buscarIdUsuari(id_conn_correcte,"silvia");
        assertNotEquals(id_user,0);
    }  
  
    @Test
    @Order(5)
     public void t5_testModificacio() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals( modificacio(id_conn_correcte,id_user,",silvia,pwdsilvia,SILVIA,OLIVAR,2,3,1"),1);
    }
     
    @Test
    @Order(6)
     public void t6_baixa() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals( baixa(id_conn_correcte, id_user),1);
    }
     
     
    @Test
    @Order(7)
     public void t7_testSimulacioLogOut() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals(testSimulacioLogOut(id_conn_correcte),1);
    } 
    
    
    
     public  int testSimulacioLoginErroni(int id_conn,String usuari, String contrasenya) throws InterruptedException {

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
             id_conn_erroni = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
              
            SystemUtils.escriuNouLog("Fem el login amb l'usuari " + usuari + " i contrasenya  incorrecte :" + contrasenya + " - El resulta és erroni ");
            SystemUtils.escriuNouLog("resposta servidor  es un id  valit    : " + id_conn_erroni);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if ( id_conn_erroni != 0) {
                
                resultat =1;
                rol = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
            }
        } catch (IOException ex) {
            
        }
        return resultat;
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
            id_conn_correcte = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
              
            SystemUtils.escriuNouLog("Fem el login amb l'usuari " + usuari + " i contrasenya  correcte :" + contrasenya + " - El resulta és correcte  ");
            SystemUtils.escriuNouLog("resposta servidor  es un id  valit    : " + id_conn_correcte);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (id_conn_correcte != 0) {
                
                resultat =1;
                rol = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
            }
        } catch (IOException ex) {
        
        }
          
        return resultat;
    }
    
     
       /**
     * Mètode que simula elLogOut d'un usuari
     * @param id_conn 
     * @return  
     */
     public int testSimulacioLogOut(int id_conn) {

        Socket sc;
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
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_EXIT",edc.getShare_key_client().toByteArray()));
            
            System.out.println("LogOut  del id_conn : "+ id_conn + " realitzat correctament.");

        } catch (IOException ex) {
          
        }
        
        return 1;
    }
    
     /**
    * Mètode que busca el id d'un usuari
     * @param id_conn
    * @param usuari
    * @return el id que té l'usuari a la Bd's
    */
    public int buscarIdUsuari(int id_conn, String usuari) {

        Socket sc;
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
            
            //Executo la consulta de la crida per sortir
            out.writeUTF(SystemUtils.encryptedText(id_conn+",USER_FIND," + usuari,edc.getShare_key_client().toByteArray()));
            
            //Llegir el numero total de registres de la consulta
            int id_trobat =Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            
            //Si troba l'usuari torna el seu id
            System.out.println("Buscar el id de l'usuari : " + usuari + "a la Bd's d'usuaris, id trobat : "+ id_trobat);
            return id_trobat;

        } catch (IOException ex) {
           
        }
        //Sinó troba l'usuari retorna 0 
        return 0;
    } 
     
    
      /**
     * Aquest mètode fa una crida  a la crida USER_NEW per simular l'alta d'un nou
     * usuari en la Bd's
     * Genera un nou usuari i recull el resultat de l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param params passem els registres del camps dels usuari separat per "," en format text
    */
    public int alta(int id_conn, String params) {

        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        int resultat=0;
        
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
    
            //Executo la consulta de la crida per fer l'alta del nou usuari
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_NEW," + params,edc.getShare_key_client().toByteArray()));
            SystemUtils.escriuNouLog("Crida d'una alta : " + id_conn + ",USER_NEW," + params);
 
           //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé  
           resultat = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            System.out.println("Resultat de la consulta : " + resultat);
            
        } catch (IOException ex) {
         
        }
        return resultat;
    }
   
    
       /**
     * Aquest mètode fa una crida  a la crida USER_MODIFI per simular la modificacio d'un
     * usuari en la Bd's
     * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
     * l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param id_key id que té el usuari en la Bd's
     * @param sql
    */ 
   public int modificacio(int id_conn,int id_key,String sql){
        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        int resultat =0;
       
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
                       
            //El primer parametre es el id a modificar
            out.writeUTF(SystemUtils.encryptedText(id_conn+",USER_MODIFI," + id_key 
                        + sql,edc.getShare_key_client().toByteArray()));

            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            resultat = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            SystemUtils.escriuNouLog("Resultat de la modificacio : " + resultat); 

        } catch (IOException ex) {
           
        }
        return resultat;
    }
     
     /**
     * Aquest mètode fa una crida  a la crida USER_DELETE per simular la baixa d'un
     * usuari en la Bd's
     * Elinima un usuari  i mostre per consola el resultat de  l'operació
     * 
     * @param id_conn passem el id_connexió obtingut al fer login.
     * @param id_key id que té el usuari en la Bd's
     * @return 
    */ 
    public int baixa(int id_conn, int id_key) {
        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        int resultat =0;
        
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
            
            //Enviem al servidor la crida per fer la baixa d'un usuari
            out.writeUTF(SystemUtils.encryptedText(id_conn +",USER_DELETE," + id_key,edc.getShare_key_client().toByteArray()));
            
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
            resultat = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            System.out.println("Resultat de la baixa : " +resultat);
       
        } catch (IOException ex) {
          
        }
        return resultat;
    }
    
     
}
