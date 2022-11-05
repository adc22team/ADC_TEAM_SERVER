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
import utilitats.SystemUtils;

/**
 *Aquesta classe te implementats mètodde static que serveixen per la realització
 * de les proves de la TEA3
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
    
        System.out.println("######### Simulació d'un login correcte per fer la resta de proves TEA3 ########");
        
       //Simulem el login/logOut d'un usuari validad en Bd's
        System.out.println("############# Simulació d'un login carles amb el rol de administrador ##########");
        testSimulacioLoginOutCorrecte("carles","pwdcarles","0");
        
        System.out.println("######## Llistat actual de registres guardats en la Bd's de DEPARTAMENTS ########");
        llistatDepartaments("carles,pwdcarles,"+String.valueOf(resposta_svr_id));
        
        //Simulem la cerca d'un id  d'un departament per el seu departament
        System.out.println("######### Simulació de buscar el ID del departament comercial   : "
                +buscarIdDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),"Comercial")+" #######"); 
       
       //Simulem una alta d'un nou departaemt dins la Bd's de departaments
        System.out.println("##################### Simulació d'una alta d'un nou DEPARTAMENT  ##################");
        altaDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),"Departament de prova");
        
        //Llistem el resultat del nou departament creat
        System.out.println("##################### Llistat actual de departaments en la BD's  ##################");
        llistatDepartaments("carles,pwdcarles,"+String.valueOf(resposta_svr_id));
       
        //Simulem una modificació d'un departament en la Bd's
        System.out.println("############# Simulació de la modificació d'un departament en la Bd's  ############");
        modificacioDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),
                String.valueOf(buscarIdDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),"Departament de prova")));   
                
        //Llistem el resultat del nou departament creat
        System.out.println("##################### Llistat actual de departaments en la BD's  ##################");
        llistatDepartaments("carles,pwdcarles,"+String.valueOf(resposta_svr_id));
        
        //Simulem la baixa d'un departament pel seu usuari
        System.out.println("########## Simulació de la baixa del departament  de prova de la Bd's ############ ");   
        baixaDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),
                  String.valueOf(buscarIdDepartament("carles,pwdcarles,"+String.valueOf(resposta_svr_id),"Departament de prova modificat")));
        
        //Llistem el resultat del nou departament creat
        System.out.println("##################### Llistat actual de departaments en la BD's  ##################");
        llistatDepartaments("carles,pwdcarles,"+String.valueOf(resposta_svr_id));
        
        //Simulem la baixa d'un  departamentrol pel nom del seu departament
        System.out.println("########## Simulació del número total de departaemts que hi han Bd's ############# ");   
        llistatCountDepartaments("carles,pwdcarles,"+String.valueOf(resposta_svr_id));
              
        System.out.println("########################## Simulació d'un logOut  CARLES ##########################");
        testSimulacioLogOut("carles","pwdcarles",String.valueOf(resposta_svr_id));
       
        //mirem el registre
       // mostrarLogsConsola();
    }
      
    /**
    * Aquest mètode fa una crida  a la crida DEPA_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * 
     * @param valitUser passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
    */ 
    public static void llistatDepartaments(String valitUser) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya id valit
            out.writeUTF("LOGIN," + valitUser);
            //Executo la consulta de la crida per sortir
            //Aquí pots fer la consulta que vulguis et tornara el seu result i el
            //podràs tractar
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els departaments de la Bd's de departaments" );
            out.writeUTF("DEPA_QUERY,select * from  departaments");
        
            //Llegir el numero total de registres de la consulta
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
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
   /**
    * Mètode que busca el id d'un departament
     * @param valitUser
    * @param departament
    * @return el id que té l'usuari a la Bd's
    */
    public static int buscarIdDepartament(String valitUser, String departament) {

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
            out.writeUTF("DEPA_FIND," + departament);
            //Llegir el numero total de registres de la consulta
            int id_trobat = in.readInt();
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
     * usuari en la Bd's
     * Genera un nou usuari i recull el resultat de l'operació
     * 
     * @param valitUser passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param params passem els registres del camps dels usuari separat per "," en format text
    */
    public static void altaDepartament(String valitUser, String params) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            SystemUtils.escriuNouLog("Resposta_svr : " + resposta_svr);
            //Enviem resposta al servidor amb el usuari i la contrasenya
            System.out.println("Valor de valituser : " + valitUser);
            //Fem el login amb un usuari
            out.writeUTF("LOGIN," + valitUser);
            //Executo la consulta de la crida per sortir
            out.writeUTF("DEPA_NEW," + params);
            System.out.println("Resultat de la consulta : " + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    /**
     * Aquest mètode fa una crida  a la crida DEPA_DELETE per simular la baixa d'un
     * departament en la Bd's
     * Elinima un departament i mostre per consola el resultat de  l'operació
     * 
     * @param valitUser passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
    * @param id_key id que té el departament en la Bd's
    */ 
    public static void baixaDepartament(String valitUser, String id_key) {
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + valitUser);
            //Enviem al servidor la crida per fer la baixa d'un usuari
            out.writeUTF("DEPA_DELETE," + Integer.parseInt(id_key));
            //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
            System.out.println("El resultat de la baixa : " + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Aquest mètode fa una crida  a la crida DEPA_MODIFI per simular la modificacio d'un
     * departament en la Bd's
     * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
     * l'operació
     * 
     * @param valitUser passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
     * @param id_key id que té el usuari en la Bd's
    */ 
   public static void modificacioDepartament(String valitUser,String id_key){
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
            out.writeUTF("DEPA_MODIFI," + id_key + ",Departament de prova modificat");

            System.out.println("El resultat de la modificació : " + in.readInt());

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Mètode que simula elLogOut d'un usuari
     * @param usuari
     * @param contrasenya
     * @param id 
     */
     public static void testSimulacioLogOut(String usuari, String contrasenya, String id) {

        Socket sc;
        System.out.println("Ara femt el logOut ....... ");

        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + usuari + "," + contrasenya + "," + id);
            //Executem la crida per sortir i donar de baixa l'usuari del HaspMap
            out.writeUTF("USER_EXIT");
            System.out.println("LogOut realitzat correctament ");

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     /**
      * Mètode que simula un login fet desde en client
      * @param usuari 
      * @param contrasenya
      * @param id
      * @throws InterruptedException 
      */
    public static void testSimulacioLoginOutCorrecte(String usuari, String contrasenya, String id) throws InterruptedException {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + usuari + "," + contrasenya + "," + id);
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
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
    * Aquest mètode fa una crida  a la crida DEPA_QUERY_COUNT per simular una consulta
    * feta pels clients en la Bd's
    * Retorna el numero de registres que compleixen la consulta.
    * 
     * @param valitUser passem les credencials i el id d'un usuari logat al program
     * amb el rol admin
    */ 
    public static void llistatCountDepartaments(String valitUser) {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            //Enviem resposta al servidor amb el usuari i la contrasenya id valit
            out.writeUTF("LOGIN," + valitUser);
            //Executo la consulta de la crida per sortir
            //Aquí pots fer la consulta que vulguis et tornara el seu result i el
            //podràs tractar
            //Exemples
            System.out.println("Executem la crida a fer un llistat de tots els departaments de la Bd's de departaments " );
            out.writeUTF("DEPA_QUERY_COUNT,select * from departaments order by departament");
         
            //Llegir el numero total de registres de la consulta
            int total = in.readInt();
            
            System.out.println("El total de registres és : " + total);

        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCridesDepartaments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
