package classes;

import utilitats.SystemUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public class Server {

    private HashMap<Integer, String> mapUsuaris = new HashMap<>();//Creating HashMap   
    private Socket sc;
    private ServerSocket server;

    //Establir la connexió a la BD's  
    Connexio conn = new Connexio();

    public Server(int port) throws IOException {

        server = new ServerSocket(port);

    }

    public HashMap<Integer, String> getMapUsuaris() {
        return mapUsuaris;
    }

    public void setMapUsuaris(HashMap<Integer, String> mapUsuaris) {
        mapUsuaris = mapUsuaris;
    }

    public void esborrar(int id, String nomClient) {
        //Esborrar el usuari i la seva sessió al HasMap
        mapUsuaris.remove(id, nomClient);

    }

    private void afegir(int id, String nomClient) {
        //Afegim el usuari i la seva sessió al HasMap
        mapUsuaris.put(id, nomClient);

    }

    public void obrirServer() throws IOException, SQLException {

        //Establir la connexió a la BD's  
        //Connexio conn = new Connexio();
        conn.establirConnexio();

        //Afegim el usuari i la seva sessió al HasMap
        try {

            File f = new File("logs.txt");
            if (!f.exists()) {
                try {
                    System.out.println("SERVER_CREATE_NEW_LOG_FILE_LOGS.TXT");
                    f.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ServerFil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            SystemUtils.escriuNouLog(f, "SERVER_online_waiting_for_request");
            while (true) {

                sc = server.accept();

                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //Missatge de benvinguda al establir la comunicació
                out.writeUTF("SERVER_SHOW_EHLO_established connection");
                SystemUtils.escriuNouLog(f, "SERVER_SHOW_EHLO_established connection");

                //Recullir el login de l'usuari
                //format LOGIN,usuari,contrasenya,id_conn
                String resposta = in.readUTF();
                SystemUtils.escriuNouLog(f, "USER_RESPONSE # " + resposta);

                //Descompondre la resposta del client, en un array
                String[] missatge = resposta.split(",");
                //missatge[0] - LOGIN
                //missatge[1] - usuari
                //missatge[2] - password
                //missatge[3] - id_conn

                //Convertir el camp id_com string a numeric
                int id_conn = Integer.parseInt(missatge[3]);

                //Mira si l'usuari existeix a la Bd's i si la contrasenya és vàlida
                int registres = conn.loginValit(missatge[1], missatge[2]);

                //Si existeix el usuari retorna 1 i 0 si no valida
                SystemUtils.escriuNouLog(f, "SQL_RESPONSE_VALIDATE_USER # " + registres);

                //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                SystemUtils.escriuNouLog(f, "SERVER_SHOW_ID_CONN_USER_RECEIVED # " + id_conn);

                //Si id_conn == 0 està fent la pantalla de LOGIN
                if (id_conn == 0) {
                    //El ususari ha fet el login correcte
                    if (registres == 1) {

                        //Gereno un id_conn nou aleatori
                        int new_id_conn = SystemUtils.generaNumAleatorio(100, 900);

                        //Afegim el usuari i la seva sessió al HasMap
                        afegir(new_id_conn, missatge[1]);

                        //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                        SystemUtils.escriuNouLog(f, "SERVER_ADD_USER_AND_ID_CONN-IN_HashMap  # "
                                + new_id_conn + " - " + missatge[1]);

                        SystemUtils.escriuNouLog(f, "SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap # "
                                + mapUsuaris);

                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(new_id_conn);
                        SystemUtils.escriuNouLog(f, "SERVER_SEND_NEW_ID_CONN_USER_OK # "
                                + new_id_conn);
                        //Enviar el rol que té l'usuari.
                        int rol = conn.rolUsuari(missatge[1], missatge[2]);
                        out.writeInt(rol);
                        SystemUtils.escriuNouLog(f, "SERVER_SEND_ROLE_USER  # "
                                + rol);

                    } else {
                        //No te ID i el usuari / contrasenya no es correcte
                        //Enviem el ID# assignat a l'usuari -ID = 0 ERROR
                        out.writeInt(0);
                        SystemUtils.escriuNouLog(f, "SERVER_SEND_ID_CONN_USER_WRONG # " + id_conn);
                    }
                } else {
                    //Te id
                    // Iniciem el fil amb el client
                    ServerFil fil = new ServerFil(sc, in, out, missatge[1], id_conn, this);
                    fil.start();
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Tanquem el fil
        sc.close();
        //Tanquem la connexió a la Bd's
        conn.tancarConexio();
    }

}
