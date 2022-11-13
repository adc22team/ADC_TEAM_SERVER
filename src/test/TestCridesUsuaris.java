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
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import utilitats.SystemUtils;


/**
 *Aquesta classe te implementats mètodde static que serveixen per la realització
 * de les proves de la TEA2
 * @author Carles Fugarolas
 * 
 */
public class TestCridesUsuaris {
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
    @Test
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
      
        System.out.println("####################        C O N T R A S E N Y A S       ################################");
        System.out.println("Encriptació contrasenya 'pwderronea' és: "+ SystemUtils.convertirSHA256("pwderronea"));
        System.out.println("Encriptació contrasenya 'pwdmartina' és: "+ SystemUtils.convertirSHA256("pwdmartina"));
        System.out.println("Encriptació contrasenya 'pwdcarles'  és: "+ SystemUtils.convertirSHA256("pwdcarles"));
        System.out.println("####################        C O N T R A S E N Y A S       ################################");
        
        System.out.println("#####################    Simulació d'un login fallit   ###################################");
        //Simulem el login d'un usuari FALLIT en Bd's
        testSimulacioLoginOutFallit  (0,"martina","pwderronea");
                             
        System.out.println("############# Simulació d'un login correcte per fer la resta de proves ###################");
        
       //Simulem el login/logOut d'un usuari validad en Bd's
        System.out.println("#################### Simulació d'un login carles amb el rol de admin ####################");
        testSimulacioLoginCorrecte(0,"carles","pwdcarles");
        //Simulem una alta d'un nou usuari dins la Bd's d'usuaris
        System.out.println("######### Simulació d'una alta d'un usuari");
        alta(resposta_svr_id,"silvia,pwdsilvia,silvia,olivar,1,1,1"); 
      
       llistat(resposta_svr_id);
           
      //Simulem la cerca d'un usuari pel seu usuari
        System.out.println("######### Simulació de buscar el ID de l'usuari Silvia   : " + buscarIdUsuari(resposta_svr_id,"silvia"));
        
         //Simulem una modificació d'un usuari
        System.out.println("######### Simulació de la modificació de l'usuari Silvia  ########");
             
        modificacio(resposta_svr_id,buscarIdUsuari(resposta_svr_id,"silvia"));
               
        
        llistat(resposta_svr_id);
        
        //Simulem la baixa d'un usuari pel seu usuari
        System.out.println("######### Simulació de la baixa de l'usuari Silvia ########### ");   
        baixa(resposta_svr_id,buscarIdUsuari(resposta_svr_id,"silvia"));
               
        llistatCount(resposta_svr_id);
        
        llistatGrid(resposta_svr_id);
         
    
        System.out.println("######### Simulació d'un logOut  CARLES ########");
        testSimulacioLogOut(resposta_svr_id);
    
        //mirem el registre
       // mostrarLogsConsola();
    }
    
   /**
    * Mètode que busca el id d'un usuari
     * @param id_conn
    * @param usuari
    * @return el id que té l'usuari a la Bd's
    */
    public static int buscarIdUsuari(int id_conn, String usuari) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
          
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            //Executo la consulta de la crida per sortir
            out.writeUTF(id_conn+",USER_FIND," + usuari);
            //Llegir el numero total de registres de la consulta
            int id_trobat = in.readInt();
            //Si troba l'usuari torna el seu id
            return id_trobat;

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
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
    public static void alta(int id_conn, String params) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
          
            //Executo la consulta de la crida per fer l'alta del nou usuari
            out.writeUTF(id_conn + ",USER_NEW," + params);
            SystemUtils.escriuNouLog("Crida d'una alta : " + id_conn + ",USER_NEW," + params);
            
            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            System.out.println("Resultat de la consulta : " + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
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
   public static void modificacio(int id_conn,int id_key){
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
            
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
                        
            //El primer parametre es el id a modificar
            out.writeUTF(id_conn+",USER_MODIFI," + id_key + ",silvia,pwdsilvia,SILVIA,OLIVAR,2,3,1");

            //Lleguim el resultat de l'operació al servidor  0 - Malament i 1 - Bé
            System.out.println("El resultat de la modificació :" + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
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
    public static void baixa(int id_conn, int id_key) {
        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
                        
            //Enviem al servidor la crida per fer la baixa d'un usuari
            out.writeUTF(id_conn +",USER_DELETE," + id_key);
            
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
             SystemUtils.escriuNouLog("El resultat de la baixa :" + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * Aquest mètode fa una crida  a la crida USER_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * @param id_conn passem les credencials i el id d'un usuari logat al program
    * amb el rol admin
    */ 
    public static void llistat(int id_conn) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris ");
            
            //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
            out.writeUTF(id_conn + ",USER_QUERY,0");
            //out.writeUTF(id_conn + ",USER_QUERY,1,nom = 'carles'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,cognom = 'fugarolas'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",USER_QUERY,2,cognom");
            //out.writeUTF(id_conn + ",USER_QUERY,2,departament");
            //out.writeUTF(id_conn + ",USER_QUERY,3,rol = 1,cognom");
           
            //El sservidor en torna el número de registres trobat en la consulta
            int total = in.readInt();

            System.out.println("El total de registres és :" + total);

            ArrayList registres = new ArrayList();
            //Posem el registres rebut dins d'un arrayList
            for (int i = 0; i < total; i++) {
                registres.add(in.readUTF());
            }
            //Mostrem els registres guardats en el arrayList
            for (int i = 0; i < registres.size(); i++) {
                System.out.println(registres.get(i).toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
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

            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(id_conn + ",USER_EXIT");
            
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
            
          
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF(id_conn +",LOGIN," + usuari + "," + contrasenya);
            
            //Recullim el id_sessio vàlit
            resposta_svr_id = in.readInt();
            System.out.println("Fem el login amb l'usuari " + usuari + "i contrasenya  correcte :" + contrasenya + " - El resulta és CORRECTE  ");
            System.out.println("resposta servidor  es un id  valit    : " + resposta_svr_id);
            
            //Si la validació és correcte, recullim el rol de l'usuari
            if (resposta_svr_id != 0) {
                rol = in.readInt();
                System.out.println("resposta servidor del rol que l'usuari : " + rol);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Mètode que simula un login errori
     * @param usuari
     * @param contrasenya
     * @param id_conn el id obtingut al fer la validació de l'usuari
     * @throws InterruptedException 
     */
    public static void testSimulacioLoginOutFallit(int id_conn,String usuari, String contrasenya) throws InterruptedException {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
         
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
           //Enviem resposta al servidor  de la crida, amb el usuari i la contrasenya
            out.writeUTF(id_conn +",LOGIN," + usuari + "," + contrasenya );

            resposta_svr_id = in.readInt();
            System.out.println("Fem el login amb l'usuari " + usuari + "i contrasenya  erronea :" + contrasenya + " - El resulta és FALLIT  ");

            if (resposta_svr_id == 0) {
                System.out.println("resposta servidor del : " + resposta_svr_id + " ERROR VALIDACIO!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
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
    public static void llistatCount(int id_conn) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                        
            
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris " );
            //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
            out.writeUTF(id_conn + ",USER_QUERY_COUNT,0");
            //out.writeUTF(id_conn + ",USER_QUERY,1,nom = 'carles'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,cognom = 'fugarolas'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",USER_QUERY,2,cognom");
            //out.writeUTF(id_conn + ",USER_QUERY,2,departament");
            //out.writeUTF(id_conn + ",USER_QUERY,3,rol = 1,cognom");
            
            //Llegir el numero total de registres de la consulta
            int total = in.readInt();
            
            System.out.println("El total de registres és :" +total);

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
    * Aquest mètode fa una crida  a la crida USER_QUERY_COUNT per simular una consulta
    * feta pels clients en la Bd's
    * Retorna el numero de registres que compleixen la consulta.
    * 
     * @param id_conn el id del usuari logat al program
     * amb el rol admin
    */ 
    public static void llistatGrid(int id_conn) {

        Socket sc;
        try {
            
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
           
           
            // Enviament de la clau pública del servidor
            out.writeUTF(                          "Enviament de la clau pública del client");
            SystemUtils.escriuNouLog("Resposta_cli: Enviament de la clau pública del client"); 
            // Llegim la clau pública del servidor
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr:" + resposta_svr);
            
            //Aquí pots fer la consulta que vulguis et tornara el seu result i el podràs tractar
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els usuaris de la Bd's d'usuaris " );
            //0 - sense parametres | 1 -  where | 2 - order by | 3 - where i order by
            out.writeUTF(id_conn + ",USER_QUERY_GRID,0");
            //out.writeUTF(id_conn + ",USER_QUERY,1,nom = 'carles'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,cognom = 'fugarolas'");
            //out.writeUTF(id_conn + ",USER_QUERY,1,id = 1");
            //out.writeUTF(id_conn + ",USER_QUERY,2,cognom");
            //out.writeUTF(id_conn + ",USER_QUERY,2,departament");
            //out.writeUTF(id_conn + ",USER_QUERY,3,rol = 1,cognom");
              
            //El sservidor en torna el número de registres trobat en la consulta
            int total = in.readInt();
            
            System.out.println("El total de registres és :" +total);
            
              ArrayList registres = new ArrayList();
            //Posem el registres rebut dins d'un arrayList
            for (int i = 0; i < total; i++) {
                registres.add(in.readUTF());
            }
            //Mostrem els registres guardats en el arrayList
            for (int i = 0; i < registres.size(); i++) {
                System.out.println(registres.get(i).toString());
            }

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Aquest mètode encripte la contrasenya d'un usuari en la Bd's Fa la
     * modificació dels camps d'un registre i mostre per consola el resultat de
     * l'operació
     *
     * @param valitUser passem les credencials i el id d'un usuari logat al
     * program amb el rol admin
     * @param sql els camps del registre del usuari menys el seu id separat per
     * ","
     */
    /*public static void encriptarContrasenya(String valitUser, String sql) {

        String[] parametres = sql.split(",");
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + valitUser);
            //Executo la consulta de la crida per sortir
            //El primer parametre es el id a modificar
            out.writeUTF("USER_MODIFI," + parametres[0] + "," + parametres[1] + ","
                    + parametres[2] + "," + parametres[3] + ","
                    + parametres[4] + "," + parametres[5] + ","
                    + parametres[6] + "," + parametres[7]);

            System.out.println("El resultat de la modificació :" + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     */
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
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCridesUsuaris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
     
}

