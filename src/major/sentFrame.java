/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author hitesh
 */
public class sentFrame extends javax.swing.JFrame {
private MessagesTableModel tableModel;
    
    // Table listing messages.
    private JTable table;
    
    // This the text area for displaying messages.
    private JTextArea messageTextArea;
    
  /* This is the split panel that holds the messages
     table and the message view panel. */
    private JSplitPane splitPane;
    JPanel buttonPanel2;
    // These are the buttons for managing the selected message.
    private JButton replyButton, forwardButton, deleteButton;
    
    // Currently selected message in table.
    private Message selectedMessage;
    
    // Flag for whether or not a message is being deleted.
    private boolean deleting;
    
    String username,password;
    /**
     * Creates new form sentFrame
     */
    public sentFrame(Message[] m,String u,String p) {
        initComponents();
        setTitle("Intelligent Email Box");
         // Handle window closing events.
        username=u;
        password=p;
      //  setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); //for full size
         tableModel = new MessagesTableModel();
        table = new JTable(tableModel)
                 {
           
    public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
        // get the current row
        Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
        // even index, not selected
        JComponent jc = (JComponent)comp;
        Message mes=tableModel.getMessage(Index_row);
                try {
                    Flags flag=mes.getFlags();
                     if(flag.contains(Flags.Flag.SEEN))
                     {
                          comp.setBackground(Color.lightGray);
                          
                     }
                     else
                     {
                          comp.setBackground(Color.white);
                     }
                     jc.setBorder(new MatteBorder(1, 0, 1, 0, Color.darkGray) );
                } catch (MessagingException ex) {
                    //Logger.getLogger(front.class.getName()).log(Level.SEVERE, null, ex);
                }
       
        
       /* if (Index_row % 2 == 0 && !isCellSelected(Index_row, Index_col)) {
           
        } else {
           
        }*/
        return comp;
    }
};
        table.setRowHeight(table.getRowHeight() + 20);
        table.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        // Allow only one row at a time to be selected.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(messageTextArea));
          splitPane.setDividerLocation(.5);
        sentPanel.setLayout(new BorderLayout());
        
        sentPanel.add(splitPane, BorderLayout.CENTER);
        
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
        sentPanel.setSize(1140, 520);
    //    emailsPanel.setExtendedState(javax.swing.JPanel.MAXIMIZED_BOTH);
     //   pack();
      // emailsPanel.add(buttonPanel2)
        getContentPane().setLayout(new BorderLayout());
          getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
      buttonPanel2.setVisible(true);  
      splitPane.setDividerLocation(.5);
        addToModel(m);
        
    }

     void addToModel(Message[] messages)
    {
        splitPane.setDividerLocation(.5);
        ArrayList m=new ArrayList();
        //here will go the logic for search and variable to searched is "str"
          for (int i = messages.length - 1; i >= 0; i--) {
            
                try{
                   m.add(messages[i]);
            } catch (Exception ex) {
                System.out.println("Sent problem :"+ex);
                //Logger.getLogger(searchFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        try {
            tableModel.setMessagesForSearch(m);
        } catch (Exception ex) {
            System.out.println("This was the error generated in sent box : "+ex);
            //Logger.getLogger(searchFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
     private void actionReply() {
        sendMessage(MessageDialog.REPLY, selectedMessage);
    }
    
    // Forward a message.
    private void actionForward() {
        sendMessage(MessageDialog.FORWARD, selectedMessage);
    }
    
    
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
     private void showSelectedMessage() {
        // Show hour glass cursor while message is loaded.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            messageTextArea.setText(
                    getMessageContent(selectedMessage));
            messageTextArea.setCaretPosition(0);
        } catch (Exception e) {
            System.out.println("Unabled to load message. "+e);
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
     //   final int store=totalemails-table.getSelectedRow();
        Message temp=tableModel.getMessage(table.getSelectedRow());
        
        final int st=temp.getMessageNumber(); //deletecounter++;
        // yaha pe diqat aaegi.......coz jbb different folders mn jaega to row number change hoga.
       //  System.out.println("This is the row number-------->"+(totalemails-table.getSelectedRow()));
       //  System.out.println("This is the row number-------->"+(st));
         
       tableModel.deleteMessage(table.getSelectedRow());
         try{
             SwingWorker<Boolean, Void> backgroundProcess = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
            try{
            
            Connection con=DriverManager.getConnection(createDB.JDBC_URL); 
            Statement stmt = con.createStatement();
            stmt.execute("Delete from emailstoremailclient where id="+st);
            String str="UPDATE emailstoremailclient SET id=id-1 WHERE id>"+st;
            stmt.executeUpdate(str);
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(""+e);
        }
            return true;
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
         //   GlobalFlagforconnect2=1;
            System.out.println("db delete Done-----------");
            // or show result dialog, messageBox, etc      
        }
    };
             backgroundProcess.execute();
            }
            catch(Exception e)
            {
                System.out.println(" Some problem in thread");
            }
        
        // Update GUI.
        messageTextArea.setText("");
        deleting = false;
        selectedMessage = null;
        updateButtons();
        //totalemails--;
        //emailsPanel.setBorder(
        //BorderFactory.createTitledBorder("Inbox("+totalemails+") [Unread "+jf+"]"));
        
        // yaha pe db update + delete that id
    }
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sentPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout sentPanelLayout = new javax.swing.GroupLayout(sentPanel);
        sentPanel.setLayout(sentPanelLayout);
        sentPanelLayout.setHorizontalGroup(
            sentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        sentPanelLayout.setVerticalGroup(
            sentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        jLabel1.setText("Sent Mails :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(sentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(sentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(sentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(sentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            //    new sentFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel sentPanel;
    // End of variables declaration//GEN-END:variables
}
