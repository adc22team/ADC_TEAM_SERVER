/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
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
/**
 *
 * @author Carles Fugarolas
 */
public class MetodesSQLgestioUsuaris {
       
    Connection conectar = null;
    //paràmetres per establir la connexió
    //L'adreça ip del servidor de Bd's esta guardada en l'arxiu config.txt
    String user = "tiqinssues";
    String passwd = "password";
    String bd = "tiq";
    String ip;
    String port = "5432";
    String cadena ;
    /**
     * Aquest mètoda mira d'establir una connexió amb el servidor de Bb's
     * @return de la connexió extablerta
     * @throws IOException 
     */
    public Connection establirConnexio() throws IOException {
   
        cadena = "jdbc:postgresql://" + SystemUtils.obtenirIpConfig() + "/" + bd;
        SystemUtils.escriuNouLog("INFO_SQL_CONNECTION # "+cadena);
        
        try {
            //Configuració dels paràmetres i establiement de la connexió
            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, user, passwd);
            SystemUtils.escriuNouLog("SQL_RESPONSE_successful_connection_user " + user);

        } catch (Exception e) {
            //No s'ha pogut realitzar la connexió en el servidor de Bd's
            SystemUtils.escriuNouLog("SQL_RESPONSE_wrong_connection_database # " + e.toString());
            //Tanquem el servidor. S'ha de revisar el servidor o l'arxiu config.txt
            System.exit(0);
        }
        return conectar;
    }
    /**
     * Mètode que tanca la connexió amb el servidor de Bd's
     * @throws SQLException 
     */
    public void tancarConexio() throws SQLException {
        conectar.close();
    }

    /**
     * Mètode que ens permet enviar una consulta SQL a la taula d'usuaris
     * @param query sentència formatejada SQL
     * @return un ArrayList amb els resultat dels registre trobat el seus camps
     * separats per ","
     * @throws SQLException
     * @throws IOException 
     */
    public ArrayList consultaSqlUsuaris(String query) throws SQLException, IOException {
        //Creació de les variables locals corresponent a cada camp de la taula
        //de la Bd's d'usuaris
        int id;
        String usuari;
        String contrasenya;
        String nom;
        String cognom;
        int rol;
        int depart;
        int estat;
        //Definició del ArrayList que guardarà el resultat de la consulta
        ArrayList<String> usuarisArrayList = new ArrayList<String>();
        //Prepara i executar la consulta
        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        //Llegim tots els registres que han resultat de les consultes
        while (result.next()) {
            //Agafem els seus valors i els volquem en les varieables local
            //i si fos necessari fer-ne el seu tractament
            id          = result.getInt("ID");
            usuari      = result.getString("usuari");
            contrasenya = result.getString("contrasenya");
            nom         = result.getString("nom");
            cognom      = result.getString("cognom");
            depart      = result.getInt("departament");
            rol         = result.getInt("rol");
            estat       = result.getInt("estat");
           //Afegir cada registre dins el ArrayList 
           usuarisArrayList.add(    id + "," + usuari + "," + contrasenya + "," + nom  + ","
                             + cognom  + "," + depart + "," + rol + "," + estat);
            
        }
        //Retornem la llista confecciona
        return usuarisArrayList;
    }
 
    /**
     * Mètode que ens permet enviar una consulta SQL a la taula d'usuaris
     * @param query sentència formatejada SQL
     * @return un ArrayList amb els resultat dels registre trobat el seus camps
     * separats per ","
     * @throws SQLException
     * @throws IOException 
     */
    public ArrayList consultaSqlGrid(String query) throws SQLException, IOException {
        //Creació de les variables locals corresponent a cada camp de la taula
        //de la Bd's d'usuaris
        String usuari;
        int id;
        String nom;
        String cognom;
        int rol;
        int depart;
        int estat;
        String departament;
        //Definició del ArrayList que guardarà el resultat de la consulta
        ArrayList<String> usuarisArrayList = new ArrayList<String>();
        //Prepara i executar la consulta
        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        //Llegim tots els registres que han resultat de les consultes
        while (result.next()) {
            //Agafem els seus valors i els volquem en les varieables local
            //i si fos necessari fer-ne el seu tractament
            id          = result.getInt("id");
            usuari      = result.getString("usuari");
            nom         = result.getString("nom");
            cognom      = result.getString("cognom");
            depart      = result.getInt("departament");
            rol         = result.getInt("rol");
            estat       = result.getInt("estat");
           //Afegir cada registre dins el ArrayList 
           usuarisArrayList.add(   id+","+usuari + "," + nom  + "," + cognom  + "," + depart + "," + rol + "," + estat);
            
        }
        //Retornem la llista confecciona
        return usuarisArrayList;
    }
    
    /**
     * Mètode que ens permet fer l'alta d'un usuari dins la taula usuraris
     * @param altaDades passem un String amb el valors dels camps separat per ","
     * tot en format texte
     * @return retornem un 0 si l'alta no s'ha pogut fer i 1 si s'ha fet satisfactoriament
     * @throws SQLException
     * @throws IOException 
     */
    public int altaUser(String[] altaDades) throws SQLException, IOException{
        //Contador que recullirar el resultat de l'operació 0 - error i 1 - satisfactori        
        int result = 0;
        //Afegim en el log l'operació d'alta demanada pel client
        SystemUtils.escriuNouLog("INSERT_NEW_USER_IN_DB #");
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("INSERT INTO usuaris (\"id\",\"usuari\",\"contrasenya\",\"nom\",\"cognom\",\"departament\",\"rol\",\"estat\") VALUES (default,?,?,?,?,?,?,?)");
        //preparem la consulta
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer l'alta 
        try {
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, altaDades[1]); // usuari
            sentence_ready.setString(2, SystemUtils.convertirSHA256(altaDades[2])); // contrasenya
            sentence_ready.setString(3, altaDades[3]); //nom
            sentence_ready.setString(4, altaDades[4]); //cognom
            sentence_ready.setInt(5, Integer.parseInt(altaDades[5])); //departament   
            sentence_ready.setInt(6, Integer.parseInt(altaDades[6])); //rol
            sentence_ready.setInt(7, Integer.parseInt(altaDades[7])); //estat
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        } catch (NumberFormatException | SQLException e) {
            //Afegim en el log l'operació d'ata ha donar error
            SystemUtils.escriuNouLog("INSERT_NEW_USER_IN_DB_ERROR # " + e);
        }
        //Retornem el resultat de l'operació 0 - Error i 1 - Correcte
        return result;
    }
    /**
     * Mètode que ens permet fer l'alta d'un usuari dins la taula usuraris
     * @param modificacioDades passem un String amb el valors dels camps separat per ","
     * tot en format texte
     * @return retornem un 0 si l'alta no s'ha pogut fer i 1 si s'ha fet satisfactoriament
     * @throws SQLException
     * @throws IOException 
     */
    public int modificarUser(String[] modificacioDades) throws SQLException, IOException{
        //Contador que recullirar el resultat de l'operació 0 - error i 1 - satisfactori              
        int result =0;
        //Afegim en el log l'operació de modificació demanada pel client
        SystemUtils.escriuNouLog("UPDATE_USER_ID # "+modificacioDades[0]);       
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("UPDATE usuaris SET usuari = ?, contrasenya = ?,nom = ?, cognom = ?, departament = ?, rol = ?, estat = ? WHERE id = ?");
        //Definim el PreparedStatement   
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer la modificació        
        try {
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setString(1, modificacioDades[2]); // usuari
            sentence_ready.setString(2, SystemUtils.convertirSHA256(modificacioDades[3])); // contrasenya
            sentence_ready.setString(3, modificacioDades[4]); //nom
            sentence_ready.setString(4, modificacioDades[5]); //cognom
            sentence_ready.setInt(5,Integer.parseInt(modificacioDades[6])); //departament   
            sentence_ready.setInt(6,Integer.parseInt(modificacioDades[7])); //rol
            sentence_ready.setInt(7,Integer.parseInt(modificacioDades[8])); //estat
            sentence_ready.setInt(8,Integer.parseInt(modificacioDades[1])); //id
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
        } catch (Exception e) {
             //Afegim en el log l'operació modificació ha donar error
             SystemUtils.escriuNouLog("UPDATE_USER_IN_DB_ERROR # " + e);
        }
         //Retornem el resultat de l'operació 0 - Error i 1 - Correcte       
        return result;
    }
  
    /**
     * Mètode que elimina un usuari de la Bd's de usuari
     * @param id_key pasem per paràmetre el identificador del registre
     * @return retorna el resultat de l'operació 0 - Error i 1 - Correcte
     * @throws SQLException
     * @throws IOException 
     */
     public int baixaUser(int id_key) throws SQLException, IOException{
        //Variable que recullirar el resultat de l'operació 0 - error i 1 - satisfactori  
        int result =0;
        //Afegim en el log l'operació de baixa demanada  pel client
        SystemUtils.escriuNouLog("DELETE_USER_ID           # "+id_key);       
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("DELETE FROM usuaris WHERE id=?");
        //Definim el PreparedStatement          
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer la baixa         
        try {
            //Executem la SQL per fer la modificació 
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            sentence_ready.setInt(1, id_key); // id
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
        } catch (Exception e) {
             //Afegim en el log l'operació baixa ha donar error
             SystemUtils.escriuNouLog("DELETE_USER_IN_DB_ERROR # " + e);
        }
        //retorna el resultat de l'operació 0 - Error i 1 - Correcte       
        return result;
    }  
     
     
     /**
     * Mètode que buscar el id d'un usuari a la Bd's
     * @param usuari String amb el nom introduit en el login
     * @return retorna  el id del usuari té a la Bd's
     * @throws SQLException 
     */
    public int cercaIdUsuari(String usuari) throws SQLException {
        //Variable que guarda el resultat de la consulta  que seraà el rol que té
        //l' usuari
        int id = 0;
        //definició del la consuta encriptant la contrasenya, a la Bd's està guardada així 
        String query = "select id from usuaris where usuari = " + "'" + usuari  + "'";
        Statement stmt = conectar.createStatement();
        //Executar la consulta
        ResultSet result = stmt.executeQuery(query);
        //Agafem el valor del camp de la Bd's i el guardem a id
        if (result.next()) {
            id = result.getInt("id");
        }
        //Retormen el seu valor
        return id;
    } 
}
