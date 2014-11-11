/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.msgpack.MessagePack;

/**
 *
 * @author hitesh
 */
public class front extends javax.swing.JFrame {
 // Message table's data model.
    File file = new File("crawledindiaa.txt");
    int LastUid=0;
    private MessagesTableModel tableModel;
 //   private MessagesTableModel tableModel2;
    JPanel buttonPanel2;
    int jf;
    // Table listing messages.
    private JTable table;
    
  //  table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // This the text area for displaying messages.
    private JTextArea messageTextArea;
    
  /* This is the split panel that holds the messages
     table and the message view panel. */
    private JSplitPane splitPane;
    
    // These are the buttons for managing the selected message.
    private JButton replyButton, forwardButton, deleteButton;
    
    // Currently selected message in table.
    private Message selectedMessage;
    
    // Flag for whether or not a message is being deleted.
    private boolean deleting;
    
    // This is the JavaMail session.
    private Session session;
    
    /**
     * Creates new form front
     */
    private CardLayout cardlayout = new CardLayout();  
    String username;
    String password;
    int totalemails=0;
       //JPanel emailsPanel = new JPanel();
    public front() {
        initComponents();
              if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(front.class.getName()).log(Level.SEVERE, null, ex);
            }
                            }
        setTitle("Intelligent Email Box");
         // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
      //  table.setcol
     //table.getColumn(columnNames[0]).setPreferredWidth(100);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); //for full size
         // Setup file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem addAccountItem = new JMenuItem("Add another Account");
        JMenuItem editAccountItem = new JMenuItem("Edit Account");
        JMenuItem remAccountItem = new JMenuItem("Remove Account");
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        addAccountItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 actionAdd();
            }
        });
        fileMenu.add(addAccountItem);
        fileMenu.add(editAccountItem);
        fileMenu.add(remAccountItem);
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
     // Setup messages table.
        tableModel = new MessagesTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        // Allow only one row at a time to be selected.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Setup E-mails panel.
        
        emailsPanel.setBorder(
                BorderFactory.createTitledBorder("Inbox("+totalemails+")"));
        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(messageTextArea));
        emailsPanel.setLayout(new BorderLayout());
        emailsPanel.add(splitPane, BorderLayout.CENTER);
        
        // Setup buttons panel 2.
        buttonPanel2 = new JPanel();
        replyButton = new JButton("Reply");
        replyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionReply();
            }
        });
        replyButton.setEnabled(false);
        buttonPanel2.add(replyButton);
        forwardButton = new JButton("Forward");
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionForward();
            }
        });
        forwardButton.setEnabled(false);
        buttonPanel2.add(forwardButton);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionDelete();
            }
        });
        deleteButton.setEnabled(false);
        buttonPanel2.add(deleteButton);
        emailsPanel.setSize(1200, 540);
    //    emailsPanel.setExtendedState(javax.swing.JPanel.MAXIMIZED_BOTH);
     //   pack();
      // emailsPanel.add(buttonPanel2)
        getContentPane().setLayout(new BorderLayout());
          getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
      buttonPanel2.setVisible(false);  
      emailsPanel.setVisible(false);
    }
         // Exit this program.
    private void actionExit() {
        // yaha warning message dena ha----------------------------------------------------------------->>>>>
        System.exit(0);
    }
     private void actionAdd()
    {
         ConnectDialog dialog = new ConnectDialog(this);
           dialog.show();
           // yaha pe connect again call hona chiaye
       String u=dialog.getUsername();
       String p=dialog.getPassword();
       connect();
    }
    
       // Connect to e-mail server.
    public void connect() {
        // Display connect dialog.
        //load the driver class  
        try{
        Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
         Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select * from userdatamailclient");  
       
       
        while(rs.next())
        {
            username=rs.getString("emailid");
            password=rs.getString("pass");
          //  System.out.println("hjh hjhjh"+username+password);
        }
        
     
  
        if(username==null){
        // Build connection URL from connect dialog settings.
          ConnectDialog dialog = new ConnectDialog(this);
           dialog.show();
        username=dialog.getUsername();
        password=dialog.getPassword();
        }
        rs.close(); 
        stmt.close();
        con.close();
        }
        catch(Exception e)
        {
            showError("Some problem in DB", true);
        }
      //  System.out.println(dialog.getUsername());
       
    
        // Download message headers from server.
        
}
 
    
     // Show error dialog and exit afterwards if necessary.
    private void showError(String message, boolean exit) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
        if (exit)
            System.exit(0);
    }
    
    // Create a new message.
    private void actionNew() {
        sendMessage(MessageDialog.NEW, null);
    }
      // Send the specified message.
    private void sendMessage(int type, Message message) {
        // Display message dialog to get message values.
        MessageDialog dialog; //this one is displayed when we send a message to anybody
        try {
            dialog = new MessageDialog(this, type, message);
            if (!dialog.display()) {
                // Return if dialog was cancelled.
                return;
            }
        } catch (Exception e) {
            showError("Unable to send message.", false);
            return;
        }
        
        try {
            // Create a new message with values from dialog.
            // this is altered later --?
            String sendingHost="smtp.gmail.com";
        int sendingPort=465;
 
        Properties props = new Properties();
 
        props.put("mail.smtp.host", sendingHost);
        props.put("mail.smtp.port", String.valueOf(sendingPort));
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
 
        props.put("mail.smtp.auth", "true");
 
         Session session1 = Session.getDefaultInstance(props);
 
         Message newMessage = new MimeMessage(session1);
        //    Message newMessage = new MimeMessage(session);
            newMessage.setFrom(new InternetAddress(dialog.getFrom()));
            newMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(dialog.getTo()));
            newMessage.setSubject(dialog.getSubject());
            newMessage.setSentDate(new Date());
            newMessage.setText(dialog.getContent());
            
            // Send new message.
            Transport transport = session1.getTransport("smtps");
 
            transport.connect (sendingHost,sendingPort, username, password);
 
            transport.sendMessage(newMessage, newMessage.getAllRecipients());
 
            transport.close();
 
            JOptionPane.showMessageDialog(null, "Mail sent successfully...","Mail sent",JOptionPane.PLAIN_MESSAGE);
        //    Transport.send(newMessage);
        } catch (Exception e) {
            showError("Unable to send message.", false);
        }
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        emailsPanel = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Intelligent Mail Box");
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.white);

        jButton1.setBackground(new java.awt.Color(209, 72, 54));
        jButton1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\1415554495_Black_Email.png")); // NOI18N
        jButton1.setText("COMPOSE");
        jButton1.setIconTextGap(13);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\1415554581_Black_DownRight-Arrow.png")); // NOI18N
        jButton2.setText("Inbox");
        jButton2.setIconTextGap(10);
        jButton2.setInheritsPopupMenu(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\1415554665_Black_ThumbsUp.png")); // NOI18N
        jButton3.setText("Sent");
        jButton3.setFocusable(false);
        jButton3.setIconTextGap(10);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout emailsPanelLayout = new javax.swing.GroupLayout(emailsPanel);
        emailsPanel.setLayout(emailsPanelLayout);
        emailsPanelLayout.setHorizontalGroup(
            emailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        emailsPanelLayout.setVerticalGroup(
            emailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        jButton4.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\1415554520_Black_Star.png")); // NOI18N
        jButton4.setText("Important");
        jButton4.setIconTextGap(10);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(153, 153, 255));
        jButton5.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\1415554478_Black_Search.png")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\icon175x175.jpeg")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 38, Short.MAX_VALUE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 842, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(emailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1))
                        .addGap(18, 18, 18)
                        .addComponent(emailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        actionNew();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        //connect2();
        emailsPanel.setVisible(true);
              buttonPanel2.setVisible(true); 
        //  getContentPane().setLayout(new BorderLayout());
      //  getContentPane().add(buttonPanel, BorderLayout.NORTH);
      //  getContentPane().add(emailsPanel, BorderLayout.CENTER);
    //      getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
        dispose();
        frontsent f=new frontsent();
        f.show();
        f.connect2();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 front f=new front();f.show();f.connect2();
                
            }
        });
    }
     // Get a message's content.
    public static String getMessageContent(Message message)
    throws Exception {
        Object content = message.getContent();
        if (content instanceof Multipart) {
            StringBuffer messageContent = new StringBuffer();
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                Part part = (Part) multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    messageContent.append(part.getContent().toString());
                }
            }
            return messageContent.toString();
        } else {
            return content.toString();
        }
    }
     // Called when table row selection changes.
    private void tableSelectionChanged() {
    /* If not in the middle of deleting a message, set
       the selected message and display it. */
        if (!deleting) {
            selectedMessage =
                    tableModel.getMessage(table.getSelectedRow());
            showSelectedMessage();
            updateButtons();
        }
    }
    
       // Reply to a message.
    private void actionReply() {
        sendMessage(MessageDialog.REPLY, selectedMessage);
    }
    
    // Forward a message.
    private void actionForward() {
        sendMessage(MessageDialog.FORWARD, selectedMessage);
    }
    
    // Delete the selected message.
    private void actionDelete() {
        deleting = true;
        
        try {
            // Delete message from server.
            selectedMessage.setFlag(Flags.Flag.DELETED, true);
            Folder folder = selectedMessage.getFolder();
            folder.close(true);
            folder.open(Folder.READ_WRITE);
        } catch (Exception e) {
            showError("Unable to delete message.", false);
        }
        
        // Delete message from table.
        tableModel.deleteMessage(table.getSelectedRow());
        
        // Update GUI.
        messageTextArea.setText("");
        deleting = false;
        selectedMessage = null;
        updateButtons();
    }
   
      // Show the selected message in the content panel.
    private void showSelectedMessage() {
        // Show hour glass cursor while message is loaded.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            messageTextArea.setText(
                    getMessageContent(selectedMessage));
            messageTextArea.setCaretPosition(0);
        } catch (Exception e) {
            showError("Unabled to load message.", false);
        } finally {
            // Return to default cursor.
            setCursor(Cursor.getDefaultCursor());
        }
    }
    /* Update each button's state based off of whether or not
     there is a message currently selected in the table. */
    private void updateButtons() {
        if (selectedMessage != null) {
            replyButton.setEnabled(true);
            forwardButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            replyButton.setEnabled(false);
            forwardButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
      // Show the application window on the screen.
    public void show() {
        super.show();
        
        // Update the split panel to be divided 50/50.
        splitPane.setDividerLocation(.5);
    }
    Message[] messages;
    Folder folder;
      // Connect to e-mail server.
    public void connect2() {
        // Display connect dialog.
        //load the driver class  
        try{
        Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
         Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select * from userdatamailclient");  
       
       
        while(rs.next())
        {
            username=rs.getString("emailid");
            password=rs.getString("pass");
          //  System.out.println("hjh hjhjh"+username+password);
        }
        
     
  
        if(username==null){
        // Build connection URL from connect dialog settings.
          ConnectDialog dialog = new ConnectDialog(this);
           dialog.show();
        username=dialog.getUsername();
        password=dialog.getPassword();
        }
        rs.close(); 
        stmt.close();
        con.close();
        }
        catch(Exception e)
        {
            showError("Some problem in DB", true);
        }
      //  System.out.println(dialog.getUsername());
       
    /* Display dialog stating that messages are
       currently being downloaded from server. */
        final DownloadingDialog downloadingDialog =
                new DownloadingDialog(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                downloadingDialog.show();
            }
        });
        
        // Establish JavaMail session and connect to server.
        Store store = null;
        try {
            // Initialize JavaMail session with SMTP server.
            Properties props = new Properties();
            props.setProperty("mail.store.protocol","imaps");
 //           props.put("mail.smtp.host", dialog.getSmtpServer());
            session = Session.getInstance(props, null);
            
            // Connect to e-mail server.
          //  URLName urln = new URLName(connectionUrl.toString());
          store = session.getStore("imaps");
              //        JOptionPane.showMessageDialog(null,""+username+" "+password,"Mail sent",JOptionPane.PLAIN_MESSAGE);
            store.connect("imap.gmail.com", username, password);
            
        } catch (Exception e) {
            // Close the downloading dialog.
            downloadingDialog.dispose();
            
            // Show error dialog.
            showError("Check your internet connection. Or invalid credentials or check your account settings. Not able to connect", true);
        }
        
        // Download message headers from server.
        try {
            // Open main "INBOX" folder.
            folder = store.getFolder("INBOX");
            //Folder folder = store.getFolder("[Gmail]/Sent Mail");
            folder.open(Folder.READ_WRITE);
           jf=folder.getUnreadMessageCount();
            totalemails=folder.getMessageCount();
            System.out.println(totalemails);
            // Get folder's list of messages.
            
            messages = folder.getMessages();
         
            emailsPanel.setBorder(
                BorderFactory.createTitledBorder("Inbox("+totalemails+") [Unread "+jf+"]"));
          //  System.out.println("ye le---------"+messages.length);
            // Retrieve message headers for each message in folder.
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            profile.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, profile);
             backgroundProcess.execute();
            // Put messages in table.
            tableModel.setMessages(messages);
        } catch (Exception e) {
            // Close the downloading dialog.
            downloadingDialog.dispose();
            
            // Show error dialog.
            showError("Unable to download messages.", true);
        }
          downloadingDialog.dispose();
        // Close the downloading dialog.
      //  downloadingDialog.dispose();
         
      //  System.out.println("jfjf");
    }
    void execute1()
    {
        
         try{
                    Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
          
       //  String org=passwordField.getPassword();
       // stmt.executeQuery("insert into emailstoremailclient values('"+s+"','"+sub+"','"+d+"')");  
           
               
            for(int i=0;i<messages.length;i++)
            {
                Statement stmt=con.createStatement(); 
                String str=null;
                Address[] senders =folder.getMessage(i+1).getFrom();
                    if (senders != null || senders.length > 0) {
                             str=senders[0].toString();
                    }
                    String sub=folder.getMessage(i+1).getSubject();
                    if(sub==null)
                        sub="No subject";
                    sub=sub.replace("'","");//coz problem aa re the.... "'" iski vajah se
                    sub=sub.replace("%","");
                    str=str.replace("'", "");
                    sub=sub.replace("^","");
                    sub=sub.replace(",","");
                  //  sub=sub.replace(""","");
          //          sub=sub.replace("^","");
                    sub=sub.replace("&","");
                    sub=sub.replace("*","");
                    //sub=sub.replace("","");
                stmt.executeQuery("insert into emailstoremailclient values"
                        + "('"+str+"','"+sub+
                        "','"+folder.getMessage(i+1).getSentDate().toString()
                        +"')");  
           stmt.close();
            }
            // rs.close(); 
        
        con.close();
             }
                catch(Exception e)
                {
                    System.out.println(e);
                }
    }
   private SwingWorker<Boolean, Void> backgroundProcess = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
            execute1();
            return true;
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
            System.out.println("dkh thread ended");
            // or show result dialog, messageBox, etc      
        }
    };
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel emailsPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
