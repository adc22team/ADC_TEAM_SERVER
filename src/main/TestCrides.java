/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cfuga
 */
public class TestCrides {

    public static void main(String[] args) throws IOException {

        Socket sc;
        try {
            sc = new Socket("127.0.0.1", 5000);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());

            // Llegir la resposta del servidor al establir la connexió
            String resposta_svr = in.readUTF();
            System.out.println("Resposta: " + resposta_svr);

            //Enviem resposta al servidor amb el usuari i la contrasenya
            out.writeUTF("LOGIN,carles,pwdcarles,555");
            System.out.println("LOGIN,carles,pwdcarles,555");

            System.out.println("Crida : NEW_USER");
            out.writeUTF("MODIFI_USER");

            System.out.println("Crida : EXIT");
            out.writeUTF("EXIT");

        } catch (IOException ex) {
            Logger.getLogger(TestCrides.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
        //Hola

    }

}
