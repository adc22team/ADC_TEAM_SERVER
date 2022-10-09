package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.TiqServerMain;
import utilitats.SystemUtils;

public class Connexio {

    Connection conectar = null;

    String user = "tiqinssues";
    String passwd = "password";
    String bd = "tiq";
    String ip;
    String port = "5432";
    String cadena ;
    
    File f = new File("logs.txt");

    public Connection establirConnexio() {
        
        File fileCfg = new File("config.txt");
        if (!fileCfg.exists()) {
            try {
                System.out.println("SERVER_CREATE_NEW_CONFIG_INI_FILE");
                fileCfg.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ServerFil.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println("SQL_RESPONSE_successful_connection_user " + user);

        } catch (Exception e) {

            System.out.println("SQL_RESPONSE_wrong_connection_database" + e.toString());

        }

        return conectar;
    }

    public void tancarConexio() throws SQLException {
        conectar.close();
    }

    public int loginValit(String usuari, String contrasenya) throws SQLException {
        int cont = 0;

        String query = "select * from usuaris where usuari = " + "'" + usuari + "'" + " and contrasenya = '" + contrasenya + "'";
        Statement stmt = conectar.createStatement();

        ResultSet result = stmt.executeQuery(query);
        System.out.println("registres trobats cont: " + cont);
        while (result.next()) {
            cont++;
        }
        return cont;
    }

    public int rolUsuari(String usuari, String contrasenya) throws SQLException {
        int rol = 0;

        String query = "select rol from usuaris where usuari = " + "'" + usuari + "'" + " and contrasenya = '" + contrasenya + "'";
        Statement stmt = conectar.createStatement();

        ResultSet result = stmt.executeQuery(query);

        if (result.next()) {
            rol = result.getInt("rol");
        }

        return rol;
    }

    public void consultaSqlUsuaris(String query) throws SQLException, IOException {

        int id;
        String usuari;
        String contrasenya;
        int rol;

        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        
        SystemUtils.escriuNouLog(f, "Valor:"+ query);
        System.out.println("Valor:"+ query);
        while (result.next()) {
            
            id          = result.getInt("ID");
            usuari      = result.getString("usuari");
            contrasenya = result.getString("contrasenya");
            rol         = result.getInt("rol");
            
            System.out.println(id + "\t" + usuari + "\t" + contrasenya +"\t"+ rol);
            // Leer registro
        }
    }
    
    public void alta(String query) throws SQLException, IOException{
        
        int result =0;
         SystemUtils.escriuNouLog(f, "ALTES");

         String usuari="usuari_prova";
         String contrasenya="contrasenya_prova";
         String nom="nom_prova";
         String cognom="cognom_prova";
         int departament=1;
         int rol = 1;
         
         
         
         String sentenciaCrear = ("INSERT INTO usuaris (\"ID\",\"usuari\",\"contrasenya\",\"nom\",\"cognom\",\"departament\",\"rol\") VALUES (default,?,?,?,?,?,?)");

         PreparedStatement sentence_ready;
         
        try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, usuari);
            sentence_ready.setString(2, contrasenya);
            sentence_ready.setString(3, nom);
            sentence_ready.setString(4, cognom);
            sentence_ready.setInt(5, departament);
            sentence_ready.setInt(6, rol);

            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        

        } catch (Exception e) {
            System.out.println(e);
        }
         
         
         
         
         
      /*  Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
         int result = 0;
        Connection conexio = null;

        String sentenciaCrear = ("INSERT INTO usuaris (\"ID\",\"Usuari\",\"Contrasenya\",\"Nom\",\"Cognom\",\"Departament\") VALUES (default,?,?,?,?,?)");

        try {
            conexio = ConnectionSQL.establirConexio();
            sentence_ready = conexio.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, user);
            sentence_ready.setString(2, passwd);
            sentence_ready.setString(3, nom);
            sentence_ready.setString(4, cognom);
            sentence_ready.setString(5, departament);

            result = sentence_ready.executeUpdate();
            sentence_ready.close();

            conexio.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        return result;*/
    }

}
