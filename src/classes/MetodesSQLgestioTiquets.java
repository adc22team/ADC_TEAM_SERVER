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
public class MetodesSQLgestioTiquets {
       
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
    public ArrayList consultaSqlTiquets(String query) throws SQLException, IOException {
        //Creació de les variables locals corresponent a cada camp de la taula
        //de la Bd's d'usuaris
        int id_tiq;
        String incidencia;
        String comentaris;
        String data_inici;
        String data_final;
        int estat;
        int prioritat;
        int id_usuari;
        int id_tecnic;
        //Definició del ArrayList que guardarà el resultat de la consulta
        ArrayList<String> tiquetsArrayList = new ArrayList<>();
        //Prepara i executar la consulta
        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        //Llegim tots els registres que han resultat de les consultes
        while (result.next()) {
            //Agafem els seus valors i els volquem en les varieables local
            //i si fos necessari fer-ne el seu tractament
            id_tiq = result.getInt("id_tiq");
            incidencia = result.getString("incidencia");
            comentaris = result.getString("comentaris");
            data_inici = result.getString("data_inici");
            data_final = result.getString("data_final");
            estat = result.getInt("estat");
            prioritat = result.getInt("prioritat");
            id_usuari = result.getInt("id_usuari");
            id_tecnic = result.getInt("id_tecnic");

            //Afegir cada registre dins el ArrayList 
         /*   tiquetsArrayList.add(id_tiq + "," + data_inici + "," + incidencia + "," + comentaris + "," + estat + "," + prioritat + ","
                    + id_usuari + "," + id_tecnic + "," + data_final);*/
            
            tiquetsArrayList.add(id_tiq + incidencia + "," + comentaris + ","  + estat + ","
                      + id_usuari + "," + id_tecnic + "," + data_inici + "," + data_final + "," + prioritat );
                    

        }
        //Retornem la llista confecciona
        return tiquetsArrayList;
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
        //Creació de les variables locals corresponent a cada camp de la taula
        //de la Bd's d'usuaris
        int id_tiq;
        String incidencia;
        String comentaris;
        String data_inici;
        String data_final;
        int estat;
        int prioritat;
        int id_usuari;
        int id_tecnic;
        //Definició del ArrayList que guardarà el resultat de la consulta
        ArrayList<String> tiquetsArrayList = new ArrayList<String>();
        //Prepara i executar la consulta
        Statement stmt = conectar.createStatement();
        ResultSet result = stmt.executeQuery(query);
        //Llegim tots els registres que han resultat de les consultes
        while (result.next()) {
            //Agafem els seus valors i els volquem en les varieables local
            //i si fos necessari fer-ne el seu tractament
            id_tiq = result.getInt("id_tiq");
            incidencia = result.getString("incidencia");
            comentaris = result.getString("comentaris");
            data_inici = result.getString("data_inici");
            data_final = result.getString("data_final");
            estat = result.getInt("estat");
            prioritat = result.getInt("prioritat");
            id_usuari = result.getInt("id_usuari");
            id_tecnic = result.getInt("id_tecnic");

            //Afegir cada registre dins el ArrayList 
            tiquetsArrayList.add(id_tiq + "," + data_inici + "," + incidencia + "," + comentaris + "," + estat + "," + prioritat + ","
                    + id_usuari + "," + id_tecnic + "," + data_final);

        }
        //Retornem la llista confecciona
        return tiquetsArrayList;
    }
    
    /**
     * Mètode que ens permet fer l'alta d'un usuari dins la taula usuraris
     * @param altaDades passem un String amb el valors dels camps separat per ","
     * tot en format texte
     * @return retornem un 0 si l'alta no s'ha pogut fer i 1 si s'ha fet satisfactoriament
     * @throws SQLException
     * @throws IOException 
     */
    public int altaTiquet(String[] altaDades) throws SQLException, IOException{
        //Contador que recullirar el resultat de l'operació 0 - error i 1 - satisfactori        
        int result = 0;
        //Afegim en el log l'operació d'alta demanada pel client
        SystemUtils.escriuNouLog("INSERT_NEW_TIQ_IN_DB #");
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("INSERT INTO tiquets (id_tiq, incidencia,comentaris,data_inici,estat,id_usuari,id_tecnic,prioritat) VALUES (default,?,?,?,?,?,?,?)");
        //preparem la consulta
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer l'alta 
        try {
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            
            sentence_ready.setString(1, altaDades[2]); // incidencia
            sentence_ready.setString(2, altaDades[3]); // comentari
            sentence_ready.setString(3, SystemUtils.agafarDataHoraSistemaTiq()); //data
            sentence_ready.setInt(4, Integer.parseInt(altaDades[4])); //estat
            sentence_ready.setInt(5, Integer.parseInt(altaDades[5])); //id_usuari  
            sentence_ready.setInt(6, Integer.parseInt(altaDades[6])); //id_tecnic
            sentence_ready.setInt(7, Integer.parseInt(altaDades[7])); //prioritat
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();

        } catch (NumberFormatException | SQLException e) {
            //Afegim en el log l'operació d'ata ha donar error
            SystemUtils.escriuNouLog("INSERT_NEW_TIQ_IN_DB_ERROR # " + e);
        }
        //Retornem el resultat de l'operació 0 - Error i 1 - Correcte
         SystemUtils.escriuNouLog("Valor de resultat # " + result);
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
    public int modificarTiquet(String[] modificacioDades) throws SQLException, IOException{
        //Contador que recullirar el resultat de l'operació 0 - error i 1 - satisfactori              
        int result =0;
        //Afegim en el log l'operació de modificació demanada pel client
        SystemUtils.escriuNouLog("UPDATE_TIQ_ID # "+modificacioDades[0]);       
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("UPDATE tiquets SET incidencia = ?, comentaris = ?,estat = ?, id_usuari = ?, id_tecnic = ?, prioritat = ?, data_final = ? WHERE id_tiq = ?");
        //Definim el PreparedStatement   
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer la modificació        
        try {
            sentence_ready = conectar.prepareStatement(sentenciaCrear);
            
            sentence_ready.setString(1, modificacioDades[3]); // incidencia
            sentence_ready.setString(2, modificacioDades[4]); // comentari
            //sentence_ready.setString(3, modificacioDades[5]); //data
            sentence_ready.setInt(3, Integer.parseInt(modificacioDades[5])); //estat
            sentence_ready.setInt(4, Integer.parseInt(modificacioDades[6])); //id_usuari  
            sentence_ready.setInt(5, Integer.parseInt(modificacioDades[7])); //id_tecnic
            sentence_ready.setInt(6, Integer.parseInt(modificacioDades[8])); //prioritat
            sentence_ready.setString(7, modificacioDades[9]); //data_final
            sentence_ready.setInt(8,Integer.parseInt(modificacioDades[2])); //id
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
        } catch (Exception e) {
             //Afegim en el log l'operació modificació ha donar error
             SystemUtils.escriuNouLog("UPDATE_TIQ_IN_DB_ERROR # " + e);
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
     public int baixaTiquet(int id_key) throws SQLException, IOException{
        //Variable que recullirar el resultat de l'operació 0 - error i 1 - satisfactori  
        int result =0;
        //Afegim en el log l'operació de baixa demanada  pel client
        SystemUtils.escriuNouLog("DELETE_TIQ_ID           # "+id_key);       
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("DELETE FROM tiquets WHERE id_tiq=?");
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
             SystemUtils.escriuNouLog("DELETE_TIQ_IN_DB_ERROR # " + e);
        }
        //retorna el resultat de l'operació 0 - Error i 1 - Correcte       
        return result;
    }  
     
     
     /**
     * Mètode que buscar el id d'un usuari a la Bd's
     * @param incidencia String amb el nom introduit en el login
     * @return retorna  el id del usuari té a la Bd's
     * @throws SQLException 
     */
    public int cercaIdTiquet(String incidencia) throws SQLException {
        //Variable que guarda el resultat de la consulta  que seraà el rol que té
        //l' usuari
        int id = 0;
        //definició del la consuta encriptant la contrasenya, a la Bd's està guardada així 
        String query = "select id_tiq from tiquets where incidencia = " + "'" + incidencia  + "'";
        Statement stmt = conectar.createStatement();
        //Executar la consulta
        ResultSet result = stmt.executeQuery(query);
        //Agafem el valor del camp de la Bd's i el guardem a id
        if (result.next()) {
            id = result.getInt("id_tiq");
        }
        //Retormen el seu valor
        return id;
    } 
    
     /**
     * Mètode que ens permet fer l'alta d'un usuari dins la taula usuraris
     * @param estat
     * @param id_tiq
     * @return retornem un 0 si l'alta no s'ha pogut fer i 1 si s'ha fet satisfactoriament
     * @throws SQLException
     * @throws IOException 
     */
    public int canviStatus(String estat, String id_tiq) throws SQLException, IOException{
        //Contador que recullirar el resultat de l'operació 0 - error i 1 - satisfactori              
        int result =0;
        //Afegim en el log l'operació de modificació demanada pel client
        SystemUtils.escriuNouLog("UPDATE_TIQ_STATUS_ID # "+ id_tiq);       
        //Definició del sentecia SQL per poder introduir el seus valors al camp corresponent
        String sentenciaCrear = ("UPDATE tiquets SET estat = ? WHERE id_tiq = ?");
        //Definim el PreparedStatement   
        PreparedStatement sentence_ready;
        //preparem i executem la SQL per fer la modificació        
        try {
            sentence_ready = conectar.prepareStatement(sentenciaCrear);   
            
            sentence_ready.setInt(1, Integer.parseInt(estat)); //estat
            sentence_ready.setInt(2,Integer.parseInt(id_tiq)); //id_tiq
            
            //Recollim el resultat de l'alta
            result = sentence_ready.executeUpdate();
            sentence_ready.close();
        } catch (Exception e) {
             //Afegim en el log l'operació modificació ha donar error
             SystemUtils.escriuNouLog("UPDATE_TIQ_IN_DB_ERROR # " + e);
        }
         //Retornem el resultat de l'operació 0 - Error i 1 - Correcte       
        return result;
    }
    
    
    
    
}
