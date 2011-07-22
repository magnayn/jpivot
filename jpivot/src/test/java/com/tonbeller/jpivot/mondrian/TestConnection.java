package com.tonbeller.jpivot.mondrian;

import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.tbutils.res.Resources;

/**
 * provide settings for test environment
 */
public class TestConnection {
  static Resources res = Resources.instance();

  public static String getConnectString() {
    String catalog = res.getString("catalog.uri");
    if (catalog == null)
      throw new RuntimeException(
          "missing system property \"catalog.uri\", e.g. -Dcatalog.uri=file:///c:/dev/mondrian/demo/FoodMart.xml");
    return getConnectString(catalog);
  }

  public static String getConnectString(String catUri) {
    String jdbcUrl = res.getString("jdbc.url");
    String jdbcUser = res.getString("jdbc.user");
    String jdbcPassword = res.getString("jdbc.password");

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
    return res.getString("jdbc.driver");
  }

  public static void initModel(MondrianModel model) throws OlapException {
    String connectString = getConnectString();
    model.setConnectString(connectString);
    String jdbcDriver = getJdbcDriver();
    model.setJdbcDriver(jdbcDriver);
    model.initialize();
  }
}