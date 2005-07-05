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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.navi.ExpressionParser;
import com.tonbeller.jpivot.olap.navi.ExpressionParser.InvalidSyntaxException;
import com.tonbeller.jpivot.param.ParameterProvider;
import com.tonbeller.jpivot.table.ClickableMember;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.wcf.charset.CharsetFilter;
import com.tonbeller.wcf.controller.Controller;
import com.tonbeller.wcf.controller.Dispatcher;
import com.tonbeller.wcf.controller.DispatcherSupport;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;
import com.tonbeller.wcf.param.SessionParam;
import com.tonbeller.wcf.param.SessionParamPool;
import com.tonbeller.wcf.utils.DomUtils;

/**
 * Base class for ClickableMember's. There are 2 implementations:
 * <ul>
 * <li>StaticClickableMember - deprecated. It is declared via jsp tags in the body of
 * the olap table and statically (permanent) assigned to the table
 * <li>DynamicClickableMember - clickable members on per-query basis.
 * </ul>
 * To operate properly, the ClickableMemberSupport must be added to the tables dispatcher somehow
 * and to the OlapModel's changeListeners
 * 
 * @author av
 */
public class ClickableMemberSupport implements ClickableMember, ModelChangeListener {
  /**
   * name of the member property that contains the URI of the target JSP.
   * If no JSP was specified elsewhere, the value of this member property
   * should be the path of the JSP, e.g. "/my/drillthrough.jsp".
   */
  private static final String PAGE_PROPERTY = "$page";

  /** 
   * urlPattern contains {0} which is replaced with the unique name 
   * of the member, {1} is replaced with the context path
   */
  private String urlPattern;

  private String page;

  /**
   * creates the parameter
   */
  private ParameterProvider parameterProvider;

  /**
   * unique name of the member, level, hierarchy or dimension which shall
   * be clickable.
   */
  private String uniqueName;

  /**
   * parsed version of uniqueName
   */
  private Expression expression;

  /**
   * needed to format the urlPattern
   */
  private ExpressionParser parser;
  private Dispatcher dispatcher = new DispatcherSupport();
  private static Logger logger = Logger.getLogger(ClickableMemberSupport.class);

  private OlapModel model;

  /**
   * @param uniqueName name of member,level, hierarchy, dimension that shall be clickable
   * 
   * @param urlPattern any url. {0} will be replaced with the unique name of the
   * selected member
   * 
   * @param page replacement for urlPattern, must start with "/". The generated url
   * does not contain the page, the browser is forwared when the user clicks on the
   * link. This gives more consistent behaviour with browser back button - if the 
   * RequestListener is cleaned up, nothing will happen.
   * 
   * @param paramProvider creates the parameter from the member.
   */
  public ClickableMemberSupport(String uniqueName, String urlPattern, String page,
      ParameterProvider parameterProvider) {
    this.uniqueName = uniqueName;
    this.urlPattern = urlPattern;
    this.page = page;
    this.parameterProvider = parameterProvider;
    if (uniqueName == null)
      throw new IllegalArgumentException("uniqueName can not be null");
  }

  public void modelChanged(ModelChangeEvent e) {
  }

  public void structureChanged(ModelChangeEvent e) {
    dispatcher.clear();
  }

  public void startRendering(RequestContext context, TableComponent table) {
    this.model = table.getOlapModel();
    dispatcher.clear();
    expression = null;
    parser = (ExpressionParser) model.getExtension(ExpressionParser.ID);
    if (parser != null) {
      try {
        // we do not use parser.parse() here because it searches for member names too. This
        // makes a lot(!) of SQL queries - so we restrict ourselves to level, hierarchy, dimension.
        expression = parser.lookupLevel(uniqueName);
        if (expression == null)
          expression = parser.lookupHierarchy(uniqueName);
        if (expression == null)
          expression = parser.lookupDimension(uniqueName);
      } catch (InvalidSyntaxException e) {
        // we do not throw an exception here. If the user 
        // has entered an invalid value, it will be ignored
        logger.warn(null, e);
      }
    }
  }

  public void stopRendering() {
    model = null;
    parser = null;
    expression = null;
  }

  private boolean match(Member member) {
    if (parser == null || expression == null)
      return false;
    if (expression instanceof Member)
      return expression.equals(member);
    if (member.isCalculated())
      return false;
    if (expression instanceof Level)
      return expression.equals(member.getLevel());
    if (expression instanceof Hierarchy)
      return expression.equals(member.getLevel().getHierarchy());
    if (expression instanceof Dimension)
      return expression.equals(member.getLevel().getHierarchy().getDimension());
    throw new IllegalArgumentException("unknown type: " + uniqueName);
  }

  private class AddMemberToParameterPool implements RequestListener {
    Member member;
    OlapModel model;
    String page;

    AddMemberToParameterPool(OlapModel model, Member m, String page) {
      this.member = m;
      this.model = model;
      this.page = page;
    }

    public void request(RequestContext context) throws Exception {
      SessionParamPool pool = SessionParamPool.instance(context.getSession());
      Collection c = parameterProvider.createSessionParams(model, member);
      for (Iterator it = c.iterator(); it.hasNext();)
        pool.setParam((SessionParam) it.next());
      
      // if no static page URI has been specified, 
      // there may be a runtime property for it
      if (page == null) {
        Property prop = member.getProperty(PAGE_PROPERTY);
        if (prop != null)
          page = prop.getValue();
      }
      
      if (page != null)
        Controller.instance(context.getSession()).setNextView(page);
    }
  }

  private Element findCaption(Element parent) {
    NodeList nl = parent.getElementsByTagName("caption");
    if (nl.getLength() > 0)
      return (Element) nl.item(0);
    else {
      logger.error("missing caption element");
      return null;
    }
  }

  /**
   * unique name in url
   */
  private String getPatternUrl(Member member) {
    String pattern = urlPattern == null ? "?param={0}" : urlPattern;
    String uname = CharsetFilter.urlEncode(parser.unparse(member));
    Object[] args = new Object[] { uname};
    return MessageFormat.format(pattern, args);
  }

  /**
   * parameter in parameter pool, random id in url
   */
  private String getHandlerUrl(Member member, String id) {
    String pattern = urlPattern == null ? "" : urlPattern;
    char sep = '?';
    if (pattern.indexOf('?') > 0)
      sep = '&';
    return pattern + sep + id + "=x";
  }

  public void decorate(Element elem, Displayable obj) {
    if (!(obj instanceof Member))
      return;
    Member m = (Member) obj;
    if (match(m)) {

      if (parameterProvider != null) {
        RequestListener r = new AddMemberToParameterPool(model, m, page);
        String id = DomUtils.randomId();
        dispatcher.addRequestListener(id, null, r);
        Element caption = findCaption(elem);
        if (caption != null)
          caption.setAttribute("href", getHandlerUrl(m, id));
      }

      // create a url that contains the members unique name as http parameter
      else {
        Element caption = findCaption(elem);
        if (caption != null)
          caption.setAttribute("href", getPatternUrl(m));
      }

    }
  }

  public void request(RequestContext context) throws Exception {
    dispatcher.request(context);
  }
}