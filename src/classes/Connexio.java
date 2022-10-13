package classes;


import java.io.IOException;
import java.sql.*;
import utilitats.SystemUtils;

public class Connexio {

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
    

}
