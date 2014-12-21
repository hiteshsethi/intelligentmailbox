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
import java.util.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.table.*;

// This class manages the e-mail table's data.
public class MessagesTableModel extends AbstractTableModel {
    
    // These are the names for the table's columns.
    private  final String[] columnNames = {"Sender",
    "Subject", "Date"};
    static int flag=0;
    // The table's list of messages.
    private ArrayList messageList = new ArrayList();
    
    // Sets the table's list of messages.
    public void setMessages(Message[] messages) {
        // yaha pe database se value lete hue nai arraylist bnani ha
        
         try{
            Class.forName("oracle.jdbc.driver.OracleDriver");  
            Connection con=DriverManager.getConnection(  "jdbc:oracle:thin:@hitesh-PC:1521:xe","system","hitesh"); 
            Statement stmt = con.createStatement();
            ResultSet rs1=stmt.executeQuery("Select count(*) from emailstoremailclient");
            int ch=0;
            while(rs1.next())
            {
                ch=rs1.getInt(1);
            }
            if(ch==messages.length)
            {
                flag=1;
            }
        for (int i = messages.length - 1; i >= 0; i--) {
            int p=i+1;String fname=null;
            ResultSet rs=stmt.executeQuery("Select fname from emailstoremailclient where id="+p);
            
            
            while(rs.next())
        {
            fname=rs.getString("fname");
           
          //  System.out.println("hjh hjhjh"+username+password);
        }
          //  System.out.println("hjh hjhjh "+ch);
            if(ch>=p)
            {
        if(fname.equals("INBOX"))
        {
            
            messageList.add(messages[i]);
            System.out.println(" this message num is "+messages[i].getMessageNumber());
        }
            }
            else
            {
                 messageList.add(messages[i]);
            System.out.println(" that message num is "+messages[i].getMessageNumber());
            }
        }
        flag=1;
        con.close();
         }
         catch(Exception e)
         {
             System.out.println(e);
         }
        
        // Fire table data change notification to table.
        fireTableDataChanged();
    }
    
    
    public void setMessagesForClassifiers(ArrayList messages) {
        // yaha pe database se value lete hue nai arraylist bnani ha
        messageList = messages;
        // Fire table data change notification to table.
        fireTableDataChanged();
    }
    
    
    
    
    // Get a message for the specified row.
    public Message getMessage(int row) {
        return (Message) messageList.get(row);
    }
    
    // Remove a message from the list.
    public void deleteMessage(int row) {
        messageList.remove(row);
        
        // Fire table row deletion notification to table.
        fireTableRowsDeleted(row, row);
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
}