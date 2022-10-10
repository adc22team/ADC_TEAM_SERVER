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

public class ServerFil extends Thread {

    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private String nomClient;
    private int id;
    private Server server;
    

    public ServerFil(Socket sc, DataInputStream in, DataOutputStream out, String nomClient, int id, Server server) {
        this.sc = sc;
        this.in = in;
        this.out = out;
        this.nomClient = nomClient;
        this.id = id;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            //Instaciar el fitxer de logs per accedir-hi
            File f = new File("logs.txt");

            try {
                System.out.println("Arribo dins el fil valor missatge[0]" );
                Boolean salir = false;
                // - - - S E R V E R  T I Q  I S S U E S - - -
                while (!salir) {
                    try {
                        //Recullo la petició codificada que fa el client
                        String resposta = in.readUTF();
                        //Descomposar la resposta
                        String[] missatge = resposta.split(",");
                        System.out.println("valor de missatge : " + missatge.toString() );
                        // - - - S E R V I D O R  ---
                        
                        switch (missatge[0]) {
                            case "USER_NEW":
                              
                                //Establir la connexió a la BD's  
                                MetodesSQLgestioUsuaris conn = new MetodesSQLgestioUsuaris();

                                SystemUtils.escriuNouLog(f, "ADD_NEW_USER");
                                //Connexio conn = new Connexio();
                                conn.establirConnexio();
                                
                                System.out.println("Arribo aqui");
                                int result =conn.altaUser(missatge);
                                //Enviem el ID# assignat a l'usuari, al servidor
                                out.writeInt(result);

                                conn.tancarConexio();
                                break;

                            case "USER_DELETE":
                                SystemUtils.escriuNouLog(f, "DELETE_NEW_USER");
                                break;

                            case "USER_MODIFI":
                                SystemUtils.escriuNouLog(f, "MODIFI_NEW_USER");
                                break;

                            case "USERS_QUERY_ALL":
                        
                                SystemUtils.escriuNouLog(f, "EXECUTO CRIDA LLISTAT");

                                //Establir la connexió a la BD's  
                                MetodesSQLgestioUsuaris connq = new MetodesSQLgestioUsuaris();
                                //Connexio conn = new Connexio();
                                connq.establirConnexio();
                                connq.consultaSqlUsuaris("select * from usuaris");
                                connq.tancarConexio();
                                break;
                                
                            case "EXIT":
                                 SystemUtils.escriuNouLog(f, "EXECUTO CRIDA EXIT");
                                salir = _EXIT();
                                break;

                            default:
                        }
                    } catch (IOException ex) {
                    } catch (SQLException ex) {
                        Logger.getLogger(ServerFil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                sc.close();
                //Registrar en el logs el llistat de tots els usuaris guardats al map
                SystemUtils.escriuNouLog(f, "SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap # " + server.getMapUsuaris());

            } catch (IOException ex) {
                Logger.getLogger(ServerFil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SystemUtils.escriuNouLog(f, "SERVER_thread_SHOW_USER_LOG_OUT # " + nomClient + " amb ID#" + id);

        } catch (IOException ex) {
            Logger.getLogger(ServerFil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Resposta servidor a la crida del client EXIT
    private boolean _EXIT() {
        //Trec de la llista d'usuaris actius al usuari que tanca sessió
        this.server.esborrar(id, nomClient);

        return true;
    }

}
