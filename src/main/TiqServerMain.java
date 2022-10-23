/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SystemUtils | Templates
 * and open the template in the editor.
 */
package main;

import classes.Server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static utilitats.SystemUtils.agafarDataHoraSistema;

/**
 *Aquesta classe te implementats la interficie gràfica del servidor
  * @author Carles Fugarolas
  
 */

public class TiqServerMain extends javax.swing.JFrame {

    /**
     * Creates new form InterficieServer
     */
    private static boolean status = false;
    filServer hb = new filServer();
    
     /**
    * En el constructor fem la crida a l'entorn gràfic del servidor
    */
    public TiqServerMain() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelTittle = new javax.swing.JLabel();
        jLabelClose = new javax.swing.JLabel();
        btn_logs = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextAreaLogs = new javax.swing.JTextArea();
        btn_start = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabelTittle.setFont(new java.awt.Font("Segoe UI Light", 0, 36)); // NOI18N
        jLabelTittle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTittle.setText("Server - TIQ Issues  ");

        jLabelClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imatges/icons8-close-24.png"))); // NOI18N
        jLabelClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelCloseMouseClicked(evt);
            }
        });

        btn_logs.setBackground(new java.awt.Color(73, 181, 172));
        btn_logs.setFont(new java.awt.Font("Segoe UI", 0, 21)); // NOI18N
        btn_logs.setForeground(new java.awt.Color(255, 255, 255));
        btn_logs.setText("LOG");
        btn_logs.setActionCommand("");
        btn_logs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_logsActionPerformed(evt);
            }
        });

        TextAreaLogs.setEditable(false);
        TextAreaLogs.setBackground(new java.awt.Color(204, 204, 204));
        TextAreaLogs.setColumns(20);
        TextAreaLogs.setForeground(new java.awt.Color(51, 51, 51));
        TextAreaLogs.setRows(5);
        TextAreaLogs.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(TextAreaLogs);

        btn_start.setBackground(new java.awt.Color(73, 181, 172));
        btn_start.setFont(new java.awt.Font("Segoe UI", 0, 21)); // NOI18N
        btn_start.setForeground(new java.awt.Color(255, 255, 255));
        btn_start.setText("START");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(255, 255, 255)
                .addComponent(jLabelTittle, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelClose)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_logs, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(195, 195, 195))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_start, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(177, 177, 177))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelClose)
                    .addComponent(jLabelTittle))
                .addGap(38, 38, 38)
                .addComponent(btn_logs, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(btn_start, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

     /**
    * Aquest mètode fa la crida a la creació d'un fil que executi la classe server
    * que quedarà a l'espera de les crides dels clients
    * @param evt un event del tipus ActionEvent 
    *
    * @return no retorna res (void)
    */
    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed
        if (status == false) {
            hb.start();
            status = true;
            btn_start.setText("RUNNING");
            TextAreaLogs.setText(agafarDataHoraSistema()[0] + ";" + agafarDataHoraSistema()[1] + " - SERVER_online_waiting_for_request");
        } else {
            JOptionPane.showMessageDialog(this, "SERVER STATUS - RUNNING");
        }
    }//GEN-LAST:event_btn_startActionPerformed

    /**
    * Aquest mètode carrega l'arxiu de log's del programa generat fins aquest monent
    * sinó el troba genera un nou arxiu en blanc.
    * @param evt un event del tipus ActionEvent 
    *
    * @return no retorna res (void)
    */
    private void btn_logsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_logsActionPerformed
       
        TextAreaLogs.setText(null);

        File f = new File("logs.txt");
        //Si no existeix el creem
        if (!f.exists()) {
            try {
                TextAreaLogs.append(agafarDataHoraSistema()[0] + ";" + agafarDataHoraSistema()[1] +"- Creat arxiu d'incidències no EXISTEIX\n");
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Llegim el,contigut del l'arxiu de log's
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            String log;
            while ((log = br.readLine()) != null) {
                TextAreaLogs.append(log + "\n");
            }
            //Tancar l'arxiu
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TiqServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        TextAreaLogs.append(agafarDataHoraSistema()[0] + ";" + agafarDataHoraSistema()[1] +" - Log's carregats correctament\n");
    }//GEN-LAST:event_btn_logsActionPerformed

     /**
    * Aquest mètode executa la sortida del programa, si s'ha clicat en la creu 
    * de sortida.
    * @param evt un event del tipus ActionEvent 
    *
    * @return no retorna res (void)
    */
    private void jLabelCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCloseMouseClicked

        System.exit(0);
    }//GEN-LAST:event_jLabelCloseMouseClicked
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TiqServerMain().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea TextAreaLogs;
    private javax.swing.JButton btn_logs;
    private javax.swing.JButton btn_start;
    private javax.swing.JLabel jLabelClose;
    private javax.swing.JLabel jLabelTittle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}

 /**
    * Aquest classe s'encarrega de crear un fil d'espera en segon pla del servidor
   
    */
class filServer extends Thread {

    @Override
    public void run() {
        try {
            //Instanciem el servidor
            Server server = new Server(5000);
            //Obrim el servidor
            server.obrirServer();

        } catch (IOException ex) {
            Logger.getLogger(filServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(filServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
