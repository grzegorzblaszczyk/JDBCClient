package gbc.jdbc.jdbcclient;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class JDBCClientTest {

  @Before
  public void setUp() {
  }

  @Test
  public void shouldReturnNumOfColumns() {
    assertEquals(0, JDBCClient.getNumOfColumns(""));
    assertEquals(0, JDBCClient.getNumOfColumns(" "));
    assertEquals(0, JDBCClient.getNumOfColumns("SELECT "));
    assertEquals(0, JDBCClient.getNumOfColumns("SELECT FROM"));
    assertEquals(0, JDBCClient.getNumOfColumns("SELECT FROM table"));

    assertEquals(1, JDBCClient.getNumOfColumns("SELECT col FROM table"));
    assertEquals(2, JDBCClient.getNumOfColumns("SELECT w.product_id product_id, p.image1_path image1_path FROM table1 w, table2 p WHERE w.product_id=p.product_id"));
    assertEquals(7, JDBCClient.getNumOfColumns("SELECT w.col1 , w.col2, w.col3, w.col4, w.col5, w.col6, p.col1 p_col1 FROM table1 w, table2 p WHERE w.product_id=p.product_id"));
    assertEquals(7, JDBCClient.getNumOfColumns("SELECT w.col1 , w.col2, w.col3, w.col4, w.col5, w.col6, p.col1 p_col1 FROM table1 w, table2 p"));
  }

}
