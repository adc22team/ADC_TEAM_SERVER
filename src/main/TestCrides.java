/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilitats.SystemUtils;

/**
 *
 * @author ADC Team
 */
public class TestCrides {

    public static void main(String[] args) throws IOException {

        alta("emi,pwdemi,emi,alarcon,1,1,1");
        //llistat();
        //altaE();
       // baixa("54");
        //modificacio("54");
      //  encriptarContrasenya("6,david,pwddavid,david,medina,1,3,1");
          encriptarContrasenya("3,martina,pwdmartina,martina,lopez,1,3,1");
        
    }
    
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
           
    /*      //Llegir les dades reculllides de la consulta al client
           for(int i =0;i < total;i++)
             System.out.println(in.readUTF()); 
    */       
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
           out.writeUTF("USER_MODIFI,"+id_key+",manel1,pwdmanel1,manel1,lopez1,5,5,1");
           
           System.out.println("El resultat de la modificació :"+ in.readInt());
           
      
        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
  
    
    //id,usuari,pwd_sense_encriptar,nom,cognom,departament,rol,estat
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
     
     
    

}

