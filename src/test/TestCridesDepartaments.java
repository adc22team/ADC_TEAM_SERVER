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
public class TestCridesDepartaments {
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
        SystemUtils.escriuNouLog("----------------- I N I C I   D E   P R O V E S   D E P A R T A M E N T S  ---------------");
        System.out.println();
        System.out.println("#################### S I M U L A C I O   D E  P R O V E S ################################");
        System.out.println("#################### #################################### ################################");
        System.out.println();
        System.out.println("############# Simulació d'un login correcte per fer la resta de proves ###################");
        System.out.println();
        
       //Simulem el login/logOut d'un usuari validad en Bd's
        System.out.println("#################### Simulació d'un login carles amb el rol de admin #####################");
        testSimulacioLoginCorrecte(0,"carles","pwdcarles");
        
        System.out.println();
        System.out.println("###################### Llistat actual de registres guardats en la Bd's ###################");
        llistatDepartaments(resposta_svr_id);
        
       //Simulem una alta d'un nou departament dins la Bd's rols
       System.out.println(); 
       System.out.println("###################### Simulació d'una alta d'un nou departament #########################");
       altaDepartament(resposta_svr_id,"departament de prova, descripcio prova,555 55 55"); 
       
       System.out.println();
       llistatDepartaments(resposta_svr_id);
           
        //Simulem la cerca del id pel nom del departament
        System.out.println();
        System.out.println("######### Simulació de buscar el ID del departament  departament de prova : " + buscarIdDepartament(resposta_svr_id,"departament de prova"));
        
        //Simulem una modificació d'un departament
        System.out.println();
        System.out.println("#################### Simulació de la modificació del departament de proves   #############");      
        modificacioDepartament(resposta_svr_id,buscarIdDepartament(resposta_svr_id,"departament de prova"));      
        
        System.out.println();
        llistatDepartaments(resposta_svr_id);
        
        //Simulem la baixa d'un usuari pel seu usuari
        System.out.println();
        System.out.println("##################### Simulació de la baixa d'un departament a ########################### ");   
        baixaDepartament(resposta_svr_id,buscarIdDepartament(resposta_svr_id,"Departament de prova modificat"));
        
        System.out.println();
        llistatDepartaments(resposta_svr_id);
        
        System.out.println();
        llistatCountDepartaments(resposta_svr_id);
      
        System.out.println();
        System.out.println("################################# Simulació d'un logOut  CARLES ############################");
        testSimulacioLogOut(resposta_svr_id);
    
        //mirem el registre
       // mostrarLogsConsola();
    }
   /**
    * Mètode que busca el id d'un departament
     * @param id_conn
    * @param departament
    * @return el id que té un departament en la Bd's
    */
    public static int buscarIdDepartament(int id_conn, String departament) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
    
            //Executo la consulta de la crida per sortir
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",DEPA_FIND," + departament,shared_secret.toByteArray()));
            
            //Llegir el numero total de registres de la consulta
             int id_trobat =Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));

             //Si troba l'usuari torna el seu id
            return id_trobat;

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Sinó troba l'usuari retorna 0 
        return 0;
    }
     /**
     * Aquest mètode fa una crida  a la crida DEPA_NEW per simular l'alta d'un nou
     * departament en la Bd's
     * Genera un nou usuari i recull el resultat de l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param params passem els registres del camps dels departament separat per "," en format text
    */
    public static void altaDepartament(int id_conn, String params) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
    
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            
            //Executo la consulta de la crida per fer l'alta del nou usuari
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",DEPA_NEW," + params,shared_secret.toByteArray()));
            
            SystemUtils.escriuNouLog("Crida d'una alta : " + id_conn + ",DEPA_NEW," + params);
            
            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            System.out.println("Resultat de l'alta : "
                    + Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray())));

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Aquest mètode fa una crida  a la crida DEPA_MODIFI per simular la modificacio d'un
     * usuari en la Bd's
     * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
     * l'operació
     * 
     * @param id_conn passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param id_key id que té el usuari en la Bd's
    */ 
   public static void modificacioDepartament(int id_conn,int id_key){
        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
     
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
     
            //El terce parametre es el id a modificar
            out.writeUTF(SystemUtils.encryptedText(id_conn+",DEPA_MODIFI," + id_key 
                    + ",Departament de prova modificat, Descripcio modificada, 666 66 66",shared_secret.toByteArray()));

            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            System.out.println("Resultat de la modificacio    : "
                    + Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray())));

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Aquest mètode fa una crida  a la crida DEPA_DELETE per simular la baixa d'un
     * usuari en la Bd's
     * Elinima un usuari  i mostre per consola el resultat de  l'operació
     * 
     * @param id_conn passem el id_connexió obtingut al fer login.
     * @param id_key id que té el usuari en la Bd's
    */ 
    public static void baixaDepartament(int id_conn, int id_key) {
        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
     
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
     
            //Enviem al servidor la crida per fer la baixa d'un usuari
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",DEPA_DELETE," + id_key,shared_secret.toByteArray()));
           
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
            SystemUtils.escriuNouLog("Resultat de la baixa       : "
                    + Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray())));

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * Aquest mètode fa una crida  a la crida DEPA_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * @param id_conn passem  id d'un usuari logat al program
    * amb el rol admin
    */ 
    public static void llistatDepartaments(int id_conn) {

        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
   
            System.out.println("Executem la crida a fer un llistat de tots els departaments de la Bd's de departaments ");
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",DEPA_QUERY,0",shared_secret.toByteArray()));
            //out.writeUTF(id_conn + ",DEPA_QUERY,1,departament = 'Compres'");
            //out.writeUTF(id_conn + ",DEPA_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",DEPA_QUERY,2,departament");
            //out.writeUTF(id_conn + ",DEPA_QUERY,3,departament = 'Compres',id");

            //El sservidor en torna el número de registres trobat en la consulta
            int total = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));

            System.out.println("El total de registres és :" + total);

            ArrayList registres = new ArrayList();
            //Posem el registres rebut dins d'un arrayList
            for (int i = 0; i < total; i++) {
                registres.add(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()) );
            }
            //Mostrem els registres guardats en el arrayList
            for (int i = 0; i < registres.size(); i++) {
                System.out.println(registres.get(i).toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
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
    public static void llistatCountDepartaments(int id_conn) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
   
          //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
        
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris " );
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",DEPA_QUERY_COUNT,0",shared_secret.toByteArray()));
         
            //Llegir el numero total de registres de la consulta
            int total = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
            
            System.out.println("El total de registres és :" + total);

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
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
        
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
            
           //Enviem resposta al servidor amb el usuari i la contrasenya
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
        
            //Cálcul clau pública client
            String[] claus_ps = SystemUtils.clauPublicaClient().split(",");
            //Enviem la clau pública del client al servidor
            out.writeUTF(String.valueOf(claus_ps[0]));
            //llegim la clau pública del servidor
            BigInteger shared_secret =SystemUtils.calculClauCompartida(in.readUTF(),claus_ps[1]);
                   
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(SystemUtils.encryptedText(id_conn + ",LOGIN," + usuari + "," + contrasenya ,shared_secret.toByteArray()));
            
            //Recullim el id_sessio vàlit
              resposta_svr_id = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
          
              System.out.println("Fem el login amb l'usuari " + usuari + "i contrasenya  correcte :" + contrasenya + " - El resulta és CORRECTE  ");
            System.out.println("resposta servidor  es un id  valit    : " + resposta_svr_id);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (resposta_svr_id != 0) {
            
                rol = Integer.parseInt(SystemUtils.decryptedText(in.readUTF(),shared_secret.toByteArray()));
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

