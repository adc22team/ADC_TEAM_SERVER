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
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Carles Fugarolas
 */
public class ServerFilUsuaris extends Thread {

    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private int id_conn;
    private Server server;
    private String[] missatge;
    private BigInteger share_key;
    /**
     * Mètode constructor del la classe ServerFilUsuaris extesa Thread
     * @param sc estableix la connexió
     * @param in DataInputStream
     * @param out DataOutputStream
     * @param missatge crida que el client cap el servidor
     * @param id_conn  el id de connexió obtingut al fer el login 
     * @param server servidor que ha creat el nou fil
     * @param share_key
     */
   
    public ServerFilUsuaris(Socket sc, DataInputStream in, DataOutputStream out, 
                            String[] missatge, int id_conn, Server server,  BigInteger share_key) {
        this.sc = sc;
        this.in = in;
        this.out = out;
        this.id_conn = id_conn;
        this.server = server;
        this.missatge = missatge;
        this.share_key = share_key;
    }

    /**
     * Mètode run per atendre les peticions del l'usuari
     */
    @Override
    public void run() {

        try {
            // - - - S E R V E R    T I Q  I S S U E S    G E S T I O    U S U A R I S - - -
            try { 
                // Registrar que s'està demanant l'alta d'un nou usuari
                SystemUtils.escriuNouLog("OPEN_DB_CONECTION_TIQ #");
                //Establir la connexió a la BD's  
                MetodesSQLgestioUsuaris conn = new MetodesSQLgestioUsuaris();
                conn.establirConnexio();
                
                EncrypDecryp ed = new  EncrypDecryp();
                
               SystemUtils.escriuNouLog("CONECTION_SUCCEFULLY_DB_TIQ #");
               
               String sql;
               int result;
                                
               SystemUtils.escriuNouLog("CRIDA_A_EXECUTAR# "+ missatge[1]);
             
                switch (missatge[1]) {
                    
                    case "USER_NEW":
                       
                        // Registrar que s'està demanant l'alta d'un nou usuari
                        SystemUtils.escriuNouLog("ADD_NEW_USER_IN_BD #");
                        //Executa la alta en la base de dades, pasant tots els camps
                        //dades = missatge[2].split(",");
                        result =conn.altaUser(missatge);
                        //Enviem el resultat de l'operació 0 - error i 1  - ok al client
                         out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                         break;

                    case "USER_DELETE":
                        
                        // Registrar que s'està fent la baixa d'un nou usuari
                        SystemUtils.escriuNouLog("DELETE_USER_IN_DB #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari                     
                        result =conn.baixaUser(Integer.parseInt(missatge[2]));
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_DELETE_USER_ID # " +result);
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                          out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                        
                        break;

                    case "USER_MODIFI":
                        // Registrar en el log que s'està fent una modificació usuari                      
                        SystemUtils.escriuNouLog("MODIFI_NEW_USER_IN_BD #");
                        //Executa la modificació en la base de dades, pasant com a paràmete els camps
                        result =conn.modificarUser(missatge);
                        //Registrar el resultat de l'operació  0 malament | 1 correcte en l'arxiu log
                         SystemUtils.escriuNouLog("MODIFI_UPDATE_USER_RESULT # " + result);
                        //Enviem el resultat de l'operació  0 malament | 1 correcte al client
                         out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                                               
                         break;
                        
                    case "USER_QUERY":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_USER_QUERY #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> usuariArrayList = new ArrayList<>();
                       
                        sql =SystemUtils.formatLlistat("select * from usuaris ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL USER_QUERY # "+ sql);
                        
                        //Guardem en un ArrayList els registres trobats
                        usuariArrayList  = conn.consultaSqlUsuaris(sql);
                        //Enviem el nombre total de elements de la llista al client
                          out.writeUTF(ed.encryptedText(String.valueOf(usuariArrayList.size()),share_key.toByteArray()));
                        
                        //Enviar les dades reculllides de la consulta al client
                        for(int i = 0; i < usuariArrayList.size(); i++){
                            //Enviem el registres separats per "," al client
                            out.writeUTF(ed.encryptedText(usuariArrayList.get(i),share_key.toByteArray()));
                            //Registrem els enviaments al l'arxiu lg's
                            SystemUtils.escriuNouLog(usuariArrayList.get(i));
                        }
                        break;
                    case "USER_QUERY_GRID":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_USER_QUERY_GRID #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> usuariArrayListGrid = new ArrayList<>();
                        //Guardem en un ArrayList els registres trobats
                        
                        sql =SystemUtils.formatLlistat("select * from public.usuaris_grid ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL USER_QUERY_GRID # "+ sql);
                        
                        usuariArrayListGrid  = conn.consultaSqlGrid(sql);
                        //Enviem el nombre total de elements de la llista al client
                        out.writeUTF(ed.encryptedText(String.valueOf(usuariArrayListGrid.size()),share_key.toByteArray()));
                        
                        //Enviar les dades reculllides de la consulta al client
                        for(int i = 0; i < usuariArrayListGrid.size(); i++){
                            //Enviem el registres separats per "," al client
                            out.writeUTF(ed.encryptedText(usuariArrayListGrid.get(i),share_key.toByteArray()));
                            //Registrem els enviaments al l'arxiu lg's
                            SystemUtils.escriuNouLog(usuariArrayListGrid.get(i));
                        }
                        break;   
                        
                    case "USER_QUERY_COUNT":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_USER_QUERY_COUNT #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> usuariArrayListCount = new ArrayList<>();
                        //Guardem en un ArrayList els registres trobats
                        
                        sql =SystemUtils.formatLlistat("select * from usuaris ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL USER_QUERY_COUNT # "+ sql);
                        
                        usuariArrayListCount  = conn.consultaSqlUsuaris(sql);
                        //Enviem el nombre total de elements de la llista al client
                          out.writeUTF(ed.encryptedText(String.valueOf(usuariArrayListCount.size()),share_key.toByteArray()));

                        break;    
                        
                    case "USER_FIND":
                       
                         // Registrar que s'està fent la cerca
                        SystemUtils.escriuNouLog("FIND_ID_IN_DB #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari
                        result =conn.cercaIdUsuari(missatge[2]);
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_FIND_USER_ID # " + result);
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                        out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                        
                        break;
                        
                    case "USER_EXIT":
                        
                        // Registrar en el log que s'està fent una sortidade l'aplicatiu  
                        SystemUtils.escriuNouLog("EXECUTE_USER_EXIT_ServerfilsUsuaris_#" +id_conn);
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id_conn);
                        
                        break;

                    default:
                        
                        SystemUtils.escriuNouLog("BAD_COMMAND_SEND_FORCE_USER_EXIT # " + missatge[0]);
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id_conn);
                }
                
                //Tanquem la comunicacio amb la BD's
                conn.tancarConexio();
                SystemUtils.escriuNouLog("CLOSE_DB_CONECTION_TIQ #" + id_conn);
                    
            } catch (IOException ex) {
            } catch (SQLException ex) {
                Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
            }

            sc.close();
           
            //Registrar en el logs el llistat de tots els usuaris guardats al map
            SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap          # " + server.getMapUsuaris());
            
        } catch (IOException ex) {
            Logger.getLogger(ServerFilUsuaris.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
}
