package gbc.jdbc.jdbcclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCClient {
  
  private String url;
  private String username;
  private String password;
  
  private String scriptFilename;
  private String outputFilename;
  
  
  public void execute() throws SQLException,
      FileNotFoundException, IOException {
    Connection con = getOracleJDBCConnection(url, username, password);
    
    if (con == null) {
      System.out.println("Could not get Connection");
      return;
    } 
    
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

    String sqlLine = null;
    while ((sqlLine = br.readLine()) != null)   {
      System.out.println("SQL: " + sqlLine);
      if (sqlLine.startsWith("--")) {
        continue;
      }
      
      sqlLine = stripSemiColon(sqlLine);
      
      PreparedStatement stmt = con.prepareStatement(sqlLine);
      if (stmt.execute()) {
        ResultSet resultSet = stmt.getResultSet();
        while (resultSet.next()) {
          int columns = getNumOfColumns(sqlLine);
          for (int i=1; i<=columns; i++) {
            out.write("\"" + resultSet.getString(i) + "\"");
            if (i<columns) {
              out.write(",");
            } else {
              out.newLine();
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
  }

  private static String stripSemiColon(final String sqlLine) {
    String stripped = sqlLine;
    if (sqlLine.endsWith(";")) {
      stripped = sqlLine.replaceAll(";", "");
    }
    return stripped;
  }
  
  protected static int getNumOfColumns(final String sqlLine) {
    if (sqlLine == null || sqlLine.trim().isEmpty()) {
      return 0;
    }

    String lowerCaseStatement = sqlLine.toLowerCase().trim();
    String columnExtract = null;
    try {
      columnExtract = sqlLine.substring(lowerCaseStatement.indexOf("select") + "select".length() + 1,  lowerCaseStatement.indexOf("from"));
    } catch (StringIndexOutOfBoundsException e) {
      return 0;
    }

    if (columnExtract.isEmpty()) {
      return 0;
    }

    int counter = 0;
    for (int i=0; i<columnExtract.length(); i++) {
      if (columnExtract.charAt(i) == ',') {
        counter++;
      }
    }
    return ++counter;
  }
  
  private Connection getOracleJDBCConnection(String url, String user, String password) {
    Connection con = null;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getScriptFilename() {
    return scriptFilename;
  }

  public void setScriptFilename(String scriptFilename) {
    this.scriptFilename = scriptFilename;
  }

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

}
