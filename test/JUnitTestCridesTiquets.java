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
import java.util.ArrayList;
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
public class JUnitTestCridesTiquets {
    
    //Variables definines per fer el joc de proves
    private static int rol;
    private static int id_conn_correcte;
    private static int id_tiq;
    
    public JUnitTestCridesTiquets() {
    }
       
    @Test
    @Order(1)
     public void t2_testSimulacioLoginCorrecte() throws InterruptedException{  //comoprova un login incorrecte

        assertEquals( testSimulacioLoginCorrecte(0, "carles","pwdcarles"),1);
    }
 
    @Test
    @Order(2)
     public void t3_llistat() throws InterruptedException, IOException{  //comoprova un login incorrecte

         assertNotEquals( llistat(id_conn_correcte),1);   
    }
     
    @Test
    @Order(3)
     public void t3_alta_baixa_modificacioTiquet() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals( alta(id_conn_correcte,"incidencia3,comentari3,1,3,1,1"),1);
        id_tiq =  buscarIdTiquet(id_conn_correcte,"incidencia3");
        assertEquals( modificacio(id_conn_correcte,id_tiq,",incidencia33,comentari33,1,3,1,1,31/12/2022;23:59:59"),1);
        assertEquals( baixa(id_conn_correcte, id_tiq),1);
         
    }  
    
    @Test
    @Order(4)
     public void t4_buscarIdTiquet() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertNotEquals(buscarIdTiquet(id_conn_correcte,"incidencia2"),0);
    }  
 
     @Test
    @Order(5)
     public void t5_testCanviEstat() throws InterruptedException, IOException{  //comoprova un canvi d'estat d'un tiquet

        assertEquals(canviEstat(id_conn_correcte,"4",String.valueOf(buscarIdTiquet(id_conn_correcte,"incidencia2"))),1);
        assertEquals(canviEstat(id_conn_correcte,"0",String.valueOf(buscarIdTiquet(id_conn_correcte,"incidencia2"))),1);
                
    }  

    @Test
    @Order(6)
     public void t6_testSimulacioLogOut() throws InterruptedException, IOException{  //comoprova un login incorrecte

        assertEquals(testSimulacioLogOut(id_conn_correcte),1);
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
            out.writeUTF(edc.encryptedText(id_conn + ",LOGIN," + usuari + "," + contrasenya ,edc.getShare_key_client().toByteArray()));
            //Recullim el id_sessio vàlit
            id_conn_correcte = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
              
            SystemUtils.escriuNouLog("Fem el login amb l'usuari " + usuari + " i contrasenya  correcte :" + contrasenya + " - El resulta és correcte  ");
            SystemUtils.escriuNouLog("resposta servidor  es un id  valit    : " + id_conn_correcte);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (id_conn_correcte != 0) {
                
                resultat =1;
                rol = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
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
            out.writeUTF(edc.encryptedText(id_conn + ",USER_EXIT",edc.getShare_key_client().toByteArray()));
            
            System.out.println("LogOut  del id_conn : "+ id_conn + " realitzat correctament.");

        } catch (IOException ex) {
          
        }
        
        return 1;
    }
    
     /**
    * Mètode que busca el id d'un usuari
     * @param id_conn
     * @param incidencia
    * @return el id que té l'usuari a la Bd's
    */
    public int buscarIdTiquet(int id_conn, String incidencia) {

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
            out.writeUTF(edc.encryptedText(id_conn+",TIQU_FIND," + incidencia,edc.getShare_key_client().toByteArray()));
            
            //Llegir el numero total de registres de la consulta
            int id_trobat =Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            
            //Si troba l'usuari torna el seu id
            System.out.println("Buscar el id_tiq del tiquet : " + incidencia + "a la Bd's de tiquets, id_tiq trobat : "+ id_trobat);
            return id_trobat;

        } catch (IOException ex) {
           
        }
        //Sinó troba l'usuari retorna 0 
        return 0;
    } 
     
    
     /**
    * Mètode per fer el canvi d'un estat d'un tiquet
     * @param id_conn
     * @param nouEstat
     * @param id_tiq
     * @return el id que té l'usuari a la Bd's
    */
    public static int canviEstat(int id_conn, String nouEstat, String id_tiq) {

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
            
            //Executo la consulta de la crida per canviar l'estat d'un tiquet
            out.writeUTF(edc.encryptedText(id_conn+",TIQU_STATUS," + nouEstat +","+ id_tiq,edc.getShare_key_client().toByteArray()));
            
            //Llegir el resultat de l'operació
            int resultat =Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            
            //Si troba l'usuari torna el seu id
            return resultat;

        } catch (IOException ex) {
        }
        //Sinó pot fer el canvi d'estat retorna 0 
        return 0;
    }
  
    
      /**
     * Aquest mètode fa una crida  a la crida TIQU_NEW per simular l'alta d'un nou
     * tiquet en la Bd's
     * Genera un nou usuari i recull el resultat de l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param params passem els registres del camps dels usuari separat per "," en format text
     * @return 
    */
    public int alta(int id_conn, String params) {

        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        int resultat=0;
        
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in   = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
   
            //Cálcul clau pública client
            edc.clauPublicaClient();
            //Enviem la clau pública del client al servidor
            out.writeUTF(edc.getClauPublicaClient());
            //llegim la clau pública del servidor i generem la clau compartida
            edc.calculClauCompartida(in.readUTF());
    
            //Executo la consulta de la crida per fer l'alta del nou usuari
            out.writeUTF(edc.encryptedText(id_conn + ",TIQU_NEW," + params,edc.getShare_key_client().toByteArray()));
            SystemUtils.escriuNouLog("Crida d'una alta d'un tiquet : " + id_conn + ",TIQU_NEW," + params);
 
           //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé  
           resultat = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            System.out.println("Resultat de la consulta : " + resultat);
            
        } catch (IOException ex) {
         
        }
        return resultat;
    }
   
    
       /**
     * Aquest mètode fa una crida  a la crida TIQU_MODIFI per simular la modificacio d'un
     *tiquet en la Bd's
     * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
     * l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param id_key id que té el tiquet en la Bd's
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
            out.writeUTF(edc.encryptedText(id_conn+",TIQU_MODIFI," + id_key 
                        + sql,edc.getShare_key_client().toByteArray()));

            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            resultat = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            SystemUtils.escriuNouLog("Resultat de la modificacio del tiquet: " + resultat); 

        } catch (IOException ex) {
           
        }
        return resultat;
    }
     
     /**
     * Aquest mètode fa una crida  a la crida TIQU_DELETE per simular la baixa d'un
     * tiquet en la Bd's
     * Elinima un tiquet  i mostre per consola el resultat de  l'operació
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
            out.writeUTF(edc.encryptedText(id_conn +",TIQU_DELETE," + id_key,edc.getShare_key_client().toByteArray()));
            
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
            resultat = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));
            System.out.println("Resultat de la baixa del tiquet: " +resultat);
       
        } catch (IOException ex) {
          
        }
        return resultat;
    }
    
  /**
    * Aquest mètode fa una crida  a la crida TIQU_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * @param id_conn passem les credencials i el id d'un usuari logat al program
    * amb el rol admin
     * @return 
    */ 
    public int llistat(int id_conn) {

        Socket sc;
        EncrypDecrypCli edc = new  EncrypDecrypCli();
        int total =0;
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
           
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris ");
            //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
            //out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_QUERY,0",shared_secret.toByteArray()));
            //out.writeUTF(id_conn + ",USER_QUERY,1,nom = 'carles'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,cognom = 'fugarolas'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",USER_QUERY,2,cognom");
           //  out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_QUERY,2,nom",shared_secret.toByteArray()));
            //out.writeUTF(id_conn + ",USER_QUERY,2,departament");
            //out.writeUTF(id_conn + ",USER_QUERY,3,rol = 1,cognom");
             out.writeUTF(edc.encryptedText(id_conn + ",TIQU_QUERY,0",edc.getShare_key_client().toByteArray()));
           
            //El sservidor en torna el número de registres trobat en la consulta
             total = Integer.parseInt(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()));

            SystemUtils.escriuNouLog("El total de registres és :" + total);

            ArrayList registres = new ArrayList();
            //Posem el registres rebut dins d'un arrayList
            for (int i = 0; i < total; i++) {
             // registres.add(in.readUTF());
                registres.add(edc.decryptedText(in.readUTF(),edc.getShare_key_client().toByteArray()) );
            }
            //Mostrem els registres guardats en el arrayList
            for (int i = 0; i < registres.size(); i++) {
                System.out.println(registres.get(i).toString());
            }
        } catch (IOException ex) {
           
        }
        
        return total;
    }
          
     
}