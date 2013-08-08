package provision.services.propagation;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * A supporting artifact of LogContext, by simplifying the entry points 
 * from the added benefits of a servlet engine with filter capabilities.
 * The intent is to mark the places where logging propagation will
 * begin (via configuration- web.xml).
 * Hence there is no need to explicitly write code to capture the start of the the request.
 * 
 * However, since the filter does not automatically detect userId and sapSoldTo,
 * this must be provided by the clients through:
 * {@link #setSapSold(HttpSession, String)} and {@link #setUserId(HttpSession, String)}
 * This is generally a one time step (at login for example). But must be done each time
 * for stateless services.
 * The session is passed since the filter will then decorate the user's session 
 * with its own copies of the propagation context that will live for the life
 * of the session, and be set for every incoming request.
 * 
 * Additional behaviour for checking the request headers and determining if propagation
 * has been added to an external PRV source, allow configurable and automatic
 * detection of propagation handling, when invoking {@link #decorateHttpRequest(HttpMethod)}
 * prior to executing an http method call {@link HttpMethod}.
 * 
 * 
 * @author msimonsen
 *
 */
public class LogContextFilter implements Filter {

	private boolean checkSession = true;
	private boolean checkHeader = true;
	protected boolean alwaysInitialize = true;
	
	public final static String HTTP_HEADER_PREFIX = "PRV_CTX_";
	public final static String SESSION_PREFIX = PropagationContext.PREFIX;
	/**
	 * Possibly parameter configurations:
	 * - check the http headers
	 * - check the http session (should be on by default)
	 * 
	 * by default both should be enabled 
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		checkSession = isEnabled(filterConfig, "checkSession");
		checkHeader = isEnabled(filterConfig, "checkHeader");
		alwaysInitialize = isEnabled(filterConfig,"alwaysInitialize");
	}
	
	private boolean isEnabled(FilterConfig filterConfig,String parameter) {
		String param = filterConfig.getInitParameter(parameter);
		if ("false".equalsIgnoreCase(param))
			return false;
		
		return true;			
				
	}
	
	public void destroy() {
		
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		HttpServletRequest httpRequest = (HttpServletRequest)request;
//		System.out.println("Getting ctx/path: " + httpRequest.getContextPath() +httpRequest.getServletPath());
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpSession session = httpReq.getSession(false);
		
		PropagationContext logCtx = null;
				
		try {
			logCtx = PropagationContextFactory.get();
			
			//check initialization
			if (alwaysInitialize) {
				logCtx.init(true,ContextPropagationMode.ALL);
			}else if (!logCtx.isInitialized()) {
				
				logCtx.init(true,ContextPropagationMode.ALL);
				
			}
			
			//first check session information
			if (checkSession)
				populateFromSession(session,logCtx);
			//mark the IP
			String IP = request.getRemoteAddr();
			logCtx.setStringCtx(PropagationContext.IP_KEY, IP);
			//the request header information over-rides anything in session
			//although there should be no intersection
			if (checkHeader)
				populateFromRequestHeaders(httpReq,logCtx);
					
				
			chain.doFilter(request, response);
		}finally {
			//Unset the context information
			if (logCtx !=null) 
				logCtx.unSetContext();
		}
		
	}

	@SuppressWarnings("unchecked")
	private void populateFromRequestHeaders(HttpServletRequest request,PropagationContext logCtx) {
		
		Enumeration<String> headers = request.getHeaderNames();
		while(headers.hasMoreElements()) {
			String header = headers.nextElement().toUpperCase(); //coming in low-case !
			if (header.startsWith(HTTP_HEADER_PREFIX)) {
				String key = header.substring(HTTP_HEADER_PREFIX.length(), header.length());
				logCtx.setStringCtx(key, request.getHeader(header));
			}
		}
		
	}
	
	private void populateFromSession(HttpSession session,PropagationContext logCtx) {
		if (session != null) {
			logCtx.init((String)session.getAttribute(PropagationContext.USER_ID_KEY), 
				(String)session.getAttribute(PropagationContext.SAP_SOLD_TO_KEY));
		}
		
	}
	
	
	/**
	 * Assigns a user Id for this particular session, which will be used for
	 * all subsequent requests.
	 * This would be equivalent to setting {@link WLPropagationContextImpl#setUserId(String)}
	 * for each http request, which will be used by the filter in subsequent requests.
	 * 
	 * This will over-ride anything previously set by previous calls.
	 * 
	 * @param session
	 * @param userId
	 */
	public static void setUserId(HttpSession session,String userId) {
		session.removeAttribute(PropagationContext.USER_ID_KEY);
		session.setAttribute(PropagationContext.USER_ID_KEY, userId);
		PropagationContextFactory.get().setUserId(userId);
	}
	/**
	 * Assigns a sapSoldTo for this particular session, which will be used for
	 * all subsequent requests.
	 * This would be equivalent to setting {@link WLPropagationContextImpl#setSapSoldTo(String)}
	 * for each http request, which will be used by the filter in subsequent requests.
	 * 
	 * This will over-ride anything previously set by previous calls.
	 * 
	 * @param session
	 * @param userId
	 */
	public static void setSapSold(HttpSession session,String sapSoldTo) {
		session.removeAttribute(PropagationContext.SAP_SOLD_TO_KEY);
		session.setAttribute(PropagationContext.SAP_SOLD_TO_KEY, sapSoldTo);
		PropagationContextFactory.get().setSapSoldTo(sapSoldTo);
	}
	/**
	 * Decorates {@link HttpMethod} to add an addition header information
	 * when submitting a request, typically interPRV type.
	 * Assumes that {@link WLPropagationContextImpl} has already been populated.
	 * 
	 * Adds headers to the outgoing request with header specific information:
	 * Removes any headers with the same information if it was already set.
	 * 
	 * - requestId 
	 * - userId
	 * - nodeId
	 * - sapSoldTo
	 *
	 * @param httpMethod
	 */
	
	/*public static void decorateHttpRequest(HttpRequest httpMethod) {
		
		PropagationContext logCtx = PropagationContextFactory.get();
		
		Iterator<String> keys = logCtx.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			httpMethod.addHeader(HTTP_HEADER_PREFIX + key,logCtx.getStringCtx(key) );
		}		
	}*/
	
}
