/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

/**
 *
 * @author hitesh
 */
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;

/* This class displays a dialog for entering e-mail
   server connection settings. */
public class ConnectDialog extends JDialog {
    
    // These are the e-mail server types.
 //   private static final String[] TYPES = {"pop3", "imap"};
    
    // Combo box for e-mail server types.
 //   private JComboBox typeComboBox;
    
    // Server, username and SMTP server text fields.
    private JTextField usernameTextField;
   
    
    // Password text field.
    private JPasswordField passwordField;
    
    // Constructor for dialog.
    public ConnectDialog(Frame parent) {
        // Call super constructor, specifying that dialog is modal.
        super(parent, true);
    //    setBounds ( 0, 0, 300, 799 );
        setSize(new Dimension(300, 300));
   //  parent.setSize(null);  
        // Set dialog title.
        setTitle("Add Account");
        
        // Handle closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });
        
        // Setup settings panel.
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(
                BorderFactory.createTitledBorder("Connection Settings"));
        settingsPanel.setSize(600, 300);
        GridBagConstraints constraints;
        GridBagLayout layout = new GridBagLayout();
        settingsPanel.setLayout(layout);
       
    //    typeComboBox = new JComboBox(TYPES);
     
    //    layout.setConstraints(typeComboBox, constraints);
  //      settingsPanel.add(typeComboBox);
    
        JLabel usernameLabel = new JLabel("Username:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(usernameLabel, constraints);
        settingsPanel.add(usernameLabel);
        usernameTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.weightx = 1.0D;
        layout.setConstraints(usernameTextField, constraints);
        settingsPanel.add(usernameTextField);
        // for space-----
        JLabel t = new JLabel("");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 5, 0);
        layout.setConstraints(t, constraints);
        settingsPanel.add(t);
        JLabel t1 = new JLabel("");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
         constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 1.0D;
        layout.setConstraints(t1, constraints);
        settingsPanel.add(t1);
        JLabel passwordLabel = new JLabel("Password:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 5, 0);
        layout.setConstraints(passwordLabel, constraints);
        settingsPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 1.0D;
        layout.setConstraints(passwordField, constraints);
        settingsPanel.add(passwordField);
     
        
        // Setup buttons panel.
        JPanel buttonsPanel = new JPanel();
        JButton connectButton = new JButton("Add");
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionConnect();
            }
        });
        buttonsPanel.add(connectButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCancel();
            }
        });
        buttonsPanel.add(cancelButton);
        
        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(settingsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        
        // Size dialog to components.
    //    pack();
        
        // Center dialog over application.
        setLocationRelativeTo(parent);
    }
    
    // Validate connection settings and close dialog.
    private void actionConnect() {
        if ( usernameTextField.getText().trim().length() < 1
                || passwordField.getPassword().length < 1
               ) {
            JOptionPane.showMessageDialog(this,
                    "One or more settings is missing.",
                    "Missing Setting(s)", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
             Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
         Statement stmt=con.createStatement();  
       //  String org=passwordField.getPassword();
        ResultSet rs=stmt.executeQuery("insert into userdatamailclient values('"+usernameTextField.getText().trim()+"','"+new String(passwordField.getPassword())+"')");  
            rs.close(); 
        stmt.close();
        con.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "DB error",
                    "DB Problem", JOptionPane.ERROR_MESSAGE);
        }
        
        // Close dialog.
        dispose();
    }
    
    // Cancel connecting and exit program.
    private void actionCancel() {
    //    System.exit(0);
        dispose();
    }
    
    // Get e-mail server type.
   // @Override
    
    
    // Get e-mail server.
  
    // Get e-mail username.
    public String getUsername() {
        return usernameTextField.getText();
    }
    
    // Get e-mail password.
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
 
}

