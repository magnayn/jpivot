package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;

/**
 * @author av
 * @since 15.12.2004
 */
public interface ClickableMember extends RequestListener {
  void startRendering(RequestContext context, TableComponent table);
  void decorate(Element elem, Displayable obj);
  void stopRendering();
}
