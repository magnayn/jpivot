package com.tonbeller.jpivot.table;


import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;
import com.tonbeller.jpivot.table.SpanBuilder.SBContext;

/**
 * creates a hyperlink or popup menu on members in the table. When the user
 * clicks on a member, the member will be made available in some way (url parameter or
 * session attribute) and may forward to another jsp.
 * 
 * @author av
 * @since 15.12.2004
 */
public interface ClickableMember extends RequestListener, ModelChangeListener {
  void startRendering(RequestContext context, TableComponent table);
  void decorate(SBContext sbctx, Displayable obj);
  void stopRendering();
}
