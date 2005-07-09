package com.tonbeller.jpivot.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * If you have multiple queries in the session with different <code>queryName</code> attribute,
 * this tag chooses the visible one.
 * 
 * @author av
 * @since 16.02.2005
 */
public class ChooseQueryTag extends TagSupport {
  String queryName;
  private static final Logger logger = Logger.getLogger(ChooseQueryTag.class);

  public int doStartTag() throws JspException {
    OlapModelProxy omp = OlapModelProxy.instance(id, pageContext.getSession());
    try {
      omp.showByName(queryName);
    } catch (Exception e) {
      logger.error(null, e);
      throw new JspException(e);
    }
    return super.doStartTag();
  }
  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }
}
