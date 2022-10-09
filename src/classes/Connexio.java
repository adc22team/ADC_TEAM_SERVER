package classes;

import java.sql.*;

public class Connexio {

    Connection conectar = null;

    String user = "tiqinssues";
    String passwd = "password";
    String bd = "tiq";
    String ip = "192.168.1.134";
    String port = "5432";

    String cadena = "jdbc:postgresql://" + ip + "/" + bd;

    public Connection establirConnexio() {

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

    public void consultaSqlUsuaris(String consulta) throws SQLException {

        int idUsuari;
        String ulogin;
        String upassword;

        int tipusUsuari;
        String email;
        String telefon;
        int idClient;

        Statement stmt = conectar.createStatement();
        String query = consulta;
        ResultSet result = stmt.executeQuery(query);
        while (result.next()) {
            //idUsuari = result.getInt("id");
            idClient = result.getInt("ID");
            ulogin = result.getString("usuari");
            upassword = result.getString("contrasenya");

            System.out.println(idClient + "\t" + ulogin + "\t" + upassword);
            // Leer registro
        }

    }

}
