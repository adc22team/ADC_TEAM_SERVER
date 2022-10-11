package classes;

import utilitats.SystemUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFilUsuaris extends Thread {

    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private String nomClient;
    private int id;
    private Server server;
    private String comanda;
    private String[] missatge;
    

    public ServerFilUsuaris(Socket sc, DataInputStream in, DataOutputStream out, String[] missatge, String comanda, int id, Server server) {
        this.sc = sc;
        this.in = in;
        this.out = out;
        this.nomClient = missatge[1];
        this.id = id;
        this.server = server;
        this.missatge = missatge;
        this.comanda = comanda;
    }

    @Override
    public void run() {

        try {
            // - - - S E R V E R  T I Q  I S S U E S  G E S T I O  U S U A R I S - - -
            try {                    
                //Descomposar la resposta
                String[] missatge = comanda.split(",");
                SystemUtils.escriuNouLog("valor de missatge[0] : " + missatge[0].toString() );
                // - - - S E R V I D O R  ---
                switch (missatge[0]) {
                    case "USER_NEW":

                        //Establir la connexió a la BD's  
                        MetodesSQLgestioUsuaris conn = new MetodesSQLgestioUsuaris();

                        SystemUtils.escriuNouLog("ADD_NEW_USER");
                        //Connexio conn = new Connexio();
                        conn.establirConnexio();

                        System.out.println("Arribo aqui");
                        int result =conn.altaUser(missatge);
                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(result);

                        conn.tancarConexio();
                        break;

                    case "USER_DELETE":
                        SystemUtils.escriuNouLog("DELETE_NEW_USER");
                        break;

                    case "USER_MODIFI":
                        SystemUtils.escriuNouLog("MODIFI_NEW_USER");
                        break;

                    case "USER_QUERY_ALL":

                        SystemUtils.escriuNouLog("EXECUTO CRIDA LLISTAT");

                        //Establir la connexió a la BD's  
                        MetodesSQLgestioUsuaris connq = new MetodesSQLgestioUsuaris();
                        //Connexio conn = new Connexio();
                        connq.establirConnexio();
                        connq.consultaSqlUsuaris("select * from usuaris");
                        connq.tancarConexio();
                        break;

                    case "EXIT":
                         SystemUtils.escriuNouLog("EXECUTO CRIDA EXIT");
                        //salir = _EXIT();
                        _EXIT();
                        break;

                    default:
                }
            } catch (IOException ex) {
            } catch (SQLException ex) {
                Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
            }

            sc.close();
            //Registrar en el logs el llistat de tots els usuaris guardats al map
            SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap # " + server.getMapUsuaris());
            SystemUtils.escriuNouLog("SERVER_thread_SHOW_USER_LOG_OUT_ServerFilUsuaris # " + nomClient + " amb ID#" + id);

        } catch (IOException ex) {
            Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //Resposta servidor a la crida del client EXIT
    private boolean _EXIT() {
        //Trec de la llista d'usuaris actius al usuari que tanca sessió
        this.server.esborrar(id, nomClient);

        return true;
    }

}
