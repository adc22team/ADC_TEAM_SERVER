/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.IOException;
import java.sql.*;
import utilitats.SystemUtils;
/**
 *
 * @author Carles Fugarolas
 */
public class Connexio {
    
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
        SystemUtils.escriuNouLog("INFO_SQL_CONNECTION # " + cadena);

        try {
            //Configuració dels paràmetres i establiement de la connexió
            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, user, passwd);
            SystemUtils.escriuNouLog("SQL_RESPONSE_successful_connection_user " + user);

        } catch (Exception e) {
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
     * Mètode que valida les credencials introduides per l'usuari per la seva validació
     * @param usuari String amb el nom introduit en el login
     * @param contrasenya String amb el nom introduit en el login
     * @return retorna 1 si troba la coincidencia i 0 sinó la troba
     * @throws SQLException 
     */
    public int loginValit(String usuari, String contrasenya) throws SQLException {
        //Variable que guarda el resultat de la consulta 0 - no ha validat i 1 ha validad
        int cont = 0;
        //definició del la consuta encriptant la contrasenya, a la Bd's està guardada així
        String query = "select * from usuaris where usuari = " + "'" + usuari + "'" + " and contrasenya = '" + SystemUtils.convertirSHA256(contrasenya) + "'";
        Statement stmt = conectar.createStatement();
        //Executar la consulta
        ResultSet result = stmt.executeQuery(query);
        System.out.println("registres trobats cont: " + cont);
        while (result.next()) {
            cont++;
        }
        //La consulta haurà de tornar o cap o un registre, tornem 0 o 1
        return cont;
    }
     /**
     * Mètode que valida les credencials introduides per l'usuari per la seva validació
     * i obte el rol de l'usuari
     * @param usuari String amb el nom introduit en el login
     * @param contrasenya String amb el nom introduit en el login
     * @return retorna 1 si troba la coincidencia i 0 sinó la troba
     * @throws SQLException 
     */
    public int rolUsuari(String usuari, String contrasenya) throws SQLException {
        //Variable que guarda el resultat de la consulta  que seraà el rol que té
        //l' usuari
        int rol = 0;
        //definició del la consuta encriptant la contrasenya, a la Bd's està guardada així 
        String query = "select rol from usuaris where usuari = " + "'" + usuari + "'" + " and contrasenya = '" + SystemUtils.convertirSHA256(contrasenya) + "'";
        Statement stmt = conectar.createStatement();
        //Executar la consulta
        ResultSet result = stmt.executeQuery(query);
        //Agafem el valor del camp de la Bd's i el guardem a rol
        if (result.next()) {
            rol = result.getInt("rol");
        }
        //Retormen el seu valor
        return rol;
    }
}
