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
public class ServerFilDepartaments extends Thread {

    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private int id_conn;
    private Server server;
    private String comanda;
    private String[] missatge;
    private BigInteger share_key;
  
    /**
     * Mètode constructor del la classe ServerFilUsuaris extesa Thread
     * @param sc estableix la connexió
     * @param in DataInputStream
     * @param out DataOutputStream
     * @param missatge crida que el client cap el servidoR
     * @param id_conn  el id de connexió obtingut al fer el login 
     * @param server servidor que ha creat el nou fil
     */
    public ServerFilDepartaments(Socket sc, DataInputStream in, DataOutputStream out,
                                 String[] missatge, int id_conn, Server server,BigInteger share_key) {
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
            // - - - S E R V E R    T I Q  I S S U E S    G E S T I O    D E P A R T A M E N T S - - -
            try { 
                // Registrar que s'està demanant l'alta d'un nou usuari
                SystemUtils.escriuNouLog("OPEN_DB_CONECTION_TIQ #");
                //Establir la connexió a la BD's  
                MetodesSQLgestioDepartaments conn = new MetodesSQLgestioDepartaments();
                conn.establirConnexio();
                
                EncrypDecryp ed = new  EncrypDecryp();
                
                
                SystemUtils.escriuNouLog("CONECTION_SUCCEFULLY_DB_TIQ #");
                
                String sql;
                int result;
   
                switch (missatge[1]) {
                    
                    case "DEPA_NEW":
                        // Registrar que s'està demanant l'alta d'un nou usuari
                        SystemUtils.escriuNouLog("ADD_NEW_DEPART_IN_BD #");
                        //Executa la alta en la base de dades, pasant tots els camps
                        result =conn.altaDepartament(missatge);
                        //Enviem el resultat de l'operació 0 - error i 1  - ok al client
                        out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                        
                         break;

                    case "DEPA_DELETE":
                        
                        // Registrar que s'està fent la baixa d'un nou usuari
                        SystemUtils.escriuNouLog("DELETE_DEPART_IN_DB #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari
                        result =conn.baixaDepartament(Integer.parseInt(missatge[2]));
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_DELETE_DEPART_ID # " +result);
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                        out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                        
                        break;

                    case "DEPA_MODIFI":
                        
                        // Registrar en el log que s'està fent una modificació usuari                      
                        SystemUtils.escriuNouLog("MODIFI_NEW_DEPA_IN_BD #");
                        //Executa la modificació en la base de dades, pasant com a paràmete els camps
                        result =conn.modificarDepartament(missatge);
                        //Registrar el resultat de l'operació  0 malament | 1 correcte en l'arxiu log
                         SystemUtils.escriuNouLog("MODIFI_UPDATE_DEPART_RESULT # " + result);
                        //Enviem el resultat de l'operació  0 malament | 1 correcte al client
                        out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray())); 
                                               
                         break;
                        
                    case "DEPA_QUERY":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_DEPART_QUERY #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> departamentsArrayList = new ArrayList<>();
                             
                        sql =SystemUtils.formatLlistat("select * from departaments ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL ROLE_QUERY # "+ sql);
                        
                        //Guardem en un ArrayList els registres trobats
                        departamentsArrayList  = conn.consultaSqlDepartaments(sql);
                        //Enviem el nombre total de elements de la llista al client
                         out.writeUTF(ed.encryptedText(String.valueOf(departamentsArrayList.size()),share_key.toByteArray()));
                        
                        //Enviar les dades reculllides de la consulta al client
                        for(int i = 0; i < departamentsArrayList.size(); i++){
                            //Enviem el registres separats per "," al client
                            out.writeUTF(ed.encryptedText(departamentsArrayList.get(i),share_key.toByteArray()));
                            
                            //Registrem els enviaments al l'arxiu lg's
                            SystemUtils.escriuNouLog(departamentsArrayList.get(i));
                        }
                        break;
                   
                    case "DEPA_QUERY_COUNT":
                        
                        // Registrar en el log que s'està fent una consulta a la Bd's usuaris  
                        SystemUtils.escriuNouLog("EXECUTE_DEPART_QUERY_COUNT_REG #");
                        //Creem un arrayList per gestionar el resultats de las consultas
                        ArrayList<String> departamentsArrayListCount = new ArrayList<>();
                               
                        sql =SystemUtils.formatLlistat("select * from departaments ",missatge);
                        SystemUtils.escriuNouLog("VALOR SQL ROLE_QUERY # "+ sql);
                        
                        //Guardem en un ArrayList els registres trobats
                        departamentsArrayListCount  = conn.consultaSqlDepartaments(sql);
                        
                        //Enviem el nombre total de elements de la llista al client
                        out.writeUTF(ed.encryptedText(String.valueOf(departamentsArrayListCount.size()),share_key.toByteArray()));

                        break;    
                        
                    case "DEPA_FIND":
                       
                         // Registrar que s'està fent la cerca
                        SystemUtils.escriuNouLog("FIND_ID_IN_DB_ROLS #");
                        //Executa la baixa en la base de dades, pasant com a paràmete id_key de l'usuari
                        
                        result =conn.cercaIdDepartament(missatge[2]);
                        //Mostra el resultat de l'operació 0 malament | 1 correcte
                        SystemUtils.escriuNouLog("RESULT_FIND_DEPART_ID # " + result);
                        
                        //Enviem el  resultat de l'operació 0  = INCORRECTE 1 = OK al client                     
                        out.writeUTF(ed.encryptedText(String.valueOf(result),share_key.toByteArray()));
                        
                        break;
                        
                    case "DEPA_EXIT":
                        
                        // Registrar en el log que s'està fent una sortidade l'aplicatiu  
                        SystemUtils.escriuNouLog("EXECUTE_DEPART_EXIT_ServerfilsDepartament");
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id_conn);
                        
                        break;

                    default:
                        
                        SystemUtils.escriuNouLog("BAD_COMMAND_SEND_FORCE_DEPA_EXIT # " + missatge[0]);
                        //Trec de la llista d'usuaris actius al usuari que tanca sessió
                        this.server.esborrar(id_conn);
                }
                
                //Tanquem la comunicacio amb la BD's
                conn.tancarConexio();
                SystemUtils.escriuNouLog("CLOSE_DB_CONECTION_TIQ #");
                    
            } catch (IOException ex) {
            } catch (SQLException ex) {
                Logger.getLogger(ServerFilDepartaments.class.getName()).log(Level.SEVERE, null, ex);
            }

            sc.close();
            //Registrar en el logs el llistat de tots els usuaris guardats al map
            SystemUtils.escriuNouLog("SERVER_SHOW_ACTIVES_USERS_IN_ID_HashMap          # " + server.getMapUsuaris());
  
        } catch (IOException ex) {
            Logger.getLogger(ServerFilDepartaments.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
}
