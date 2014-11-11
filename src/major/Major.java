/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.util.Properties;
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

        front f=new front();
        f.show();
        f.connect2();
        
        Properties props = System.getProperties();
props.setProperty("mail.store.protocol", "imaps");
try {
    Session session = Session.getDefaultInstance(props, null);
    javax.mail.Store store = session.getStore("imaps");
    store.connect("imap.gmail.com", "shanu.latest@gmail.com", "zxcvbnmzxc");
    javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
    for (javax.mail.Folder folder : folders) {
        if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
            System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
        }
    }
} catch (MessagingException e) {
    e.printStackTrace();
}
        // Display connect dialog.
      // client.connect();
    }
}
