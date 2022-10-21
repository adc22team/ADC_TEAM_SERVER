/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package main;

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
 * de les proves de la TEA2
 * @author Carles Fugarolas
 * 
 */
public class TestCrides {
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
        
        //NOTA:  per fer les proves ha d'existir l'usuari carles // pwd carles i 
        //l'usuari martina // pwdmartina
        System.out.println("#################### S I M U L A C I O   D E  P R O V E S ################################");
        System.out.println("#################### ###################################S ################################");
      
        //Simulem el login/logOut d'un usuari validad en Bd's
        System.out.println("######### Simulació d'un login ########");
        testSimulacioLoginOutCorrecte("martina","pwdmartina","0");
        
        System.out.println("######### Simulació d'un logOut ########");
        testSimulacioLogOut("martina","pwdmartina",String.valueOf(resposta_svr_id));
        //mirem el registre
        mostrarLogsConsola();  
        
        //Simulem el login/logOut d'un usuari FALLIT en Bd's
        testSimulacioLoginOutFallit  ("martina","pwderronea","0");
        //mirem el registre
        mostrarLogsConsola();  
        
       //Simulem una alta d'un nou usuari dins la Bd's d'usuaris
        System.out.println("######### Simulació d'una alta d'un usuari");
        alta("silvia,pwdsilvia,silvia,olivar,1,1,1");
        llistat();
        
        //Simulem la cerda d'un usuari pel seu usuari
        System.out.println("######### Simulació de buscar el ID de l'usuari Silvia   : "
                +buscarIdUsuari("silvia"));
        
        //Simulem una modificació d'un usuari
        System.out.println("######### Simulació de la modificació de l'usuari Silvia  ########");
        modificacio(String.valueOf(buscarIdUsuari("silvia")));
        llistat();
        mostrarLogsConsola();  
        
        //Simulem la baixa d'un usuari pel seu usuari
        System.out.println("######### Simulació de la baixa de l'usuari Silvia ########### ");   
        baixa(String.valueOf(buscarIdUsuari("silvia")));
        llistat();
        mostrarLogsConsola();  
    }
    
   /**
    * Mètode que busca el id d'un usuari
    * @param usuari
    * @return el id que té l'usuari a la Bd's
    */
    public static int buscarIdUsuari(String usuari){
        
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
                                      
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           
           //Executo la consulta de la crida per sortir
        
           out.writeUTF("USER_FIND,"+usuari);
           
           //Llegir el numero total de registres de la consulta
           int id_trobat =in.readInt();
           //Si troba l'usuari torna el seu id
           return id_trobat; 
       
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
  
       //Sinó troba l'usuari retorna 0 
       return 0; 
    }
    /**
     * Mètode que simula un login errori
     * @param usuari
     * @param contrasenya
     * @param id
     * @throws InterruptedException 
     */
     public static void testSimulacioLoginOutFallit(String usuari,String contrasenya, String id) throws InterruptedException{
        
        Socket sc;

        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();

            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + usuari + "," + contrasenya + "," + id);

            resposta_svr_id = in.readInt();
            System.out.println("Fet el login FALLIT ....... ");
            
            if (resposta_svr_id == 0) {
                
                System.out.println("resposta servidor del : " + resposta_svr_id +" ERROR VALIDACIO!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * Mètode que simula elLogOut d'un usuari
     * @param usuari
     * @param contrasenya
     * @param id 
     */
     public static void testSimulacioLogOut(String usuari, String contrasenya,String id){
         
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
            
            out.writeUTF("USER_EXIT");
            
            System.out.println("LogOut realitzat correctament "); 

        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
   
     /**
      * Mètode que simula un login fet desde en client
      * @param usuari 
      * @param contrasenya
      * @param id
      * @throws InterruptedException 
      */
    public static void testSimulacioLoginOutCorrecte(String usuari,String contrasenya, String id) throws InterruptedException{
        
        Socket sc;

        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();

            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN," + usuari + "," + contrasenya + "," + id);

            resposta_svr_id = in.readInt();
            System.out.println("Fet el login CORRECTE ....... ");
            System.out.println("resposta servidor id      : " + resposta_svr_id);

            if (resposta_svr_id != 0) {
                rol = in.readInt();
                System.out.println("resposta servidor del rol : " + rol);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      
    }
    
      /**
    * Aquest mètode fa una crida  a la crida USER_QUERY per simular una consulta
    * feta pels clients en la Bd's
    * Retorna un llistat per consola de la consulta feta.
    * 
    */ 
    public static void llistat(){
        
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
                                      
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           
           //Executo la consulta de la crida per sortir
           //Aquí pots fer la consulta que vulguis et tornara el seu result i el
           //podràs tractar
           //Exemples
           out.writeUTF("USER_QUERY,select * from usuaris order by nom");
           
           //Llegir el numero total de registres de la consulta
           int total =in.readInt();
                  
          ArrayList  registres = new ArrayList();
           
           //Posem el registres rebut dins d'un arrayList
           for(int i =0;i < total;i++)
              registres.add(in.readUTF()); 
           
           //Mostrem els registres guardats en el arrayList
          for(int i =0;i < registres.size();i++)
              System.out.println(registres.get(i).toString());   
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     /**
    * Aquest mètode fa una crida  a la crida USER_NEW per simular l'alta d'un nou
    * usuari en la Bd's
    * Genera un nou usuari i recull el resultat de l'operació
    * 
     * @param params
    */
    public  static void alta(String params){
        
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
           
           SystemUtils.escriuNouLog("Resposta_svr:"+ resposta_svr);
                            
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           //Executo la consulta de la crida per sortir
           out.writeUTF("USER_NEW,"+params);
           System.out.println("Resultat de la consulta : " + in.readInt());
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    /**
    * Aquest mètode fa una crida  a la crida USER_DELETE per simular la baixa d'un
    * usuari en la Bd's
    * Elinima un usuari  i mostre per consola el resultat de  l'operació
    * 
    * @param id_key
    */ 
   public static void baixa(String id_key){
        Socket sc;
       try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
               
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
                                      
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");      
           
           out.writeUTF("USER_DELETE,"+Integer.parseInt(id_key));
           
           //Llegir el numero total de registres de la consulta, si resultat és 1 es correcte
           System.out.println("El resultat de la baixa :"+ in.readInt());
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
    /**
    * Aquest mètode fa una crida  a la crida USER_MODIFI per simular la modificacio d'un
    * usuari en la Bd's
    * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
    * l'operació
    * 
    */ 
   public static void modificacio(String id_key){
         Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                   
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
          
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles"+"," + "555");
           //Executo la consulta de la crida per sortir
           //El primer parametre es el id a modificar
           out.writeUTF("USER_MODIFI,"+id_key+",silvia,pwdsilvia,SILVIA,OLIVAR,2,3,1");
           
           System.out.println("El resultat de la modificació :"+ in.readInt());
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
  
    /**
    * Aquest mètode encripte la contrasenya d'un usuari en la Bd's
    * Fa la modificació dels camps d'un registre i mostre per consola el resultat de 
    * l'operació
     * @param sql
    */ 
     public static void encriptarContrasenya(String sql){
        
         String[] parametres = sql.split(",");
        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                   
           // Llegir la resposta del servidor al establir la connexió
           String resposta_svr = in.readUTF();
          
           //Enviem resposta al servidor amb el usuari i la contrasenya
           out.writeUTF("LOGIN," + "carles" + "," + "pwdcarles" + "," + "555");
           //Executo la consulta de la crida per sortir
           //El primer parametre es el id a modificar
           out.writeUTF("USER_MODIFI," + parametres[0] + "," + parametres[1]  + "," 
                                       + parametres[2] + "," + parametres[3] + ","
                                       + parametres[4] + "," + parametres[5] + ","
                                       + parametres[6] + "," + parametres[7]);
           
           System.out.println("El resultat de la modificació :"+ in.readInt());
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
     
       public static void mostrarLogsConsola(){
    
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
            
            if(f.delete()){
                System.out.println("El fitxer ha sigut esborrat satifactoriament");
            }else{
                System.out.println("El fitxer NO HA sigut esborrat satifactoriament");
            };
     
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                   
}

