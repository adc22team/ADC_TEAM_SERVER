package classes;

import utilitats.SystemUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
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
            // - - - S E R V E R    T I Q  I S S U E S    G E S T I O    U S U A R I S - - -
            try {     
                //Establir la connexió a la BD's  
                MetodesSQLgestioUsuaris conn = new MetodesSQLgestioUsuaris();
                conn.establirConnexio();
                
                //Descomposar la resposta rebuda pel client
                String[] missatge = comanda.split(",");
                                
                switch (missatge[0]) {
                    case "USER_NEW":

                        SystemUtils.escriuNouLog("ADD_NEW_USER_IN_BD #");
                     
                        int result =conn.altaUser(missatge);
                        
                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(result);
                         break;

                    case "USER_DELETE":
                        
                        SystemUtils.escriuNouLog("DELETE_USER_IN_DB #");
                        
                        int resultat =conn.baixaUser(Integer.parseInt(missatge[1]));
                        
                        SystemUtils.escriuNouLog("RESULT_DELETE_USER_ID # " +resultat);
                        
                        //Enviem el  resultat de l'operació 1 = OK                      
                        out.writeInt(resultat);
                        
                        break;

                    case "USER_MODIFI":
                                               
                        SystemUtils.escriuNouLog("MODIFI_NEW_USER_IN_BD #");
                     
                        int update =conn.modificarUser(missatge);
                        
                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(update);
                        
                        SystemUtils.escriuNouLog("MODIFI_UPDATE_USER_RESULT # "
                                                +update);
                         break;
                        
                    case "USER_QUERY":
                        
                        SystemUtils.escriuNouLog("EXECUTE_USER_QUERY #");

                        ArrayList<String> usuariArrayList = new ArrayList<String>();
                        
                        usuariArrayList  = conn.consultaSqlUsuaris(missatge[1]);
                        
                        //Enviem el nombre total de elements de la llista
                        out.writeInt(usuariArrayList.size());
                        
                        //Enviar les dades reculllides de la consulta al client
                        for(int i = 0; i < usuariArrayList.size(); i++){
                            out.writeUTF(usuariArrayList.get(i));
                            SystemUtils.escriuNouLog(usuariArrayList.get(i));
                        }
                        break;

                    case "USER_EXIT":
                        
                        SystemUtils.escriuNouLog("EXECUTO CRIDA EXIT");
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id, nomClient);
                        
                        break;

                    default:
                }
                
                //Tanquem la comunicacio amb la BD's
                conn.tancarConexio();
                    
            } catch (IOException ex) {
            } catch (SQLException ex) {
                Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
            }

            sc.close();
            //Registrar en el logs el llistat de tots els usuaris guardats al map
            SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap          # " + server.getMapUsuaris());
            SystemUtils.escriuNouLog("SERVER_thread_SHOW_USER_LOG_OUT_ServerFilUsuaris # " + nomClient + " ID#" + id);

        } catch (IOException ex) {
            Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
}
