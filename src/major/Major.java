/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

/**
 *
 * @author hitesh
 */
public class Major {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EmailClient client = new EmailClient();
        client.show();
        
        // Display connect dialog.
       client.connect();
    }
}
