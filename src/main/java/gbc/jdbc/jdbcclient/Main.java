package gbc.jdbc.jdbcclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
  
  static String jarName;
  static String jarVersion;

  public static void main(String[] args) throws Exception {
    
    readProperties();

    if (args.length != 5) {
      printUsage();
      System.exit(1);
    }

    String url = args[2];
    String user = args[0];
    String password = args[1];
    String scriptFilename = args[3];
    String outputFilename = args[4];
    
    System.out.println("Getting connection with settings:");
    System.out.println("\t url: " + url);
    System.out.println("\t user: " + user);
    System.out.println("\t password: " + password);
    System.out.println("\t script: " + scriptFilename);
    System.out.println("\t output CSV: " + outputFilename);

    JDBCClient jdbcClient = new JDBCClient();
    jdbcClient.setUrl(url);
    jdbcClient.setUsername(user);
    jdbcClient.setPassword(password);
    jdbcClient.setScriptFilename(scriptFilename);
    jdbcClient.setOutputFilename(outputFilename);
    
    jdbcClient.execute();
  }

  private static void readProperties() throws IOException {
    InputStream in = Main.class.getClass().getResourceAsStream("/environment.properties");
    Properties props = new Properties(System.getProperties());
    props.load(in);
    jarName = props.getProperty("jar.name");
    jarVersion = props.getProperty("jar.version");
  }

  public static void printUsage() throws IOException {
    System.out.println("Usage: ");
    System.out.println("java -jar " + jarName + "-" + jarVersion + ".jar [user] [password] [url(jdbc:oracle:oci:@sid) or (jdbc:oracle:thin:@1.2.3.4:1521:sid)] [script_name] [outputfile]");
  }
}
