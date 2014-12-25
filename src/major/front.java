/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
    File file;
    int flag=1;
    final JPanel emailsPanel = new JPanel();
     private JPanel[] otherPanel = new JPanel[50];
    int GlobalFlagforconnect2=0;
    int LastUid=0;
   // int flag=0;
    private MessagesTableModel tableModel;
    private MessagesTableModel[] othertableModel = new MessagesTableModel[50];
 //   private MessagesTableModel tableModel2;
    JPanel buttonPanel2;
    int jf;
    // Table listing messages.
    private JTable table;
    private JTable[] othertable = new JTable[50];
    JPopupMenu popupMenu ;

  //  table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // This the text area for displaying messages.
    private JTextArea messageTextArea;
    private JTextArea[] othermessageTextArea = new JTextArea[50];
     private ArrayList[] messageList = new ArrayList[50];
  /* This is the split panel that holds the messages
     table and the message view panel. */
    private JSplitPane splitPane;
    private JSplitPane[] othersplitPane = new JSplitPane[50];
    // These are the buttons for managing the selected message.
    private JButton replyButton, forwardButton, deleteButton;
    
    // Currently selected message in table.
    private Message selectedMessage;
    
    // Flag for whether or not a message is being deleted.
    private boolean deleting;
    
    // This is the JavaMail session.
    private Session session;
    private List<String> classlist = new ArrayList<String>();
    /**
     * Creates new form front
     */
    //private CardLayout cardlayout = new CardLayout();  
    String username;
    String password;
    int totalemails=0;
       //JPanel emailsPanel = new JPanel();
    public front() {
        initComponents();
        
   //     setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("C:\\Users\\hitesh\\Documents\\NetBeansProjects\\major\\major images\\reload.png")));
        this.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
   //     java.net.URL url = ClassLoader.getSystemResource("com/xyz/resources/camera.png");
              
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
        table.setRowHeight(table.getRowHeight() + 13);
        table.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        // Allow only one row at a time to be selected.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Setup E-mails panel.
       addPrimary();
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
        tabbedPane.setSize(1160, 520);
        emailsPanel.setSize(1160, 520);
    //    emailsPanel.setExtendedState(javax.swing.JPanel.MAXIMIZED_BOTH);
     //   pack();
      // emailsPanel.add(buttonPanel2)
        getContentPane().setLayout(new BorderLayout());
          getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
      buttonPanel2.setVisible(true);  
      emailsPanel.setVisible(false);
      
      //classifier name reading from classes.txt and putting it into list
      
      
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
           // yaha pe connect dubara call hone chaiye exact ho to connect2() hona chiye....along with sarre users ke mail show hone chiaaye
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
    
 //   int tabCounter=classlist.size()-1; //-1 coz inbox bhi added ha classlist mn
    //isko purane tabs se start krna pdega
  
    
     public void add(String str,int tabCounter) {
       //  tabCounter=classlist.size()-1; 
         othertableModel[tabCounter] = new MessagesTableModel();
      
         othertable[tabCounter] = new JTable(othertableModel[tabCounter]);
      //   tabletocountermap.put(othertable[tabCounter], tabCounter);
         othertable[tabCounter].setRowHeight(othertable[tabCounter].getRowHeight() + 13);
        
        // Allow only one row at a time to be selected.
        othertable[tabCounter].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         str=str.toUpperCase();
         otherPanel[tabCounter]= new JPanel();
    //final JPanel content = new JPanel();
    JPanel tab = new JPanel();
    tab.setOpaque(false);
    
    JLabel tabLabel = new JLabel(str);
tabLabel.setPreferredSize(new Dimension(130, 20));
    tab.add(tabLabel, BorderLayout.WEST);
 //   tab.add(tabCloseButton, BorderLayout.EAST);
  othermessageTextArea[tabCounter] = new JTextArea();
        othermessageTextArea[tabCounter].setEditable(false);
        othersplitPane[tabCounter] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(othertable[tabCounter]), new JScrollPane(othermessageTextArea[tabCounter]));
        otherPanel[tabCounter].setLayout(new BorderLayout());
   
        
        otherPanel[tabCounter].add(othersplitPane[tabCounter], BorderLayout.CENTER);
    tabbedPane.addTab(null, otherPanel[tabCounter]);
    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tab);
    addMessageOtherTables(tabCounter,messageList[tabCounter]);
    System.out.println(" -------------------------------------"+tabCounter);
  //      ++tabCounter;
    addingSelectionListners(tabCounter);
      addRightClickMenuOnTable();
   othersplitPane[tabCounter].setDividerLocation(.5);
  }
     void addingSelectionListners(int cou)
     {
         
             final int p=cou;
             othertable[p].getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                othertableSelectionChanged(p);
            }
        });
         
     }
  void addMessageOtherTables(int ind,ArrayList mes)
  {
    //  Message []m=messages;
     /* 
      try{
            Class.forName("oracle.jdbc.driver.OracleDriver");  
            Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh"); 
            Statement stmt = con.createStatement();
            stmt.executeQuery("Delete from emailstoremailclient where id=");
            String str="UPDATE emailstoremailclient SET id=id-1 WHERE id>";
            stmt.executeUpdate(str);
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(""+e);
        }
     */
      // yaha pe mssages mn kuch ha nai na abhi
      othertableModel[ind].setMessagesForClassifiers(mes);
  }
 public void addPrimary() {
    JPanel tab = new JPanel();
    tab.setOpaque(false);
    JLabel tabLabel = new JLabel("  PRIMARY",new javax.swing.ImageIcon(getClass().getResource("/major/major images/file8.png")),2);
    tabLabel.setPreferredSize(new Dimension(130, 20));
   // tabCounter++;
    //JButton tabCloseButton = new JButton(closeXIcon);
//    tabCloseButton.setPreferredSize(closeButtonSize);
  /* tabCloseButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        int closeTabNumber = tabbedPane.indexOfComponent(content);
        tabbedPane.removeTabAt(closeTabNumber);
      }
    });*/

    tab.add(tabLabel, BorderLayout.WEST);
 //   tab.add(tabCloseButton, BorderLayout.EAST);

    tabbedPane.addTab(null, emailsPanel);
    
    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tab);
     
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
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        addTabButton = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        resetbutton = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Intelligent Mail Box");
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.white);

        jButton1.setBackground(new java.awt.Color(209, 72, 54));
        jButton1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/1415554495_Black_Email.png"))); // NOI18N
        jButton1.setText("COMPOSE");
        jButton1.setIconTextGap(13);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/1415554581_Black_DownRight-Arrow.png"))); // NOI18N
        jButton2.setText("Inbox");
        jButton2.setIconTextGap(10);
        jButton2.setInheritsPopupMenu(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/1415554665_Black_ThumbsUp.png"))); // NOI18N
        jButton3.setText("Sent");
        jButton3.setFocusable(false);
        jButton3.setIconTextGap(10);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/1415554520_Black_Star.png"))); // NOI18N
        jButton4.setText("Important");
        jButton4.setIconTextGap(10);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(153, 153, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/1415554478_Black_Search.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/icon.png"))); // NOI18N

        addTabButton.setText("Add Category");
        addTabButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabButtonActionPerformed(evt);
            }
        });

        jButton6.setText("Manage Tabs");

        jButton7.setText("Add Label");

        resetbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/major/major images/reload.png"))); // NOI18N
        resetbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetbuttonActionPerformed(evt);
            }
        });

        jButton9.setText("drop");

        jButton10.setText("More");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addTabButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tabbedPane)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 842, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 143, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(resetbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(resetbutton, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(12, 12, 12)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addTabButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                    
    SwingWorker<Boolean, Void> refreshProcess = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
           // execute1();
            try{
                System.out.println("yaha aa gya mn");
                connect2();
                
            }
            catch(Exception e)
            {
                System.out.println("thread mn panga ha----------------------------------");
              //  return false;
                
            }
            return true;
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
            GlobalFlagforconnect2=1;
            System.out.println("Refresh Done-----------");
            try {
           boolean g = get();
         } catch (InterruptedException ex) {
           ex.printStackTrace();
         } catch (ExecutionException ex) {
           ex.printStackTrace();
         }
            // or show result dialog, messageBox, etc      
        }
    };
        refreshProcess.execute();
        
           //   connect2();
          //     updateInbox.execute();
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

    private void addTabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabButtonActionPerformed
        // TODO add your handling code here:
        String s="";
        while(s.compareTo("")==0)
        {
            try{
            s = (String)JOptionPane.showInputDialog( "Enter Classifier Name :","");
            if(s!=null){
            Pattern p= Pattern.compile("[^a-zA-Z ]");
            Matcher m = p.matcher(s);
            if(s.compareTo("")==0  || m.find() || s.length()>30)
            {
                JOptionPane.showMessageDialog( tabbedPane,"Please enter correct word...", "Error", JOptionPane.ERROR_MESSAGE);
                s="";
            }
            }
            else
            {
                break;
            }
            }
            catch(Exception e)
            {
                System.out.println("Error Ocurred in Regex of classifier input  --> "+e);
            }
        }
        if(s!=null){
            int i=classlist.size()-1;
            if(i<0)
            {
                i=0;
            }
            classlist.add(s);
          messageList[i] = new ArrayList();    
        add(s,i);
      //  System.out.println()
         
      
        //yaha pe vo popupmenu ko update vala function fir se call hoga
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("classes.txt", true)));
            out.println(s);
            out.close();
            } catch (IOException e) {
            //exception handling left as an exercise for the reader
            }   
            
        }
    }//GEN-LAST:event_addTabButtonActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void resetbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetbuttonActionPerformed
        // TODO add your handling code here:
        
    SwingWorker<Boolean, Void> refreshProcess = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
           // execute1();
            try{
                System.out.println("yaha aa gya mn");
                connect2();
                
            }
            catch(Exception e)
            {
                System.out.println("thread mn panga ha----------------------------------");
              //  return false;
                
            }
            return true;
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
            GlobalFlagforconnect2=1;
            System.out.println("Refresh Done-----------");
            try {
           boolean g = get();
         } catch (InterruptedException ex) {
           ex.printStackTrace();
         } catch (ExecutionException ex) {
           ex.printStackTrace();
         }
            // or show result dialog, messageBox, etc      
        }
    };
        refreshProcess.execute();
        
       // connect2();
    }//GEN-LAST:event_resetbuttonActionPerformed

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
     private void othertableSelectionChanged(int i) {
    /* If not in the middle of deleting a message, set
       the selected message and display it. */
        if (!deleting) {
            selectedMessage =
                    othertableModel[i].getMessage(othertable[i].getSelectedRow());
            othershowSelectedMessage(i);
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
     //   final int store=totalemails-table.getSelectedRow();
        Message temp=tableModel.getMessage(table.getSelectedRow());
        final int st=temp.getMessageNumber();
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
            Class.forName("oracle.jdbc.driver.OracleDriver");  
            Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh"); 
            Statement stmt = con.createStatement();
            stmt.executeQuery("Delete from emailstoremailclient where id="+st);
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
        totalemails--;
        emailsPanel.setBorder(
        BorderFactory.createTitledBorder("Inbox("+totalemails+") [Unread "+jf+"]"));
        // yaha pe db update + delete that id
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
     // Show the selected message in the content panel.
    private void othershowSelectedMessage(int i) {
        // Show hour glass cursor while message is loaded.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            othermessageTextArea[i].setText(
                    getMessageContent(selectedMessage));
            othermessageTextArea[i].setCaretPosition(0);
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
    String str;
      // Connect to e-mail server.
    public void setUserPass()
    {
        try{
            GlobalFlagforconnect2=0;
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
        addRightClickMenuOnTable();
    }
    public void addRightClickMenuOnTable()
    {
        popupMenu= new JPopupMenu();
        //added right click menu on table rows
          JMenu menuItemFurther = new JMenu("Move to tab");
        JMenuItem menuItemRead = new JMenuItem("Mark as Read");
        JMenuItem menuItemArchive = new JMenuItem("Archive");
        JMenuItem menuItemDelete = new JMenuItem("Delete");
        for(int iter=0;iter<classlist.size();iter++)
        {
            JMenuItem menuitem = new JMenuItem(classlist.get(iter));
            menuItemFurther.add(menuitem);
            if(iter!=classlist.size()-1){
            menuItemFurther.addSeparator();}
            final String temp = classlist.get(iter);
             menuitem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! yaha pe store ko cal krne ka method change krna pdega
             * 
             * 
             * 
             * 
             * 
             * 
             */
                
             //   final int store=totalemails-table.getSelectedRow();
                 Message tem=tableModel.getMessage(table.getSelectedRow());
        final int st=tem.getMessageNumber();
       
                //yaha pe getselectedrow nai work krega thang se.......sokuch ot krna pdega
               String qry="UPDATE emailstoremailclient SET fname="+"'"+temp.toUpperCase()+"'"+" WHERE id="+st;
                  try{
        Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
         Statement stmt=con.createStatement();  
         stmt.executeQuery(qry);
         System.out.println("ppppppppp======="+st);
    //     tableModel.deleteMessage(table.getSelectedRow());
           System.out.println("llllllllllllllllll======="+table.getSelectedRow());
         con.close();
                  }
                  catch(Exception ex)
                  {
                      System.out.println(" there is some error is moving to other tabs "+ex);
                  }
                 
              // JOptionPane.showMessageDialog(tabbedPane,"Right-click performed on table and choose this ->"+temp);
            }
        });
        }
        popupMenu.add(menuItemFurther);
        popupMenu.addSeparator();
        popupMenu.add(menuItemRead);
        popupMenu.addSeparator();
        popupMenu.add(menuItemArchive);
        popupMenu.addSeparator();
        popupMenu.add(menuItemDelete);
        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new TableMouseListener(table));
        //aise functions call honge
         menuItemDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actionDelete();
              //totalmessage--;
                
               // JOptionPane.showMessageDialog(tabbedPane,"Right-click performed on table and choose this");
            }
        });
        
         
         
    }
    public void connect2() {
        
        // Establish JavaMail session and connect to server.
        Store store = null;
        try {
            // Initialize JavaMail session with SMTP server.
            Properties props = new Properties();
            props.setProperty("mail.store.protocol","imaps");
            session = Session.getInstance(props, null);
            store = session.getStore("imaps");
              //        JOptionPane.showMessageDialog(null,""+username+" "+password,"Mail sent",JOptionPane.PLAIN_MESSAGE);
            store.connect("imap.gmail.com", username, password);
            
        } catch (Exception e) {
            // Show error dialog.
            showError("Check your internet connection. Or invalid credentials or check your account settings. Not able to connect", true);
        }
   //     int p=0;
       
        String st;
         st=username.replace(".", "");
          st=st.replace(".","");
           st=st.replace("@","");
            st=st.replace("_","");
          /* try{
            BufferedReader reader = new BufferedReader(new FileReader("uid"+st+".txt"));
String line = null;
while ((line = reader.readLine()) != null) {
    int i=Integer.parseInt(line);
    System.out.println("this is "+i);
    p=i;
}
            reader.close();
           }
           catch(Exception e)
           {
               
           }
       //        reader.close();
        */
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
         //   UIDFolder f;
        //    profile.add(UIDFolder.FetchProfileItem.UID);
            folder.fetch(messages, profile);
           
            
             //recent=folder.getUID();
        //     getMessageUID(session,folder,message[0]);
            try{
             SwingWorker<Boolean, Void> backgroundProcess = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
            execute1();
            return true;
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
         //   GlobalFlagforconnect2=1;
            System.out.println("db Entry Done-----------");
            // or show result dialog, messageBox, etc      
        }
    };
             backgroundProcess.execute();
            }
            catch(Exception e)
            {
                System.out.println(" Some problem in thread");
            }
           // execute1();
            // Put messages in table.
             
            tableModel.setMessages(messages);
            if(flag==1){
            addPreClassifier();   
            flag=0;
            }
            
        } catch (Exception e) {
            System.out.println("Unable to download messages."+e);
        }
          str=username.replace(".", "");
          str=str.replace(".","");
           str=str.replace("@","");
            str=str.replace("_","");
            // str=str.replace("","");
          /* file = new File("uid"+str+".txt");
              
              if (!file.exists()) {
            try {
                file.createNewFile();
                   FileWriter fw = null;
        
         fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String s="0";
            bw.write(s);
            bw.close();
            fw.close();
            } catch (IOException ex) {
                Logger.getLogger(front.class.getName()).log(Level.SEVERE, null, ex);
            } 
                            }
                            */
    }
    // simply puts all mails into  the database
    void addPreClassifier()
    {
        
            Map<String, Integer> myMap = new HashMap<String, Integer>();
            try{
           BufferedReader reader = new BufferedReader(new FileReader("classes.txt"));
            String line = null;
            int c=0;
            while ((line = reader.readLine()) != null) {
                line=line.toUpperCase();
                    classlist.add(line);
                    myMap.put(line,c);
                       messageList[c++] = new ArrayList();
                  //  add(line);
                    // yaha be automaticalyy tab creation ho rhe ha
                    
                  //  System.out.println(line);
            }
            reader.close();
      classlist.add("inbox".toUpperCase());
      }
      catch(IOException e)
      {
          
      }    
        
           try{
        Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
         Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select id,fname from EMAILSTOREMAILCLIENT");  
       
       
        while(rs.next())
        {
            int id=rs.getInt("id");
            String fname=rs.getString("fname");
         if(!fname.equals("INBOX")){
            int classnum=myMap.get(fname);
             System.out.println(id+" ---  "+fname+" ---  "+classnum);
            messageList[classnum].add(messages[id-1]);
         }
        }
        con.close();
           }
           catch(Exception e)
           {
               System.out.println("errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr  "+e);
           }
          
      
            
            
       try{
           BufferedReader reader = new BufferedReader(new FileReader("classes.txt"));
            String line = null;
            int c=0;
            while ((line = reader.readLine()) != null) {
                 //   classlist.add(line);
                    add(line,c++);
                    // yaha be automaticalyy tab creation ho rhe ha
                    
                  //  System.out.println(line);
            }
            reader.close();
     // classlist.add("inbox".toUpperCase());
      }
      catch(IOException e)
      {
          
      } 
            
         //   for(int i)
            
            
            
            
            
            
            
            
        
    }
    void execute1()
    {int count=0;
        
         try{
                    Class.forName("oracle.jdbc.driver.OracleDriver");  
//create  the connection object  
        Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh");  
          
       //  String org=passwordField.getPassword();
       // stmt.executeQuery("insert into emailstoremailclient values('"+s+"','"+sub+"','"+d+"')");  
           
               
        /*    BufferedReader reader = new BufferedReader(new FileReader("uid"+str+".txt"));
String line = null;
while ((line = reader.readLine()) != null) {
    int i=Integer.parseInt(line);
   // System.out.println("this is "+i);
    count+=i;
}
            reader.close();
            int start=count;
            System.out.println(start+" yaha haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            //yaha start to start+mess.len tha
          */
           for(int i=0;i<messages.length;i++)
            {count++;
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
                    int dbuid=folder.getMessage(i+1).getMessageNumber();
                stmt.executeQuery("insert into emailstoremailclient values"
                        + "("+dbuid+",'"+str+"','"+sub+
                        "','"+folder.getMessage(i+1).getSentDate().toString()
                        +"','INBOX')");  
         /*       String cont= getMessageContent(folder.getMessage(i+1));
                File file = new File("emails\\"+i+".txt");
                 FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(cont);
                    bw.close();*/
           stmt.close();
            }
            // rs.close(); 
        
        con.close();
      /*   int recent=count;
        FileWriter fw = null;
        
         fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String s=recent+"";
            bw.write(s);
            bw.close();
            fw.close();*/
             }
                catch(Exception e)
                {
                    System.out.println(e);
                }
      
    }
    
   private SwingWorker<Boolean, Void> updateInbox = new SwingWorker<Boolean, Void>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // paste the MySQL code stuff here
        while(true){    try {

  Thread.sleep(60000L);    // one second
            }
catch (Exception e) {
     return true;
    
}
        connect2();}
   //      return true;
           
        }

        @Override
        protected void done() {
            // Process ended, mark some ended flag here
            System.out.println("dkh thread ended");
            // or show result dialog, messageBox, etc      
        }
    };
    
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTabButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton resetbutton;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}

//sarre user defined categories kisi file mn store ho....and hmesha vaha se read krke right click menu update ho