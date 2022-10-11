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
        conn.establirConnexio();

        //Afegim el usuari i la seva sessió al HasMap
       try {
            
            SystemUtils.escriuNouLog("SERVER_online_waiting_for_request");
            while (true) {

                sc = server.accept();

                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //Missatge de benvinguda al establir la comunicació
                out.writeUTF(            "SERVER_SHOW_EHLO_established connection");
                SystemUtils.escriuNouLog("SERVER_SHOW_EHLO_established connection");

                //Recullir el login de l'usuari
                //format LOGIN,usuari,contrasenya,id_conn
                String resposta = in.readUTF();
                SystemUtils.escriuNouLog("USER_RESPONSE # " + resposta);

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
                SystemUtils.escriuNouLog("SQL_RESPONSE_VALIDATE_USER        # " + registres);

                //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                SystemUtils.escriuNouLog("SERVER_SHOW_ID_CONN_USER_RECEIVED # " + id_conn);

                //Si id_conn == 0 està fent la pantalla de LOGIN
                if (id_conn == 0) {
                  
                    //El ususari ha fet el login correcte
                    if (registres == 1) {

                        //Gereno un id_conn nou aleatori
                        int new_id_conn = SystemUtils.generaNumAleatorio(100, 900);

                        //Afegim el usuari i la seva sessió al HasMap
                        afegir(new_id_conn, missatge[1]);

                        //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                        SystemUtils.escriuNouLog("SERVER_ADD_USER_AND_ID_CONN-IN_HashMap  # "
                                + new_id_conn + " - " + missatge[1]);

                        SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap # "
                                + mapUsuaris);

                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(new_id_conn);
                        SystemUtils.escriuNouLog("SERVER_SEND_NEW_ID_CONN_USER_OK          # "
                                                        + new_id_conn);
                        //Enviar el rol que té l'usuari.
                        int rol = conn.rolUsuari(missatge[1], missatge[2]);
                        out.writeInt(rol);
                        SystemUtils.escriuNouLog("SERVER_SEND_ROLE_USER                    # "
                                                        + rol);
                    } else {
                        //No te ID i el usuari / contrasenya no es correcte
                        //Enviem el ID# assignat a l'usuari -ID = 0 ERROR
                        out.writeInt(0);
                        SystemUtils.escriuNouLog("SERVER_SEND_ID_CONN_USER_WRONG # " + id_conn);
                    }
                } else {
                    //Te id
                    SystemUtils.escriuNouLog("crida a la gestió de fils "+missatge[0]);
                    // Iniciem el fil amb el client
                    GestioFils(sc, in, out, missatge, id_conn, this);   
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
    
    public  void GestioFils(Socket socket, DataInputStream in, DataOutputStream out, String[] missatge, 
                            int id_conn, Server server) throws IOException{
               
        //Recullo la petició codificada que fa el client
        String comanda = in.readUTF();
         
        SystemUtils.escriuNouLog("READ_COMMAND_EXECUTE # "+comanda);
        SystemUtils.escriuNouLog("SELECT_ITEMS_GRUP    # "+comanda.substring(0,5));
        
           switch (comanda.substring(0,5)) {
                    case "USER_":
                        ServerFilUsuaris fil = new ServerFilUsuaris(sc, in, out, missatge,comanda, id_conn, this);
                        fil.start();
                        
                    default:
            }
        
    }

}
