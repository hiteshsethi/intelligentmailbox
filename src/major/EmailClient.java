/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

/**
 *
 * @author hitesh
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;

// The E-mail Client.
public class EmailClient extends JFrame {
    String username;
    String password;
    int totalemails=0;
    JPanel emailsPanel = new JPanel();
    // Message table's data model.
    private MessagesTableModel tableModel;
    
    // Table listing messages.
    private JTable table;
    
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
    
    // Constructor for E-mail Client.
    public EmailClient() {
        // Set application title.
        setTitle("Intelligent Email Box");
        
        // Set window size.
        setSize(640, 480);
        
        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        
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
        
        // Setup buttons panel.
        JPanel buttonPanel = new JPanel();
        JButton newButton = new JButton("Compose Email");
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionNew();
            }
        });
        buttonPanel.add(newButton);
        
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
        JPanel buttonPanel2 = new JPanel();
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
        
        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(emailsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
    }
    
    // Exit this program.
    private void actionExit() {
        // yaha warning message dena ha----------------------------------------------------------------->>>>>
        System.exit(0);
    }
    
    // Create a new message.
    private void actionNew() {
        sendMessage(MessageDialog.NEW, null);
    }
    
    
    private void actionAdd()
    {
         ConnectDialog dialog = new ConnectDialog(this);
           dialog.show();
           // yaha pe connect again call hona chiaye
       String u=dialog.getUsername();
       String p=dialog.getPassword();
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
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            totalemails=folder.getMessageCount();
            System.out.println(totalemails);
            // Get folder's list of messages.
            Message[] messages = folder.getMessages();
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
                Address[] senders = messages[i].getFrom();
                    if (senders != null || senders.length > 0) {
                             str=senders[0].toString();
                    }
                    String sub=messages[i].getSubject();
                    
                    sub=sub.replace("'","");//coz problem aa re the.... "'" iski vajah se
                stmt.executeQuery("insert into emailstoremailclient values"
                        + "('"+str+"','"+sub+
                        "','"+messages[i].getSentDate().toString()
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
            emailsPanel.setBorder(
                BorderFactory.createTitledBorder("Inbox("+totalemails+")"));
          //  System.out.println("ye le---------"+messages.length);
            // Retrieve message headers for each message in folder.
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, profile);
           
            // Put messages in table.
            tableModel.setMessages(messages);
        } catch (Exception e) {
            // Close the downloading dialog.
            downloadingDialog.dispose();
            
            // Show error dialog.
            showError("Unable to download messages.", true);
        }
        
        // Close the downloading dialog.
        downloadingDialog.dispose();
    }
    
    // Show error dialog and exit afterwards if necessary.
    private void showError(String message, boolean exit) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
        if (exit)
            System.exit(0);
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
    
    // Run the E-mail Client.
    
}