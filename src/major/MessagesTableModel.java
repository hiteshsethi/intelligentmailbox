/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

/**
 *
 * @author hitesh
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.swing.*;
import javax.swing.table.*;

// This class manages the e-mail table's data.
public class MessagesTableModel extends AbstractTableModel {
    
    // These are the names for the table's columns.
    private  final String[] columnNames = {"Sender",
    "Subject", "Date"};
    static int flag=0;
    File file = new File("classes.txt");
              
       
    // The table's list of messages.
    private ArrayList messageList = new ArrayList();
    
    // Sets the table's list of messages.
    public void setMessages(Message[] messages) throws SQLException {
        // yaha pe database se value lete hue nai arraylist bnani ha
               if (file.exists()) {
                  flag=1;
              }
        try (Connection con = DriverManager.getConnection(createDB.JDBC_URL)) {
            Statement stmt = con.createStatement();
              int lastid=0;
            if(flag==1){
        
            ResultSet rs1 = con.createStatement().executeQuery("select MAX(id) from emailstoremailclient");
            if(rs1.next())
            {
              lastid = rs1.getInt(1);
            //  System.out.println(lastid + " ha ahha ahaha ha ");
            }
            }
        for (int i = messages.length - 1; i >= 0; i--) {
            int p=i+1;String fname=null;
        
          //  System.out.println("hjh hjhjh "+ch);
            if(flag==1 && p<=lastid){  //yaha pe algo lgegi possibly
                  try{
          
            ResultSet rs=stmt.executeQuery("Select fname from emailstoremailclient where id="+p);
            
            while(rs.next())
        {
            fname=rs.getString("fname");
           
         //   System.out.println("hjh hjhjh"+fname+" "+p);
        }
            }
           catch(SQLException e)
         {
             System.out.println("------------"+e);
         }
        if(fname.equals("INBOX"))
        {
            
            messageList.add(messages[i]);
            fireTableDataChanged();
            System.out.println(" this message num is "+messages[i].getMessageNumber());
        }
            }
            else
            {
                String fnamebyclass=null;
                File file=new File("classes.txt");
               if(file.exists())// if(mailClassifier.TrainDone==1)
                {
                    try {                   
                        fnamebyclass=mailClassifier.testMessage(front.html2text(messages[i].getSubject()+" "+front.getMessageContent(messages[i])));
                    } catch (Exception ex) {
                        //Logger.getLogger(MessagesTableModel.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Problem in calling testMessage() :"+ex);
                    }
                     Map<String, Integer> myMap = new HashMap<String, Integer>();
            
                    try{
                        BufferedReader reader = new BufferedReader(new FileReader("classes.txt"));
                        String line = null;
                        int c=0;
                        while ((line = reader.readLine()) != null) {
                                line=line.toUpperCase();
                                myMap.put(line,c);
                                c++;
                
                            } reader.close();
                        }
                    catch(Exception ep)
                    {
                
                    }
                    
                    int classnum;
                    if(fnamebyclass.equals("INBOX"))
                    {
                        try {
                            messages[i].setFlag(Flags.Flag.SEEN, false);
                        } catch (MessagingException ex) {
                            //Logger.getLogger(MessagesTableModel.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Problem in marking flag seen"+ex);
                        }
                        messageList.add(messages[i]);
                    fireTableDataChanged();
                    }
                    else{
                    try {
                        classnum=myMap.get(fnamebyclass);
                        messages[i].setFlag(Flags.Flag.SEEN, false);
                         front.othertableModel[classnum].setFirstPlaceMessage(messages[i]);
                        front.othersplitPane[classnum].setDividerLocation(.5);
                    } catch (Exception ex) {
                        System.out.println("----------------------"+ex);
                       // Logger.getLogger(MessagesTableModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    try{
                        Connection con1=DriverManager.getConnection(createDB.JDBC_URL);
                        Statement stmt1=con1.createStatement(); 
                        String str=null;
                        Address[] senders =messages[i].getFrom();
                        if (senders != null || senders.length > 0) 
                        {
                            str=senders[0].toString();
                        }
                         String sub=messages[i].getSubject();
                    if(sub==null)
                        sub="No subject";
                    sub=sub.replace("'","");//coz problem aa re the.... "'" iski vajah se
                    sub=sub.replace("%","");
                    str=str.replace("'","");
                    sub=sub.replace("^","");
                    sub=sub.replace(",","");
                    sub=sub.replace("&","");
                    sub=sub.replace("*","");
                    int dbuid=messages[i].getMessageNumber();
                    
                    stmt1.executeUpdate("insert into emailstoremailclient values"
                        + "("+dbuid+",'"+str+"','"+sub+
                        "','"+messages[i].getSentDate().toString()
                        +"','"+fnamebyclass+"')");  
                    stmt1.close();       
                    con1.close();      
             }
                catch(SQLException | MessagingException e)
                {
                    System.out.println("----------------------"+e);
                //    Logger.getLogger(mailClassifier.class.getName()).log(Level.SEVERE, null, e);
                }
      
                   
                }
                else
                {
                
                 messageList.add(messages[i]);
                  fireTableDataChanged();
                }
     //       System.out.println(" that message num is "+messages[i].getMessageNumber());
            }
        }
        flag=1;
        }
       
         
        
        // Fire table data change notification to table.
        fireTableDataChanged();
    }
 
    
    public void setMessagesForClassifiers(ArrayList messages) {
        if(messages!=null){
        // yaha pe database se value lete hue nai arraylist bnani ha
        Collections.reverse(messages);
        messageList = messages;
        // Fire table data change notification to table.
        fireTableDataChanged();
    }
    }
    
    public void setFirstPlaceMessage(Message m)
    {
        if(m!=null)
        {
            messageList.add(0, m);
                     fireTableDataChanged();
        }
    }
    
     public void setMessagesForSearch(ArrayList messages) {
         if(messages!=null){
        // yaha pe database se value lete hue nai arraylist bnani ha
        messageList = messages;
        // Fire table data change notification to table.
        fireTableDataChanged();}
    }
    
    // Get a message for the specified row.
    public Message getMessage(int row) {
        if(row>=0){
        return (Message) messageList.get(row);
        }
        else
            return null;
    }
    
    // Remove a message from the list.
    public void deleteMessage(int row) {
        if(row>=0){
        messageList.remove(row);
        
        // Fire table row deletion notification to table.
        fireTableRowsDeleted(row, row);}
    }
    
    // Get table's column count.
    public int getColumnCount() {
        return columnNames.length;
    }
    
    // Get a column's name.
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    // Get table's row count.
    public int getRowCount() {
        return messageList.size();
    }
    
    // Get value for a specific row and column combination.
    public Object getValueAt(int row, int col) {
        try {
            Message message = (Message) messageList.get(row);
            switch (col) {
                case 0: // Sender
                    Address[] senders = message.getFrom();
                    if (senders != null || senders.length > 0) {
                        return senders[0].toString();
                    } else {
                        return "[none]";
                    }
                case 1: // Subject
                    String subject = message.getSubject();
                    if (subject != null && subject.length() > 0) {
                        return subject;
                    } else {
                        return "[none]";
                    }
                case 2: // Date
                    Date date = message.getSentDate();
                    if (date != null) {
                        return date.toString();
                    } else {
                        return "[none]";
                    }
            }
        } catch (Exception e) {
            // Fail silently.
            return "";
        }
        
        return "";
    }

    void setEmpty(MessagesTableModel model)
    {
        int len=model.getRowCount();
        if(len>0)
        {
            messageList.clear();
            fireTableRowsDeleted(0,len-1);
        }
    }
}