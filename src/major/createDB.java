/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 *
 * @author hitesh
 */
public class createDB {
  //  public static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DRIVER="org.apache.derby.jdbc.ClientDriver";
  //  public static final String JDBC_URL = "jdbc:derby:emaildb;create=true";
   public static final String JDBC_URL = "jdbc:derby://localhost:1527/emailclientdb";
    public void makeDB() throws Exception
    {
         Class.forName(DRIVER);
        
        //  Class.forName("org.apache.derby.jdbc.ClientDriver");  
           // Connection con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/emailclient","email","email");
        Connection con = DriverManager.getConnection(JDBC_URL);
        DatabaseMetaData dbm = con.getMetaData();
    ResultSet rs = dbm.getTables(null, null, "userdatamailclient".toUpperCase(), null);
    if (rs.next()) {
      System.out.println("Table exists"); 
    } else {
      System.out.println("Table does not exist"); 
    
        con.createStatement().execute("create table userdatamailclient(emailid varchar(55),pass varchar(55),PRIMARY KEY (emailid))");
        con.createStatement().execute("create table emailstoremailclient(id int primary key,fr varchar(1000),subject varchar(1000),dt varchar(30),fname varchar(40))");
    }   
        
        System.out.println("DB made");
        con.close();
    }
    public static void main(String args[]) throws Exception
    {
        
       new createDB().makeDB();
    }
}
