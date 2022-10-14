
package classes;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import utilitats.SystemUtils;


public class MetodesSQLgestioUsuaris {
       
    Connection conectar = null;

    String user = "tiqinssues";
    String passwd = "password";
    String bd = "tiq";
    String ip;
    String port = "5432";
    String cadena ;

    public Connection establirConnexio() throws IOException {
   
        cadena = "jdbc:postgresql://" + SystemUtils.obtenirIpConfig() + "/" + bd;
        SystemUtils.escriuNouLog("INFO_SQL_CONNECTION # "+cadena);
        
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

    
    public ArrayList consultaSqlUsuaris(String query) throws SQLException, IOException {

        int id;
        String usuari;
        String contrasenya;
        String nom;
        String cognom;
        int rol;
        int depart;
        int estat;
        
        ArrayList<String> usuarisArrayList = new ArrayList<String>();

        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        
        while (result.next()) {
            
            id          = result.getInt("ID");
            usuari      = result.getString("usuari");
            contrasenya = result.getString("contrasenya");
            nom         = result.getString("nom");
            cognom      = result.getString("cognom");
            depart      = result.getInt("departament");
            rol         = result.getInt("rol");
            estat       = result.getInt("estat");
            
           usuarisArrayList.add(    id + "," + usuari + "," + contrasenya + "," + nom  + "," + cognom  + "," + depart + "," + rol + "," + estat);
            
        }
        return usuarisArrayList;
    }
    
    public int altaUser(String[] altaDades) throws SQLException, IOException{
        
        
        int result =0;
        SystemUtils.escriuNouLog("INSERT_NEW_USER_IN_DB #");       
     
         String sentenciaCrear = ("INSERT INTO usuaris (\"id\",\"usuari\",\"contrasenya\",\"nom\",\"cognom\",\"departament\",\"rol\",\"estat\") VALUES (default,?,?,?,?,?,?,?)");

         PreparedStatement sentence_ready;
       
         try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, altaDades[1]); // usuari
            sentence_ready.setString(2, altaDades[2]); // contrasenya
            sentence_ready.setString(3, altaDades[3]); //nom
            sentence_ready.setString(4, altaDades[4]); //cognom
            sentence_ready.setInt(5,Integer.parseInt(altaDades[5])); //departament   
            sentence_ready.setInt(6,Integer.parseInt(altaDades[6])); //rol
            sentence_ready.setInt(7,Integer.parseInt(altaDades[7])); //estat
            
            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        } catch (Exception e) {
            System.out.println(e);
        }
               
        return result;
    }
    
    public int modificarUser(String[] altaDades) throws SQLException, IOException{
        
        
        int result =0;
        
        SystemUtils.escriuNouLog("UPDATE_USER_ID # "+altaDades[0]);       
     
        
        String sentenciaCrear = ("UPDATE usuaris SET usuari = ?, contrasenya = ?,nom = ?, cognom = ?, departament = ?, rol = ?, estat = ? WHERE id = ?");
            
        PreparedStatement sentence_ready;
                
        try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, altaDades[2]); // usuari
            sentence_ready.setString(2, altaDades[3]); // contrasenya
            sentence_ready.setString(3, altaDades[4]); //nom
            sentence_ready.setString(4, altaDades[5]); //cognom
            sentence_ready.setInt(5,Integer.parseInt(altaDades[6])); //departament   
            sentence_ready.setInt(6,Integer.parseInt(altaDades[7])); //rol
            sentence_ready.setInt(7,Integer.parseInt(altaDades[8])); //estat
            sentence_ready.setInt(8,Integer.parseInt(altaDades[1])); //id

             
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
     

        } catch (Exception e) {
            System.out.println(e);
        }
               
        return result;
    }
  
    
     public int baixaUser(int id_key) throws SQLException, IOException{
        
        
        int result =0;
        
        SystemUtils.escriuNouLog("DELETE_USER_ID # "+id_key);       
     
        String sentenciaCrear = ("DELETE FROM usuaris WHERE id=?");
              
        PreparedStatement sentence_ready;
                
        try {
           
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setInt(1, id_key); // id
             
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
     

        } catch (Exception e) {
            System.out.println(e);
        }
               
        return result;
    }  
}
