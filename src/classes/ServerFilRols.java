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
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Carles Fugarolas
 */
public class ServerFilRols extends Thread {

    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private String nomClient;
    private int id;
    private Server server;
    private String comanda;
    private String[] missatge;
    
    static final String  ID_0           ="id";
    static final String  ROL_1         ="rol";
    
    
    /**
     * Mètode constructor del la classe ServerFilUsuaris extesa Thread
     * @param sc estableix la connexió
     * @param in DataInputStream
     * @param out DataOutputStream
     * @param missatge crida que el client cap el servidor
     * @param comanda crida que vol executar en client en la gestió d'usuaris
     * @param id  el id de connexió obtingut al fer el login 
     * @param server servidor que ha creat el nou fil
     */
    public ServerFilRols(Socket sc, DataInputStream in, DataOutputStream out, String[] missatge, String comanda, int id, Server server) {
        this.sc = sc;
        this.in = in;
        this.out = out;
        this.nomClient = missatge[1];
        this.id = id;
        this.server = server;
        this.missatge = missatge;
        this.comanda = comanda;
    }

    /**
     * Mètode run per atendre les peticions del l'usuari
     */
    @Override
    public void run() {

        try {
            // - - - S E R V E R    T I Q  I S S U E S    G E S T I O    R O L S - - -
            try { 
                // Registrar que s'està demanant l'alta d'un nou usuari
                SystemUtils.escriuNouLog("OPEN_DB_CONECTION_TIQ #");
                //Establir la connexió a la BD's  
                MetodesSQLgestioRols conn = new MetodesSQLgestioRols();
                conn.establirConnexio();
                
                SystemUtils.escriuNouLog("CONECTION_SUCCEFULLY_DB_TIQ #");
                
                String sql;
                int result;
                                                
                switch (missatge[1]) {
                    
                    case "ROLE_NEW":
                        // Registrar que s'està demanant l'alta d'un nou usuari
                        SystemUtils.escriuNouLog("ADD_NEW_ROLE_IN_BD #");
                        //Executa la alta en la base de dades, pasant tots els camps
                        result =conn.altaRol(missatge);
                        //Enviem el resultat de l'operació 0 - error i 1  - ok al client
                        out.writeInt(result);
                         break;

                    case "ROLE_DELETE":
                        
                        // Registrar que s'està fent la baixa d'un nou usuari
                        SystemUtils.escriuNouLog("DELETE_ROLE_IN_DB #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari
                        result =conn.baixaRol(Integer.parseInt(missatge[2]));
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_DELETE_ROLE_ID # " +result);
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                        out.writeInt(result);
                        
                        break;

                    case "ROLE_MODIFI":
                        
                        // Registrar en el log que s'està fent una modificació usuari                      
                        SystemUtils.escriuNouLog("MODIFI_NEW_USER_IN_BD #");
                        //Executa la modificació en la base de dades, pasant com a paràmete els camps
                        result =conn.modificarRol(missatge);
                        //Registrar el resultat de l'operació  0 malament | 1 correcte en l'arxiu log
                         SystemUtils.escriuNouLog("MODIFI_UPDATE_ROLE_RESULT # " + result);
                        //Enviem el resultat de l'operació  0 malament | 1 correcte al client
                        out.writeInt(result);
                                               
                         break;
                        
                    case "ROLE_QUERY":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_ROLE_QUERY #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> rolsArrayList = new ArrayList<String>();
                        
                        sql =formatLlistat("select * from rols ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL ROLE_QUERY # "+ sql);
                        
                        //Guardem en un ArrayList els registres trobats
                        rolsArrayList  = conn.consultaSqlRols(sql);
                        
                        //Enviem el nombre total de elements de la llista al client
                        out.writeInt(rolsArrayList.size());
                        
                        //Enviar les dades reculllides de la consulta al client
                        for(int i = 0; i < rolsArrayList.size(); i++){
                            //Enviem el registres separats per "," al client
                            out.writeUTF(rolsArrayList.get(i));
                            //Registrem els enviaments al l'arxiu lg's
                            SystemUtils.escriuNouLog(rolsArrayList.get(i));
                        }
                        break;
                   
                    case "ROLE_QUERY_COUNT":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_ROLE_QUERY_COUNT_REG #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> rolsArrayListCount = new ArrayList<String>();
                        
                        sql =formatLlistat("select * from rols ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL ROLE_QUERY # "+ sql);
                        
                        //Guardem en un ArrayList els registres trobats
                        rolsArrayListCount  = conn.consultaSqlRols(sql);
                        //Enviem el nombre total de elements de la llista al client
                        out.writeInt(rolsArrayListCount.size());

                        break;    
                        
                    case "ROLE_FIND":
                       
                         // Registrar que s'està fent la cerca
                        SystemUtils.escriuNouLog("FIND_ID_IN_DB_ROLS #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari
                        result =conn.cercaIdRol(missatge[2]);
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_FIND_ROLE_ID # " + result);
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                        out.writeInt(result);
                        
                        break;
                        
                    case "ROLE_EXIT":
                        // Registrar en el log que s'està fent una sortidade l'aplicatiu  
                        SystemUtils.escriuNouLog("EXECUTE_ROLE_EXIT_ServerfilsRols");
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id, nomClient);
                        
                        break;

                    default:
                        
                        SystemUtils.escriuNouLog("BAD_COMMAND_SEND_FORCE_ROL_EXIT # " + missatge[0]);
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id, nomClient);
                }
                
                //Tanquem la comunicacio amb la BD's
                conn.tancarConexio();
                SystemUtils.escriuNouLog("CLOSE_DB_CONECTION_TIQ #");
                    
            } catch (IOException ex) {
            } catch (SQLException ex) {
                Logger.getLogger(ServerFilRols.class.getName()).log(Level.SEVERE, null, ex);
            }

            sc.close();
            //Registrar en el logs el llistat de tots els usuaris guardats al map
            SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap          # " + server.getMapUsuaris());
            SystemUtils.escriuNouLog("SERVER_thread_SHOW_USER_LOG_OUT_ServerFilUsuaris # " + nomClient + " ID#" + id);

        } catch (IOException ex) {
            Logger.getLogger(ServerFilRols.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    
     private static String formatLlistat(String selectBasic, String[] missatge) throws IOException {

        SystemUtils.escriuNouLog("E N T R O   E N  F O R M A T   L L I S T A T ");
        //Select amb la base de la consulta
        String sql = selectBasic;
        String[] filtrat;

        switch (missatge[2]) {
            case "0":
                sql = sql + "order by id";
                break;
            case "1":
                SystemUtils.escriuNouLog("Arribo aqui valor missatge[2] :" + missatge[2]);
                filtrat = missatge[3].split("#");
                SystemUtils.escriuNouLog("valor filtrat[0] :" + filtrat[0]);
                SystemUtils.escriuNouLog("valor filtrat[1] :" + filtrat[1]);
                SystemUtils.escriuNouLog("valor filtrat[2] :" + filtrat[2]);
                sql = sql + "where " + buscarCamp(filtrat[0]) + filtrat[1] + filtrat[2];
                break;
            case "2":

                sql = sql + "order by " + buscarCamp(missatge[3]);
                break;

            case "3":
                SystemUtils.escriuNouLog("Arribo aqui valor missatge[2] :" + missatge[2]);
                filtrat = missatge[3].split("#");
                SystemUtils.escriuNouLog("valor filtrat[0] :" + filtrat[0]);
                SystemUtils.escriuNouLog("valor filtrat[1] :" + filtrat[1]);
                SystemUtils.escriuNouLog("valor filtrat[2] :" + filtrat[2]);
                sql = sql + " where " + buscarCamp(filtrat[0]) + filtrat[1] + filtrat[2];
                sql = sql + " order by " + buscarCamp(missatge[4]);
                break;

            default:
        }

        SystemUtils.escriuNouLog("Resultat de la setencia final SQL : " + sql);

        return sql;
    }
    
    private static String buscarCamp(String camp) {

        String resultat;
        switch (camp) {
            case "0":
                resultat = ID_0;
                break;
            case "1":
                resultat = ROL_1;
                break;
            
            default:
                resultat = "";
        }
        return resultat;
    }

    
    
    
}