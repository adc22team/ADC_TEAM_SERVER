/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package classes;

import utilitats.SystemUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
/**
 *
 * @author Carles Fugarolas
 */
public class Server {
    //Variables privades  utilitzades per la classe
    private HashMap<Integer, String> mapUsuaris = new HashMap<>();//Creating HashMap   
    private Socket sc;
    private ServerSocket server;
  

    //Establir la connexió a la BD's  
    Connexio conn = new Connexio();

    //Contructor i getter i setter necessaris 
    public Server(int port) throws IOException {

        server = new ServerSocket(port);

    }

    public HashMap<Integer, String> getMapUsuaris() {
        return mapUsuaris;
    }

     /**
     * Aquest mètode en permet eliminar id i el nom de l'usuari que ha realitzat un logout
     * de la llista d'usuaris actius
     * @param id el numero generat pel sistema al fer el login
     */
    public void esborrar(int id) {
        //Esborrar el usuari i la seva sessió al HasMap
        mapUsuaris.remove(id);

    }

    /**
     * Aquest mètode en permet afegir un nou id de connexió i el nom de l'usuari
     * que ha fet el login correctament
     * @param id el numero generat pel sistema
     * @param nomClient  el nom de l'usuari logat
     */
    private void afegir(int id, String nomClient) {
        //Afegim el usuari i la seva sessió al HasMap
        mapUsuaris.put(id, nomClient);

    }

    /**
     * Aquest és el mètode principal de la classe que permet la creació d'un fil
     * d'un nou servidor, establint una connexió amb la Bd's i quedan a l'escolta
     * dels clients
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void obrirServer() throws IOException, SQLException {

        //Establir la connexió a la BD's  
        conn.establirConnexio();

       try {
          
            while (true) {

                //Registro que el servidor està a l'espera en el log's
                SystemUtils.escriuNouLog("SERVER_online_waiting_for_request");   
                
                sc = server.accept();

                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

         /*      SystemUtils.escriuNouLog("clau_publica_ per implementar");
                //Llegir la clau pública del client
                SystemUtils.escriuNouLog(in.readUTF());
                //Enviament de la clau pública del client
                out.writeUTF(            "clau_publica_ per implementar");
         */       
                //Llegim la clau pública del client
                String clauPublicaClient = in.readUTF();
                SystemUtils.escriuNouLog("Valor llegit clau pública client readUTF : " + clauPublicaClient);

                String[] claus_ps = SystemUtils.clausServer(clauPublicaClient).split(",");

                System.out.println("Valor de claus[0]  server  public_key    : " + claus_ps[0]);
                System.out.println("Valor de claus[1]  server   share_key    : " + claus_ps[1]);

                //Enviem la clau pública del servidor    
                out.writeUTF(claus_ps[0]);

                BigInteger share_key = new BigInteger(claus_ps[1]);
                SystemUtils.escriuNouLog("Valor share_key en el  server      : " +share_key);
                   
                //Llegir la crida del client
                //format id_conn,CRIDA,....,...
                String resposta = SystemUtils.decryptedText(in.readUTF(),share_key.toByteArray());
                SystemUtils.escriuNouLog("USER_RESPONSE # " + resposta);

                //Descompondre la resposta del client, en un array
                String[] missatge = resposta.split(",");
                //missatge[0] - id_conn
                //missatge[1] - crida
                //missatge[2] - usuari
                //missatge[3] - password
                
                //Convertir el camp id_com string a numeric
                int id_conn = Integer.parseInt(missatge[0]);
                
                SystemUtils.escriuNouLog("VALUE_id_conn # "+ id_conn);
                //Vol dir que estic fent la crida de LOGIN
                if(id_conn == 0){

                    SystemUtils.escriuNouLog("ENCRYTED_PASSWORD_ALG             # "+ SystemUtils.convertirSHA256(missatge[3]));       
                    //Mira si l'usuari existeix a la Bd's i si la contrasenya és vàlida
                    int registres = conn.loginValit(missatge[2], missatge[3]);

                    //Si existeix el usuari retorna 1 i 0 si no valida
                    SystemUtils.escriuNouLog("SQL_RESPONSE_VALIDATE_USER        # " + registres);

                    //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                    SystemUtils.escriuNouLog("SERVER_SHOW_ID_CONN_USER_RECEIVED # " + id_conn);
  
                    //El ususari ha fet el login correcte
                    if (registres == 1) {

                        //Gereno un id_conn nou aleatori
                        int new_id_conn = SystemUtils.generaNumAleatorio(100, 900);

                        //Afegim el usuari i la seva sessió al HasMap
                        afegir(new_id_conn, missatge[2]);

                        //Està entrant per la pantalla del login NO TE ID i és un usuari validat
                        SystemUtils.escriuNouLog("SERVER_ADD_USER_AND_ID_CONN-IN_HashMap  # "
                                + new_id_conn + " - " + missatge[2]);
                        //Registre els usuaris que hi han actius
                        SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap # "
                                + mapUsuaris);

                        //Enviem el ID# assignat a l'usuari, al servidor
                        out.writeInt(new_id_conn); 
                        SystemUtils.escriuNouLog("SERVER_SEND_NEW_ID_CONN_USER_OK         # "
                                                        + new_id_conn);
                        //Enviar el rol que té l'usuari.
                        int rol = conn.rolUsuari(missatge[2], missatge[3]);
                        out.writeInt(rol);
                        SystemUtils.escriuNouLog("SERVER_SEND_ROLE_USER                   # "
                                                        + rol);
                    } else {
                        //No te ID i el usuari / contrasenya no es correcte
                        //Enviem el ID# assignat a l'usuari si el ID# = 0 ERROR
                        out.writeInt(0);
                        SystemUtils.escriuNouLog("SERVER_SEND_ID_CONN_USER_WRONG          # " + id_conn);
                    }
                } else {
                    //Te id
                    SystemUtils.escriuNouLog("crida a la gestió de fils "+missatge[1]);
                    // Iniciem el fil amb el client
                    GestioFils(sc, in, out, missatge, id_conn, this, share_key);   
                }
            }
       } catch (IOException ex) {
          Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
       }
        //Tanquem la connexió a la Bd's
        conn.tancarConexio();
        //Tanquem el fil
        sc.close();     
    }

    /**
     * Aquest mètode permet genera un nou fil per atendre les peticions de l'usuari logat i
     * deixan lliure el servidor perquè rebi més peticions d'altres usuaris
     * @param socket estableix la connexió
     * @param in DataInputStream
     * @param out DataOutputStream
     * @param missatge crida que el client cap el servidor
     * @param id_conn el id de connexió obtingut al fer el login 
     * @param server servidor que ha creat el nou fil
     * @throws IOException 
     */
    public  void GestioFils(Socket socket, DataInputStream in, DataOutputStream out, String[] missatge, 
                            int id_conn, Server server, BigInteger share_key) throws IOException{
     
        //USER_ crista a la classe que gestiona els usuaris i la seva persistència
        switch (missatge[1].substring(0, 5)) {
            //USER_ crista a la classe que gestiona els usuaris i la seva persistència
            case "USER_":
                                
                ServerFilUsuaris filusuaris = new ServerFilUsuaris(sc, in, out, missatge, id_conn, this,share_key);
                filusuaris.start();
              
                break;
                  
            case "DEPA_":
               
                ServerFilDepartaments fildepart = new ServerFilDepartaments(sc, in, out, missatge, id_conn, this);
                fildepart.start();  
                break;
                
            case "TIQU_":
                
                //TIQU_ crista a la classe que gestiona els tiquets i la seva persistència,
                //No està implementada    
                break;
                
            case "ROLE_":
                
                ServerFilRols filrols = new ServerFilRols(sc, in, out, missatge, id_conn, this);
                filrols.start();
                break; 
            
            default:
                //Si la crida enviada pel client no és correcte, executem la crida forçada de sortida
                //Escriu la sortida en el registre
                SystemUtils.escriuNouLog("BAD_COMMAND_SEND_FORCE_EXIT # " + missatge[1] );
                break;
        }
    }
}
