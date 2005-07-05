package com.tonbeller.jpivot.mondrian;

import com.tonbeller.jpivot.olap.model.OlapException;

/**
 * provide settings for test environment
 */
public class TestConnection {
  public static String getConnectString() {
    String catalog = System.getProperty("catalog.uri");
    if (catalog == null)
      throw new RuntimeException(
          "missing system property \"catalog.uri\", e.g. -Dcatalog.uri=file:///c:/dev/mondrian/demo/FoodMart.xml");
    return getConnectString(catalog);
  }

  public static String getConnectString(String catUri) {
    String jdbcUrl = System.getProperty("jdbc.url", "jdbc:odbc:MondrianFoodMart");
    //String jdbcUrl = System.getProperty("jdbc.url",
    // "jdbc:mysql://localhost/foodmart");
    String jdbcUser = System.getProperty("jdbc.user", "");
    String jdbcPassword = System.getProperty("jdbc.password", "");

    StringBuffer con = new StringBuffer();

    con.append("provider=Mondrian;");
    con.append("Jdbc=");
    con.append(jdbcUrl);
    con.append(";Catalog=");
    con.append(catUri);

    if (jdbcUser.length() > 0)
      con.append(";JdbcUser=").append(jdbcUser);
    if (jdbcPassword.length() > 0)
      con.append(";JdbcPassword=").append(jdbcPassword);

    return con.toString();
  }

  public static String getJdbcDriver() {
    String jdbcDriver = System.getProperty("jdbc.driver", "sun.jdbc.odbc.JdbcOdbcDriver");
    return jdbcDriver;
  }

  public static void initModel(MondrianModel model) throws OlapException {
    String connectString = getConnectString();
    model.setConnectString(connectString);
    String jdbcDriver = getJdbcDriver();
    model.setJdbcDriver(jdbcDriver);
    model.initialize();
  }
}