/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.TiqServerMain;
import utilitats.SystemUtils;


public class MetodesSQLgestioUsuaris {
    
    
     Connection conectar = null;

    String user = "tiqinssues";
    String passwd = "password";
    String bd = "tiq";
    String ip;
    String port = "5432";
    String cadena ;
    
    File f = new File("logs.txt");

    public Connection establirConnexio() throws IOException {
        
        File fileCfg = new File("config.txt");
        if (!fileCfg.exists()) {
            try {
                System.out.println("SERVER_CREATE_NEW_CONFIG_INI_FILE");
                fileCfg.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Llegim la ip del servidor bd's
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileCfg));
            String configIp;
            while ((configIp = br.readLine()) != null) {
               
                 ip = configIp;              
            }
           //Tancar l'arxiu
            br.close();

            cadena = "jdbc:postgresql://" + ip + "/" + bd;
            System.out.println(cadena);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, user, passwd);
            SystemUtils.escriuNouLog("SQL_RESPONSE_successful_connection_user " + user);

        } catch (Exception e) {

            SystemUtils.escriuNouLog("SQL_RESPONSE_wrong_connection_database" + e.toString());

        }

        return conectar;
    }

    public void tancarConexio() throws SQLException {
        conectar.close();
    }

    
    public void consultaSqlUsuaris(String query) throws SQLException, IOException {

        int id;
        String usuari;
        String contrasenya;
        int rol;

        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        
        while (result.next()) {
            
            id          = result.getInt("ID");
            usuari      = result.getString("usuari");
            contrasenya = result.getString("contrasenya");
            rol         = result.getInt("rol");
            
            System.out.println(id + "\t" + usuari + "\t" + contrasenya +"\t"+ rol);
            // Leer registro
        }
    }
    
    public int altaUser(String[]altaDades) throws SQLException, IOException{
        
        
        int result =0;
        SystemUtils.escriuNouLog("ALTES NOU USUARI");       
     
         String sentenciaCrear = ("INSERT INTO usuaris (\"ID\",\"usuari\",\"contrasenya\",\"nom\",\"cognom\",\"departament\",\"rol\") VALUES (default,?,?,?,?,?,?)");

         PreparedStatement sentence_ready;
         
         System.out.println("Arribo al prepared");
         
        try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, altaDades[1]); // usuari
            sentence_ready.setString(2, altaDades[2]); // contrasenya
            sentence_ready.setString(3, altaDades[3]); //nom
            sentence_ready.setString(4, altaDades[4]); //cognom
            sentence_ready.setInt(5,Integer.parseInt(altaDades[5])); //departament   
            sentence_ready.setInt(6,Integer.parseInt(altaDades[6])); //rol

            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        } catch (Exception e) {
            System.out.println(e);
        }
               
        return result;
    }
    
    public int baixaUser(String[]altaDades) throws SQLException, IOException{
        
        
        int result =0;
        SystemUtils.escriuNouLog("BAIXA");       
     
         String sentenciaCrear = ("");

         PreparedStatement sentence_ready;
         
         System.out.println("Arribo al prepared");
         
        try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, altaDades[1]); // usuari
            sentence_ready.setString(2, altaDades[2]); // contrasenya
            sentence_ready.setString(3, altaDades[3]); //nom
            sentence_ready.setString(4, altaDades[4]); //cognom
            sentence_ready.setInt(5,Integer.parseInt(altaDades[5])); //departament   
            sentence_ready.setInt(6,Integer.parseInt(altaDades[6])); //rol

            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        } catch (Exception e) {
            System.out.println(e);
        }
               
        return result;
    }
    
}
