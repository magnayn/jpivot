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

import com.tonbeller.jpivot.param.MemberParamProvider;
import com.tonbeller.jpivot.param.ParameterProvider;
import com.tonbeller.jpivot.param.PropertyParamProvider;
import com.tonbeller.jpivot.param.PropertyPrefixParamProvider;
import com.tonbeller.jpivot.table.AxisBuilder;
import com.tonbeller.jpivot.table.ClickableMember;
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
  String menuLabel;

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

    OlapModelTag omt = (OlapModelTag) findAncestorWithClass(this, OlapModelTag.class);
    if (omt != null) {
      createDynamicClickable(omt);
      return SKIP_BODY;
    }

    throw new JspException("ClickableMemberTag must be nested in a table or query tag");
  }

  private void createDynamicClickable(OlapModelTag omt) {
    ParameterProvider provider = createProvider();
    ClickableMember clickable = createClickable(provider);
    omt.addClickable(clickable);
  }

  private void createStaticClickable(TableComponentTag tct) throws JspException {
    TableComponent tc = (TableComponent) tct.getComponent();
    ParameterProvider provider = createProvider();
    decorate(tc.getRowAxisBuilder(), createClickable(provider));
    decorate(tc.getColumnAxisBuilder(), createClickable(provider));
  }

  private ClickableMember createClickable(ParameterProvider provider) {
    if (provider != null) {
      DynamicClickableMember dcm = new DynamicClickableMember(uniqueName, menuLabel, provider, page);
      dcm.setUrlPattern(urlPattern); // support legacy
      return dcm;
    }
    return new UrlClickableMember(uniqueName, menuLabel, urlPattern);
  }

  private ParameterProvider createProvider() {
    ParameterProvider provider = null;

    if (sessionParam != null) {
      if (propertyName != null)
        provider = new PropertyParamProvider(sessionParam, propertyName);
      else
        provider = new MemberParamProvider(sessionParam);
    } else if (propertyPrefix != null) {
      provider = new PropertyPrefixParamProvider(propertyPrefix);
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

  private void decorate(AxisBuilder axisBuilder, ClickableMember clickable) {
    SpanBuilder decoree = axisBuilder.getSpanBuilder();
    SpanBuilder decorator = new StaticClickableMember(decoree, clickable);
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

  public void setMenuLabel(String menuLabel) {
    this.menuLabel = menuLabel;
  }

}