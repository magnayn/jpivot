package com.tonbeller.jpivot.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * destroys all instances stored with this id
 * @author av
 * @since 16.02.2005
 */
public class DestroyQueryTag extends TagSupport {
  private static final Logger logger = Logger.getLogger(DestroyQueryTag.class);
  String queryName;
  
  public int doStartTag() throws JspException {
    OlapModelProxy omp = OlapModelProxy.instance(id, pageContext.getSession());
    try {
      if (queryName == null)
        omp.destroyAll();
      else
        omp.destroyQuery(queryName);
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
