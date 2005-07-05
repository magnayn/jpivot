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
package com.tonbeller.jpivot.table.navi;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.param.DefaultParamProvider;
import com.tonbeller.jpivot.param.ParameterProvider;
import com.tonbeller.jpivot.table.AxisBuilder;
import com.tonbeller.jpivot.table.SpanBuilder;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.jpivot.table.TableComponentTag;
import com.tonbeller.jpivot.tags.OlapModelTag;

/**
 * allows to click on a member in the table and pass the member
 * as parameter to another jsp page
 * 
 * @author av
 */
public class ClickableMemberTag extends TagSupport {
  String urlPattern;
  String uniqueName;

  String sessionParam;
  String propertyName;
  String propertyPrefix;
  String providerClass;
  String page;

  private static Logger logger = Logger.getLogger(ClickableMemberTag.class);

  public int doStartTag() throws JspException {
    if (propertyPrefix != null && sessionParam != null)
      throw new JspException("propertyPrefix and sessionParam can not be specified both");

    if (logger.isInfoEnabled())
      logger.info("creating clickable member for URL: " + urlPattern + ", uniqueName: "
          + uniqueName);

    TableComponentTag tct = (TableComponentTag) findAncestorWithClass(this, TableComponentTag.class);
    if (tct != null) {
      createStaticClickable(tct);
      return SKIP_BODY;
    }
    
    OlapModelTag omt = (OlapModelTag)findAncestorWithClass(this, OlapModelTag.class);
    if (omt != null) {
      createDynamicClickable(omt);
      return SKIP_BODY;
    }
    
    throw new JspException("ClickableMemberTag must be nested in a table or query tag");
  }

  private void createDynamicClickable(OlapModelTag omt) {
    ParameterProvider provider = createProvider();
    DynamicClickableMember dcm = new DynamicClickableMember(uniqueName, urlPattern, page, provider);
    omt.addClickable(dcm);
  }

  private void createStaticClickable(TableComponentTag tct) throws JspException {
    TableComponent tc = (TableComponent) tct.getComponent();
    ParameterProvider provider = createProvider();
    decorate(tc.getRowAxisBuilder(), provider);
    decorate(tc.getColumnAxisBuilder(), provider);
  }

  private ParameterProvider createProvider() {
    ParameterProvider provider = null;

    if (sessionParam != null) {
      if (propertyName != null)
        provider = DefaultParamProvider.createPropertyInstance(sessionParam, propertyName);
      else
        provider = DefaultParamProvider.createMemberInstance(sessionParam);
    } else if (propertyPrefix != null) {
      provider = DefaultParamProvider.createPropertyPrefixInstance(propertyPrefix);
    } else if (providerClass != null) {
      try {
        provider = (ParameterProvider) Class.forName(providerClass).newInstance();
      } catch (InstantiationException e) {
        logger.error(null, e);
      } catch (IllegalAccessException e) {
        logger.error(null, e);
      } catch (ClassNotFoundException e) {
        logger.error(null, e);
      }
    }
    return provider;
  }

  private void decorate(AxisBuilder axisBuilder, ParameterProvider provider) {
    SpanBuilder decoree = axisBuilder.getSpanBuilder();
    SpanBuilder decorator = new StaticClickableMember(decoree, uniqueName, urlPattern, page, provider);
    axisBuilder.setSpanBuilder(decorator);
  }

  public void setUniqueName(String string) {
    uniqueName = string;
  }

  public void setUrlPattern(String string) {
    urlPattern = string;
  }

  public void setSessionParam(String sessionParam) {
    this.sessionParam = sessionParam;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public void setPropertyPrefix(String propertyPrefix) {
    this.propertyPrefix = propertyPrefix;
  }

  public void setProviderClass(String providerClass) {
    this.providerClass = providerClass;
  }

  public void setPage(String page) {
    this.page = page;
  }
}