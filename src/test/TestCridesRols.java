/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilitats.SystemUtils;

/**
 *Aquesta classe te implementats mètodde static que serveixen per la realització
 * de les proves de la TEA2
 * @author Carles Fugarolas
 * 
 */
public class TestCridesRols {
    //Variables definines per fer el joc de proves
    private static int rol;
    private static int resposta_svr_id;
    
 /**
    * Aquest mètode s'encarrega d'iniciar l'execuciò del programa de les proves
    * És el mètode principal
     * @param args
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
    */
    public static void main(String[] args) throws IOException, InterruptedException {
        /**
         NOTA:  per fer les proves ha d'existir l'usuari carles // pwd carles i 
         l'usuari martina // pwdmartina
         TiqSerMain haurà d'estar ences i RUNNING i el servidor de Bd's Operatiu
         Eliminació de l'arxiu de logs de la carpeta del projecte
        * 
        */
        File f = new File("logs.txt");
        //Comprovo que l'arxiu existeix abans de eliminar
        if(f.delete()){
                System.out.println("El fitxer ha sigut esborrat satifactoriament");
            }else{
                System.out.println("El fitxer NO HA sigut esborrat satifactoriament");
            };    
       
        //NOTA:  per fer les proves ha d'existir l'usuari carles // pwd carles i 
        //l'usuari martina // pwdmartina
        System.out.println("#################### S I M U L A C I O   D E  P R O V E S ################################");
        System.out.println("#################### #################################### ################################");
      
                             
        System.out.println("############# Simulació d'un login correcte per fer la resta de proves ###################");
        
       //Simulem el login/logOut d'un usuari validad en Bd's
        System.out.println("#################### Simulació d'un login carles amb el rol de admin ####################");
        testSimulacioLoginCorrecte(0,"carles","pwdcarles");
        
        
        System.out.println("############# Llistat actual de registres guardats en la Bd's ############");
        llistatRols(resposta_svr_id);
        
       //Simulem una alta d'un nou rol dins la Bd's rols
        System.out.println("######### Simulació d'una alta d'un usuari");
        altaRol(resposta_svr_id,"rol de prova"); 
        
        llistatRols(resposta_svr_id);
           
        //Simulem la cerca del id pel nom del rol
        System.out.println("######### Simulació de buscar el ID del rol tècnic : " + buscarIdRol(resposta_svr_id,"Tècnic"));
        
        //Simulem una modificació d'un usuari
        System.out.println("######### Simulació de la modificació de l'usuari Silvia  ########");
             
        modificacioRol(resposta_svr_id,buscarIdRol(resposta_svr_id,"rol de prova"));      
        
        llistatRols(resposta_svr_id);
        
        //Simulem la baixa d'un usuari pel seu usuari
        System.out.println("######### Simulació de la baixa de l'usuari Silvia ########### ");   
        baixaRol(resposta_svr_id,buscarIdRol(resposta_svr_id,"Rol de prova modificat"));
        
        llistatRols(resposta_svr_id);
        llistatCountRols(resposta_svr_id);
         
        System.out.println("######### Simulació d'un logOut  CARLES ########");
        testSimulacioLogOut(resposta_svr_id);
    
        //mirem el registre
       // mostrarLogsConsola();
    }
   /**
    * Mètode que busca el id d'un usuari
     * @param id_conn
    * @param rol
    * @return el id que té l'usuari a la Bd's
    */
    public static int buscarIdRol(int id_conn, String rol) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
    /*     
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
    */
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);        
    
            //Executo la consulta de la crida per sortir
          //out.writeUTF(id_conn + ",ROLE_FIND," + rol);
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",ROLE_FIND," + rol,shared_secret.toByteArray()));
            
            //Llegir el numero total de registres de la consulta
         // int id_trobat = in.readInt();
            int id_trobat =Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
            //Si troba l'usuari torna el seu id
            return id_trobat;

        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
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
    public static void altaRol(int id_conn, String params) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
     /*       
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
    */      
             //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);     
     
            //Executo la consulta de la crida per fer l'alta del nou usuari
     //     out.writeUTF(id_conn + ",ROLE_NEW," + params);
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",ROLE_NEW," + params,shared_secret.toByteArray()));
     
            SystemUtils.escriuNouLog("Crida d'una alta   : " + id_conn + ",ROLE_NEW," + params);
            
            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            System.out.println("Resultat de la consulta : " + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    */ 
   public static void modificacioRol(int id_conn,int id_key){
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
    /*        
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
    */
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);         
    
            //El primer parametre es el id a modificar
         // out.writeUTF(id_conn+",ROLE_MODIFI," + id_key + ",Rol de prova modificat");
            out.writeUTF(SystemUtils.encryptedText(id_conn+",ROLE_MODIFI," + id_key + ",Rol de prova modificat",shared_secret.toByteArray()));

            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
          //System.out.println("El resultat de la modificació :" + in.readInt());
          System.out.println("Resultat de la modificació : "
                    + Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray())));

        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Aquest mètode fa una crida  a la crida USER_DELETE per simular la baixa d'un
     * usuari en la Bd's
     * Elinima un usuari  i mostre per consola el resultat de  l'operació
     * 
     * @param id_conn passem el id_connexió obtingut al fer login.
     * @param id_key id que té el usuari en la Bd's
    */ 
    public static void baixaRol(int id_conn, int id_key) {
        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
   /*         
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
   */                   
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);
   
            //Enviem al servidor la crida per fer la baixa d'un usuari
         // out.writeUTF(id_conn +",ROLE_DELETE," + id_key);
            out.writeUTF(SystemUtils.encryptedText(id_conn +",ROLE_DELETE," + id_key,shared_secret.toByteArray()));
            
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
        //  SystemUtils.escriuNouLog("El resultat de la baixa :" + in.readInt());
            System.out.println("Resultat de la baixa       : "
                    + Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray())));

        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * Aquest mètode fa una crida  a la crida USER_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * @param id_conn passem les credencials i el id d'un usuari logat al program
    * amb el rol admin
    */ 
    public static void llistatRols(int id_conn) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
  /*
            // Enviament de la clau pública del client al servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
  */          
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);
            System.out.println("Executem la crida a fer un llistat de tots els rols de la Bd's de rols ");
            //out.writeUTF(id_conn + ",ROLE_QUERY,0");
              out.writeUTF(SystemUtils.encryptedText(id_conn + ",ROLE_QUERY,0",shared_secret.toByteArray()));
            //out.writeUTF(id_conn + ",ROLE_QUERY,1,rol = 'Administrador'");
            //out.writeUTF(id_conn + ",ROLE_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",ROLE_QUERY,2,rol");
            //out.writeUTF(id_conn + ",ROLE_QUERY,3,rol = 'Administrador',id");
           
      
            //El sservidor en torna el número de registres trobat en la consulta
        //  int total = in.readInt();
            int total = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));

            System.out.println("El total de registres és :" + total);

            ArrayList registres = new ArrayList();
            //Posem el registres rebut dins d'un arrayList
            for (int i = 0; i < total; i++) {
             // registres.add(in.readUTF());
                registres.add(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()) );
            }
            //Mostrem els registres guardats en el arrayList
            for (int i = 0; i < registres.size(); i++) {
                System.out.println(registres.get(i).toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
    * Aquest mètode fa una crida  a la crida USER_QUERY_COUNT per simular una consulta
    * feta pels clients en la Bd's
    * Retorna el numero de registres que compleixen la consulta.
    * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
    */ 
    public static void llistatCountRols(int id_conn) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
    /*                    
            // Enviament de la clau pública del client al servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
    */
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);
        
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris " );
      //    out.writeUTF(id_conn + ",ROLE_QUERY_COUNT,0");
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",ROLE_QUERY_COUNT,0",shared_secret.toByteArray()));
         
           //Llegir el numero total de registres de la consulta
        // int total = in.readInt();
           int total = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
            
            System.out.println("El total de registres és :" +total);

        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Mètode que simula elLogOut d'un usuari
     * @param id_conn 
     */
     public static void testSimulacioLogOut(int id_conn) {

        Socket sc;
        System.out.println("Ara femt el logOut ....... ");

        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

        /*    // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
        */
        
         //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);
            
            
           //Enviem resposta al servidor amb el usuari i la contrasenya
          //out.writeUTF(id_conn + ",USER_EXIT");
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",USER_EXIT",shared_secret.toByteArray()));
            
            System.out.println("LogOut realitzat correctament ");

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
      * Mètode que simula un login fet desde en client
      * @param usuari 
      * @param contrasenya
      * @param id_conn
      * @throws InterruptedException 
      */
    public static void testSimulacioLoginCorrecte( int id_conn,String usuari, String contrasenya) throws InterruptedException {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
          
        /*  // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
        */
        
          //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            System.out.println("Valor public_key part client enviada al servidor: " + claus_ps[0]);
            
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            System.out.println("Valor share_secret generada : " + shared_secret);
       
            System.out.println("Server public key           : " + claus_ps[0]);
            System.out.println("Shared secret               : " + shared_secret);
                   
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",LOGIN," + usuari + "," + contrasenya ,shared_secret.toByteArray()));
          //out.writeUTF(id_conn +",LOGIN," + usuari + "," + contrasenya);
            
            //Recullim el id_sessio vàlit
            //resposta_svr_id = in.readInt();
           // resposta_svr_id = Integer.parseInt(in.readUTF());
              resposta_svr_id = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
              
              
              
            System.out.println("Fem el login amb l'usuari " + usuari + "i contrasenya  correcte :" + contrasenya + " - El resulta és CORRECTE  ");
            System.out.println("resposta servidor  es un id  valit    : " + resposta_svr_id);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (resposta_svr_id != 0) {
              //rol = in.readInt();
              //rol = Integer.parseInt(in.readUTF());
                rol = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
                                
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    /**
     * Mètode que llista per consolta tot l'arxiu de log's
     */
    public static void mostrarLogsConsola() {

        File f = new File("logs.txt");
        //Llegim el,contigut del l'arxiu de log's
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            String log;
            while ((log = br.readLine()) != null) {
                System.out.println(log);
            }
            //Tancar l'arxiu
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCridesRols.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}

        