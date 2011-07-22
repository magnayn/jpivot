package com.tonbeller.jpivot.table.navi;

import java.util.Collection;
import java.util.Iterator;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.param.ParameterProvider;
import com.tonbeller.wcf.controller.Controller;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;
import com.tonbeller.wcf.param.SessionParam;
import com.tonbeller.wcf.param.SessionParamPool;

/**
 * a clickable member with query scope. The DynamicClickableMember is attached to the
 * Query - its lifecycle ends when the query is replaced.
 * 
 * @author av
 * @since 15.12.2004
 */
public class DynamicClickableMember extends ClickableMemberSupport {
  private String page;
  private ParameterProvider paramProvider;
  private String menuLabel;

  /**
   * @param uniqueName name of level, hierarchy or dimension that shall be clickable.
   * If null, all dimensions except Measures will be clickable.
   * 
   * @param page the page to display when the link is clicked, must start with "/". 
   * Set to null to stay on the same page
   * 
   * @param paramProvider creates the parameter from the member.
   */
  public DynamicClickableMember(String uniqueName, String menuLabel, ParameterProvider paramProvider, String page) {
    super(uniqueName);
    this.menuLabel = menuLabel;
    this.paramProvider = paramProvider;
    this.page = page;
  }

  private class AddMemberToParameterPool implements RequestListener {
    private Member member;
    private OlapModel model;
    /**
     * name of the member property that contains the URI of the target JSP.
     * If no JSP was specified elsewhere, the value of this member property
     * should be the path of the JSP, e.g. "/my/drillthrough.jsp".
     */
    private static final String PAGE_PROPERTY = "$page";

    AddMemberToParameterPool(OlapModel model, Member m) {
      this.model = model;
      this.member = m;
    }

    public void request(RequestContext context) throws Exception {
      SessionParamPool pool = SessionParamPool.instance(context.getSession());
      Collection c = paramProvider.createSessionParams(model, member);
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

  protected RequestListener createRequestListener(OlapModel model, Member m) {
    return new AddMemberToParameterPool(model, m);
  }

  public String getMenuLabel() {
    return menuLabel;
  }
}