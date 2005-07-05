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
package com.tonbeller.jpivot.param;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.navi.ExpressionParser;
import com.tonbeller.jpivot.olap.navi.SetParameter;
import com.tonbeller.jpivot.olap.navi.ExpressionParser.InvalidSyntaxException;
import com.tonbeller.wcf.expr.ExprUtils;
import com.tonbeller.wcf.param.SessionParam;
import com.tonbeller.wcf.param.SessionParamPool;

/**
 * looks for a http parameter with a certain name. If the parameter is 
 * present, the parameter is parsed and set into the MDX Query.
 * <p/>
 * If a sessionParam is specified, its value is set into the MDX query.
 * @author av
 */
public class SetParameterTag extends TagSupport {
  String httpParam;
  String mdxParam;
  String sessionParam;
  String query;

  public int doStartTag() throws JspException {
    if ((httpParam == null) == (sessionParam == null))
      throw new JspException("either httpParam or sessionParam required");

    if (httpParam != null)
      return doStartTagHttp();
    return doStartTagSession();
  }

  public int doStartTagSession() throws JspException {
    return SKIP_BODY;
  }

  public int doStartTagHttp() throws JspException {
    String value = pageContext.getRequest().getParameter(httpParam);
    if (value != null)
      return EVAL_BODY_INCLUDE;
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    if (httpParam != null)
      return doEndTagHttp();
    return doEndTagSession();
  }

  public int doEndTagSession() throws JspException {
    SessionParamPool pool = SessionParamPool.instance(pageContext.getSession());
    SessionParam p = pool.getParam(sessionParam);
    if (p != null)
      setQueryParam(p.getMdxValue());
    return super.doEndTag();
  }

  public int doEndTagHttp() throws JspException {
    String value = pageContext.getRequest().getParameter(httpParam);
    if (value != null)
      setQueryParam(value);
    return super.doEndTag();
  }

  private void setQueryParam(String value) throws JspException {
    // RequestContext context = RequestContextFactoryFinder.createContext(pageContext);
    OlapModel model = (OlapModel) ExprUtils.getModelReference(pageContext, query);
    if (model == null)
      throw new JspException("OlapModel/Query " + query + " not found");
    SetParameter setter = (SetParameter) model.getExtension(SetParameter.ID);
    if (setter == null)
      throw new JspException("SetParameter not supported");
    ExpressionParser parser = (ExpressionParser) model.getExtension(ExpressionParser.ID);
    if (parser == null)
      throw new JspException("ExpressionParser not supported");
    try {
      Expression expr = parser.parse(value);
      setter.setParameter(mdxParam, expr);
    } catch (InvalidSyntaxException e) {
      throw new JspException(e);
    }
  }

  public void setHttpParam(String string) {
    httpParam = string;
  }

  public void setMdxParam(String string) {
    mdxParam = string;
  }

  public void setQuery(String string) {
    query = string;
  }

  public void setSessionParam(String sessionParam) {
    this.sessionParam = sessionParam;
  }
}