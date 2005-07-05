/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.mondrian;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.tonbeller.jpivot.core.ModelFactory;

/**
 * creates a MondrianModel from config.xml
 * @author av
 */
public class MondrianModelFactory {
  private static Logger logger = Logger.getLogger(MondrianModelFactory.class);

  private MondrianModelFactory() {
  }

  static String makeConnectString(Config cfg) {

    // for an external datasource, we do not need JdbcUrl *and* data source 
    /*
    if ((cfg.getJdbcUrl() == null) == (cfg.getDataSource() == null))
      throw new IllegalArgumentException("exactly one of jdbcUrl or dataSource must be specified");
    */
    // provider=Mondrian;Jdbc=jdbc:odbc:MondrianFoodMart;Catalog=file:///c:/dev/mondrian/demo/FoodMart.xml
    StringBuffer sb = new StringBuffer("provider=Mondrian");
    if (cfg.getJdbcUrl() != null) {
      String jdbcUrl = cfg.getJdbcUrl();
      sb.append(";Jdbc=");
      // if the url contains a semicolon, it must be in quotes
      if (jdbcUrl.indexOf(';') >= 0) {
        char c = jdbcUrl.charAt(0);
        if (c != '"' && c != '\'') {
          char escape = '"';
          if (jdbcUrl.indexOf('"') >= 0) {
            if (jdbcUrl.indexOf('\'') >= 0) {
              // this is not valid
              throw new IllegalArgumentException(
                  "jdbcUrl is not valid - contains single and double quotes");
            }
            escape = '\'';
          }
          sb.append(escape);
          sb.append(jdbcUrl);
          sb.append(escape);
        } else
          sb.append(jdbcUrl); // already quoted
      } else
        sb.append(jdbcUrl); // no quotes neccessary

      if (cfg.getJdbcUser() != null)
        sb.append(";JdbcUser=").append(cfg.getJdbcUser());
      if (cfg.getJdbcPassword() != null && cfg.getJdbcPassword().length() > 0)
        sb.append(";JdbcPassword=").append(cfg.getJdbcPassword());
    } else if (cfg.getDataSource() != null) {
      sb.append(";DataSource=java:comp/env/").append(cfg.getDataSource());
      testDataSource(cfg.getDataSource());
    }
    sb.append(";Catalog=").append(cfg.getSchemaUrl());

    // debug
    if (cfg.getRole() != null) {
      sb.append(";Role=").append(cfg.getRole());
    }
    // sb.append(";Role=California manager");
    return sb.toString();
  }

  private static void testDataSource(String dataSourceName) {
    final DataSource dataSource;
    Connection connection = null;
    String dsName = "java:comp/env/" + dataSourceName;
    try {
      dataSource = (DataSource) new InitialContext().lookup(dsName);
      connection = dataSource.getConnection();
    } catch (Throwable e) {
      String msg = "Datasource " + dsName + " is not configured properly";
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    } finally {
      if (connection != null)
        try {
          connection.close();
        } catch (SQLException e) {
          logger.error("could not close SQL Connection for DataSource " + dataSourceName, e);
        }
    }
  }

  /** 
   * replaces " with "" and ' with ''
   */
  private static String escape(String s) {
    if (s.indexOf('"') < 0 && s.indexOf('\'') < 0)
      return s;
    StringBuffer sb = new StringBuffer();
    char[] carr = s.toCharArray();
    for (int i = 0; i < carr.length; i++) {
      switch (carr[i]) {
      case '"':
        sb.append("\"\"");
        break;
      case '\'':
        sb.append("''");
        break;
      default:
        sb.append(carr[i]);
        break;
      }
    }
    return sb.toString();
  }

  public static MondrianModel instance() throws SAXException, IOException {
    URL url = MondrianModelFactory.class.getResource("config.xml");
    return (MondrianModel) ModelFactory.instance(url);
  }

  public static MondrianModel instance(Config cfg) throws SAXException, IOException {
    URL url = MondrianModelFactory.class.getResource("config.xml");
    return instance(url, cfg);
  }

  public static MondrianModel instance(URL url, Config cfg) throws SAXException, IOException {
    if (logger.isInfoEnabled()) {
      logger.info(cfg.toString());
      logger.info("ConnectString=" + makeConnectString(cfg));
    }
    MondrianModel mm = (MondrianModel) ModelFactory.instance(url);
    mm.setMdxQuery(cfg.getMdxQuery());
    mm.setConnectString(makeConnectString(cfg));
    mm.setJdbcDriver(cfg.getJdbcDriver());
    mm.setDynresolver(cfg.getDynResolver());
    if ("false".equalsIgnoreCase(cfg.getConnectionPooling()))
      mm.setConnectionPooling(false);
    mm.setExternalDataSource(cfg.getExternalDataSource());
    return mm;
  }

  public static Config createFoodmartConfig() {
    Config cfg = new Config();

    String schemaUrl = System.getProperty("catalog.uri");
    if (schemaUrl == null)
      throw new IllegalArgumentException(
          "missing foodmart catalog.uri, e.g. -Dcatalog.uri=file:///path/to/FoodMart.xml");
    cfg.setSchemaUrl(schemaUrl);

    cfg.setJdbcDriver(System.getProperty("jdbc.driver", "sun.jdbc.odbc.JdbcOdbcDriver"));
    cfg.setJdbcUrl(System.getProperty("jdbc.url", "jdbc:odbc:MondrianFoodMart"));
    cfg.setJdbcUser(System.getProperty("jdbc.user", ""));
    cfg.setJdbcPassword(System.getProperty("jdbc.password", ""));
    cfg
        .setMdxQuery("select "
            + "  {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
            + "  {([Promotion Media].[All Media], [Product].[All Products])} ON rows "
            + "from Sales " + "where ([Time].[1997]) ");
    return cfg;
  }

  public static class Config {
    String jdbcUrl;
    String jdbcDriver;
    String jdbcUser;
    String jdbcPassword;
    // name of datasource, i.e. "jdbc/JPivotDS"
    String dataSource;
    String schemaUrl;
    String mdxQuery;
    String role;
    String dynResolver;
    String connectionPooling;
    // external DataSource to be used by Mondrian
    DataSource externalDataSource = null;

    /**
     * Returns the jdbcDriver.
     * @return String
     */
    public String getJdbcDriver() {
      return jdbcDriver;
    }

    /**
     * Returns the jdbcPassword.
     * @return String
     */
    public String getJdbcPassword() {
      return jdbcPassword;
    }

    /**
     * Returns the jdbcUrl.
     * @return String
     */
    public String getJdbcUrl() {
      return jdbcUrl;
    }

    /**
     * Returns the jdbcUser.
     * @return String
     */
    public String getJdbcUser() {
      return jdbcUser;
    }

    /**
     * Returns the mdxQuery.
     * @return String
     */
    public String getMdxQuery() {
      return mdxQuery;
    }

    /**
     * Returns the schemaUrl.
     * @return String
     */
    public String getSchemaUrl() {
      return schemaUrl;
    }

    /**
     * Returns the role.
     * @return String
     */
    public String getRole() {
      return role;
    }

    /**
     * Sets the role.
     * @param role The role to set
     */
    public void setRole(String role) {
      this.role = role;
    }

    /**
     * Sets the jdbcDriver.
     * @param jdbcDriver The jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {
      this.jdbcDriver = jdbcDriver;
    }

    /**
     * Sets the jdbcPassword.
     * @param jdbcPassword The jdbcPassword to set
     */
    public void setJdbcPassword(String jdbcPassword) {
      this.jdbcPassword = jdbcPassword;
    }

    /**
     * Sets the jdbcUrl.
     * @param jdbcUrl The jdbcUrl to set
     */
    public void setJdbcUrl(String jdbcUrl) {
      this.jdbcUrl = jdbcUrl;
    }

    /**
     * Sets the jdbcUser.
     * @param jdbcUser The jdbcUser to set
     */
    public void setJdbcUser(String jdbcUser) {
      this.jdbcUser = jdbcUser;
    }

    /**
     * Sets the mdxQuery.
     * @param mdxQuery The mdxQuery to set
     */
    public void setMdxQuery(String mdxQuery) {
      this.mdxQuery = mdxQuery;
    }

    /**
     * Sets the schemaUrl.
     * @param schemaUrl The schemaUrl to set
     */
    public void setSchemaUrl(String schemaUrl) {
      this.schemaUrl = schemaUrl;
    }

    /**
     * @return
     */
    public String getDataSource() {
      return dataSource;
    }

    /**
     * @param string
     */
    public void setDataSource(String string) {
      dataSource = string;
    }

    public String getDynResolver() {
      return dynResolver;
    }

    public void setDynResolver(String dynResolver) {
      this.dynResolver = dynResolver;
    }

    public String getConnectionPooling() {
      return connectionPooling;
    }

    public void setConnectionPooling(String connectionPooling) {
      this.connectionPooling = connectionPooling;
    }

    public DataSource getExternalDataSource() {
      return externalDataSource;
    }

    public void setExternalDataSource(DataSource externalDataSource) {
      this.externalDataSource = externalDataSource;
    }

    public String toString() {
      return "Config[" + "jdbcUrl=" + jdbcUrl + ", jdbcDriver=" + jdbcDriver + ", jdbcUser="
          + jdbcUser + ", jdbcPassword=" + jdbcPassword + ", dataSource=" + dataSource
          + ", schemaUrl=" + schemaUrl + ", mdxQuery=" + mdxQuery + ", role=" + role
          + ", dynResolver=" + dynResolver + ", connectionPooling=" + connectionPooling
          + "externalDataSource=" + externalDataSource + "]";
    }
  }
}