/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Session;

/**
 *
 * @author hitesh
 */
public class Major extends javax.swing.JFrame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    //    EmailClient client = new EmailClient();
    //    client.show();
           try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
           try{
         new createDB().makeDB();
           }
           catch(Exception e)
           {
               System.out.println("problem while creating db "+e);
           }
        front f=new front();
        f.show();
        f.setUserPass();
        try {
            f.connect2();
            // client.connect();
          // client.connect();
        } catch (SQLException ex) {
            Logger.getLogger(Major.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
