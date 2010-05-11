package gbc.jdbc.jdbcclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCClient {
  static String user;
  static String password;
  static String url;
  static String scriptFilename;
  static String outputFilename;

  static Connection con = null;

  public static void main(String[] args) throws Exception {

    if (args.length != 5) {
      printUsage();
      System.exit(1);
    }

    user = args[0];
    password = args[1];
    url = args[2];
    scriptFilename = args[3];
    outputFilename = args[4];

    System.out.println("Getting connection with settings:");
    System.out.println("\t url: " + url);
    System.out.println("\t user: " + user);
    System.out.println("\t password: " + password);
    System.out.println("\t script: " + scriptFilename);
    System.out.println("\t output CSV: " + outputFilename);

    Connection con = getOracleJDBCConnection(url, user, password);
    if (con != null) {
      System.out.println("Got Connection.");
      DatabaseMetaData meta = con.getMetaData();
      System.out.println("Driver Name : " + meta.getDriverName());
      System.out.println("Driver Version : " + meta.getDriverVersion());

      con.setAutoCommit(false);
      con.setReadOnly(false);

      FileInputStream fstream = new FileInputStream(scriptFilename);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));

      String numOfColumns = null;
      String sqlLine = null;
      while ((sqlLine = br.readLine()) != null)   {
        System.out.println("SQL: " + sqlLine);

        if (sqlLine.startsWith("--")) {
          numOfColumns = sqlLine.substring(sqlLine.lastIndexOf(":")+1);
          System.out.println("number of columns: " + numOfColumns);
          continue;
        } else {
          PreparedStatement stmt = con.prepareStatement(sqlLine);
          if (stmt.execute()) {
            ResultSet resultSet = stmt.getResultSet();
            while (resultSet.next()) {
              int max = Integer.parseInt(numOfColumns);
              for (int i=1; i<=max; i++) {
                out.write("\"" + resultSet.getString(i) + "\"");
                if (i<max) {
                  out.write(",");
                } else {
                  out.newLine();
                }
              }
            }
          }
        }
      }
      in.close();
      out.close();
      System.out.println("EOF: " + scriptFilename);
      con.commit();
      System.out.println("Commit performed");
      System.out.println("Data written to: " + outputFilename);
    } else {
      System.out.println("Could not get Connection");
    }
  }

  public static void printUsage() {
    System.out.println("Usage: ");
    System.out.println("java -jar jdbctestclient-1.0.1.jar [user] [password] [url(jdbc:oracle:oci:@sid) or (jdbc:oracle:thin:@1.2.3.4:1521:sid)] [script_name] [outputfile]");
  }

  public static Connection getOracleJDBCConnection(String url, String user, String password) {
    try {
      Class.forName("oracle.jdbc.OracleDriver");
    } catch (java.lang.ClassNotFoundException e) {
      System.err.print("ClassNotFoundException: ");
      System.err.println(e.getMessage());
    }
    try {
      con = DriverManager.getConnection(url, user, password);
    } catch (SQLException ex) {
      System.err.println("SQLException: " + ex.getMessage());
    }

    return con;
  }
}
