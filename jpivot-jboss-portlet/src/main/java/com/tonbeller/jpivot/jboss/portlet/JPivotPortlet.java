/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2005 SHERMAN WOOD.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package com.tonbeller.jpivot.jboss.portlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.portals.bridges.common.ServletContextProvider;
import org.jboss.portal.common.context.DelegateContext;
import org.jboss.portal.core.servlet.jsp.PortalJsp;
import org.jboss.portal.server.WindowContext;
import org.jboss.portal.server.invocation.AttachmentKey;
import org.jboss.portal.server.invocation.Invocation;
import org.jboss.portal.server.invocation.component.ContextDispatcherInterceptor;
import org.jboss.portal.server.servlet.ServletCommand;

import com.tonbeller.tbutils.testenv.Environment;
import com.tonbeller.wcf.controller.Controller;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestContextFactory;
import com.tonbeller.wcf.controller.RequestContextFactoryFinder;
import com.tonbeller.wcf.controller.RequestFilter;
import com.tonbeller.wcf.controller.RequestSynchronizer;
import com.tonbeller.wcf.controller.WcfController;
import com.tonbeller.wcf.utils.DomUtils;
import com.tonbeller.wcf.utils.JDK13Utils;
import com.tonbeller.wcf.utils.UrlUtils;

public class JPivotPortlet extends GenericPortlet {

	private static Logger logger = Logger.getLogger(JPivotPortlet.class);
	
	static final String NEXTVIEW = RequestFilter.class.getName() + ".nextview";
	static final String ISNEW = RequestFilter.class.getName() + ".isnew";
	
	static final String OPERATION_PARM = "op";
	
    static final String SCREEN_PARM = "screen";
    static final String QUERY_PARM = "query";

    /** Name of the Request Attribute containing the Request.getRequestURI */
	public static final String CONTEXT = "context";

	/** if the session attribute FORCE_INDEX_JSP exists, then the client will be redirected to index.jsp */
	public static final String FORCE_INDEX_JSP = "com.tonbeller.wcf.controller.FORCE_INDEX_JSP";
	
	/** 
	 * If this request parameter is present, the DomUtils.randomId() 
	 * will be reset once. 
	 */
	public static final String RESET_RANDOM_SEED = "resetRandomSeed";
	
	/**
     * Name of class implementing {@link ServletContextProvider}
     */
    public static final String PARAM_SERVLET_CONTEXT_PROVIDER = "ServletContextProvider";
    
    private ServletContextProvider servletContextProvider;

	private String errorJSP = null;
	private String busyJSP = null;
	private String indexJSP = "/index.jsp";
	private String[] passThru = null;
	private String mainJSP = "/testpage.jsp";
	
	private String forceExtension = null;


	public JPivotPortlet() {
		super();
	}
	
	public void init(PortletConfig config) throws PortletException {
        super.init(config);
        String contextProviderClassName = getContextProviderClassNameParameter(config);
        if (contextProviderClassName == null)
            throw new PortletException("Portlet " + config.getPortletName()
                + " is incorrectly configured. Init parameter "
                + PARAM_SERVLET_CONTEXT_PROVIDER + " not specified");
        
	    if (contextProviderClassName != null)
	    {
	        try
	        {
	            Class clazz = Class.forName(contextProviderClassName);
	            if (clazz != null) {
	                Object obj = clazz.newInstance();
	                if (ServletContextProvider.class.isInstance(obj)) {
	                    servletContextProvider = (ServletContextProvider) obj;
	                }
	                else
	                    throw new PortletException("class not found");
	            }
	        } catch (Exception e)
	        {
	            if (e instanceof PortletException)
	                throw (PortletException) e;
	            e.printStackTrace();
	            throw new PortletException("Cannot load", e);
	        }
	    }
	    if (servletContextProvider == null)
	        throw new PortletException("Portlet " + config.getPortletName()
	                + " is incorrectly configured. Invalid init parameter "
	                + PARAM_SERVLET_CONTEXT_PROVIDER + " value "
	                + contextProviderClassName);
	}
	
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, PortletSecurityException, IOException {
        HttpServletRequest httpRequest = getHttpServletRequest(this, request, response);
        HttpServletResponse httpResponse = getHttpServletResponse(this, request, response);

        ContextDispatcherInterceptor.InvokeNextCommand cmd =
            (ContextDispatcherInterceptor.InvokeNextCommand)httpRequest.getAttribute(ServletCommand.REQ_ATT_KEY);
      	Invocation inv = cmd.getInvocation();
        WindowContext windowCtx = (WindowContext)inv.getAttachment(AttachmentKey.WINDOW_CONTEXT);

        String op = request.getParameter(OPERATION_PARM);
        if (op != null) {
        	if (op.equalsIgnoreCase("print")) {
        		PrintTable.processRequest(httpRequest, httpResponse, windowCtx, response);
        		return;
        	} else if (op.equalsIgnoreCase("getChartImage")) {
        		GetChartImage.processRequest(httpRequest, httpResponse, windowCtx, response);
        		return;
        	}
        }
	}
	
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException {
		//ResourceBundle bundle = getResourceBundle(request.getLocale());

        // log if necessary
        if (logger.isInfoEnabled())
          logRequest(request);
		
        DelegateContext root = new DelegateContext();
/*        
        if (request.getUser() != null)
        {
           root.next("switch_user_logged_in");
        }
*/
        request.setAttribute(PortalJsp.CTX_REQUEST, root);

        PortletSession session = request.getPortletSession(true);
        MDC.put("SessionID", session.getId());

        String cpath = request.getContextPath();
        request.setAttribute(CONTEXT, cpath);
        HttpServletRequest httpRequest = getHttpServletRequest(this, request, response);
        HttpServletResponse httpResponse = getHttpServletResponse(this, request, response);
		
		response.setContentType("text/html");

		RequestContext context = createContext(request, response);
        // set locale for JSTL tags
        Config.set(httpRequest, Config.FMT_LOCALE, context.getLocale());
/*
 * Don't do passThru right now        
        if (passThru(req)) {
          chain.doFilter(req, res);
          return;
        }
*/
        MyHandler handler = new MyHandler(this, context, request, response);
        try {
          long t1 = System.currentTimeMillis();
          RequestSynchronizer.instance(httpRequest).handleRequest(handler);
          long t2 = System.currentTimeMillis();
          if (logger.isInfoEnabled())
            logger.info("Request Execution total time: " + (t2 - t1) + " ms");
        } catch (Throwable e) {
          PrintWriter out = null;
          try {
            out = response.getWriter();
          } catch (Exception e2) {
            out = new PrintWriter(System.out);
            logger.error("No output writer could be retrieved, logging to stdout");
          }
          while (e != null) {
            logger.error("Error handling request", e);
            out.println();
            out.println("<h2>" + e.toString() + "</h2><pre>");
            e.printStackTrace(out);
            out.println("</pre>");

            Throwable prev = e;
            e = JDK13Utils.getCause(e);
            if (e == prev)
              break;
          }
        } finally {
          if (context != null)
            context.invalidate();
        }
	}

    /** TODO EBIF: das sollte wieder verschwinden, wenn HH die $VARIABLEN im Mondrian eingebaut hat */
/*
	private boolean passThru(ServletRequest req) {
      if (passThru == null)
        return false;
      HttpServletRequest hsr = (HttpServletRequest) req;
      return UrlUtils.matchPattern(hsr, passThru);
    }
*/
    public void destroy() {
    }

    /** for testing */
    void setForceExtension(String forceExtension) {
      this.forceExtension = forceExtension;
    }

    private void logRequest(PortletRequest request) {
      logger.info(">>> Request " + request.getScheme() + "://" + request.getServerName() + ":"
          + request.getServerPort() + request.getContextPath() );
    }

    public RequestContext createContext(RenderRequest request, RenderResponse response) {
      
      HttpServletRequest httpRequest = getHttpServletRequest(this, request, response);
      HttpServletResponse httpResponse = getHttpServletResponse(this, request, response);
      //HttpSession session = httpRequest.getSession(true);
      //RequestContextFactory rcf = RequestContextFactoryFinder.findFactory(session);

      //return rcf.createContext(httpRequest, httpResponse);
      return RequestContextFactoryFinder.createContext(httpRequest, httpResponse, true);
    }
    
    protected String getContextProviderClassNameParameter(PortletConfig config) {
        return config.getInitParameter(PARAM_SERVLET_CONTEXT_PROVIDER);
    }
    
    protected ServletContextProvider getServletContextProvider() {
        return servletContextProvider;
    }
    
    protected ServletContext getServletContext(GenericPortlet portlet, PortletRequest request, PortletResponse response) {
        return getServletContextProvider().getServletContext(portlet);
    }
    
    protected HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request, PortletResponse response) {
        return getServletContextProvider().getHttpServletRequest(portlet, request);
    }
    
    protected HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletRequest request, PortletResponse response) {
        return getServletContextProvider().getHttpServletResponse(portlet, response);
    }

	class MyHandler implements RequestSynchronizer.Handler {
		protected GenericPortlet portlet;
		protected RequestContext context;
	    protected RenderRequest request;
	    protected RenderResponse response;
	    protected HttpServletRequest httpRequest;
	    protected HttpServletResponse httpResponse;

	    public MyHandler(GenericPortlet portlet, RequestContext context, RenderRequest request, RenderResponse response) {
	      this.portlet = portlet;
	      this.context = context;
	      this.request = request;
	      this.response = response;
	      this.httpRequest = getHttpServletRequest(portlet, request, response);
	      this.httpResponse = getHttpServletResponse(portlet, request, response);
	    }

	    public String getResultURI() {
	      // did the controller change the view?
	      String uri = (String) request.getAttribute(NEXTVIEW);
	      if (uri != null) {
	        uri = UrlUtils.forceExtension(uri, forceExtension);
	        //uri = redirectURI(uri);
	        return uri;
	      }

	      // no, redisplay the current request
	      //return request.getRequestURI();
	      return request.getContextPath();
	    }

	    public void normalRequest() throws Exception {
	      try {

	        if (request.getParameter(RESET_RANDOM_SEED) != null)
	          DomUtils.setRandomSeed(123);

	        if (redirectToIndex())
	          return;

	        HttpSession session = httpRequest.getSession(true);

	        // fire events
	        Controller controller = WcfController.instance(session);
	        controller.request(context);
	        
	        // someone has called sendError() or sendRedirect() on the response?
	        if (context.isResponseComplete())
	          return;

	        // if the controller redirects to another page, thats fine.
	        // if not, the current page is redisplayed UNLESS there was an error
	        // in which case we go to the start page.
	        String uri = (String) request.getAttribute(NEXTVIEW);
	        if (session.getAttribute(FORCE_INDEX_JSP) != null) {
	          session.removeAttribute(FORCE_INDEX_JSP);
	          if (uri == null)
	            uri = indexJSP;
	        }
            
            String query = request.getParameter(QUERY_PARM);
            if (query != null) {
                include(query, request, response);
            }
            
            // Assume we are given a screen and a query
            String screen = request.getParameter(SCREEN_PARM);
            if (screen != null) {
                uri = screen;
            }
            
	        if (uri != null)
	        	include(uri, request, response);
	        else
	        	include(mainJSP, request, response);
	      } catch (Exception e) {
	        logger.error(null, e);
	        if (errorJSP != null) {
	          try {
	            logger.error("redirecting to error page " + errorJSP, e);
	            request.setAttribute("javax.servlet.jsp.jspException", e);
	            include(errorJSP, request, response);
	          } catch (Exception e2) {
	            // there was an error displaying the error page. We  
	            // ignore the second error and display the original error
	            // instead
	            throw e;
	          }
	        } else
	          throw e;
	      }
	    }

        public void recursiveRequest() throws Exception {
            include(mainJSP, request, response);
        }

	    public void showBusyPage(boolean redirect) throws Exception {
	      if (redirectToIndex())
	        return;
	      if (busyJSP != null) {
	        if (redirect) {
	          request.setAttribute("isBusyPage", "true");
	          include(busyJSP, request, response);
	        }
	      } else
	        throw new IllegalStateException("concurrent requests and no busy.jsp defined in web.xml");
	    }

	    public boolean isBusyPage() {
	      return "true".equals(request.getParameter("isBusyPage"));
	    }

        /**
         * @param uri
         * @throws IOException
         */
        private void include(String uri, RenderRequest request, RenderResponse response) throws PortletException, IOException {
          //uri = redirectURI(uri);
          uri = UrlUtils.forceExtension(uri, forceExtension);
          if (logger.isInfoEnabled())
            logger.info("including " + uri);
          PortletRequestDispatcher rdisp = getPortletContext()
            .getRequestDispatcher(uri);
          rdisp.include(request, response);
        }

        /*
	    private String redirectURI(String uri) {

		    if (uri.startsWith("/")) {
		      StringBuffer sb = new StringBuffer();
		      sb.append(request.getContextPath());
		      sb.append(uri);
		      uri = sb.toString();
		    }
		    return uri;

	    }
*/
	    /**
	     * true, if the current request was redirected to the index page
	     * because there is no valid session.
	     */
	    protected boolean redirectToIndex() throws Exception {
	      if (indexJSP != null) {
	        // do not redirect to index.jsp while testing
	        if (Environment.isTest())
	          return false;
	        
	        PortletSession session = request.getPortletSession();
	        boolean isNew = session.isNew();
	        if (!isNew && request.getParameter(OPERATION_PARM) != null)
		      isNew = request.getParameter(OPERATION_PARM).equalsIgnoreCase("index");
	        if (!isNew)
	          isNew = !"false".equals(session.getAttribute(ISNEW));
	        if (isNew) {
	          session.setAttribute(ISNEW, "false");
	          include(indexJSP, request, response);
	          return true;
	        }
	      }
	      return false;
	    }

	  }

}
