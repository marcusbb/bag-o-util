package provision.services.propagation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;

import provision.services.propagation.LogContextFilter;
import provision.services.propagation.PropagationContext;
import provision.services.propagation.PropagationContextDefaultImpl;
import provision.services.propagation.PropagationContextFactory;
import provision.services.propagation.UIDTypeIDImpl;

public class LogContextFilterTests {

	/**
	 * Depends that properties file exists.
	 */
	@Test
	public void testLoadedFromFile() {
		PropagationContext ctx = PropagationContextFactory.get();
		
		Assert.assertTrue(ctx instanceof PropagationContextDefaultImpl);
		
	}

	@Test
	public void concurrentIDGeneration() throws Exception {
		int genSize = 100000;
		final List<String> genList = new CopyOnWriteArrayList<String>();
		HashSet<String> genSet = new HashSet<String>();
		
		ExecutorService exec = Executors.newFixedThreadPool(10);
		
		for (int i=0;i<genSize;i++) {
			exec.submit( new Callable<Void>() {
	
				@Override
				public Void call() throws Exception {
					
					genList.add(new UIDTypeIDImpl().generateID());
					return null;
				}
					
			});
		}
		exec.shutdown();
		exec.awaitTermination(10, TimeUnit.SECONDS);
		
		for (int i=0;i<genSize;i++) {
			String id = genList.get(i);
			//System.out.println(i+":"+id);
			if (genSet.contains(id))
				Assert.fail("Duplicate id: " +id);
			genSet.add(id);
		}
	}
	
	
	@Test
	public void ensureNewId() throws Exception {
		LogContextFilter filter = new LogContextFilter();
		PropagationContext ctx = PropagationContextFactory.get();
		Assert.assertEquals(PropagationContext.EMPTY_VALUE,ctx.getId());
		
		filter.doFilter(new ServletReqMock(), new ServletRespMock(), new FilterChainMock());
		
		//unset would have done this
		Assert.assertEquals(PropagationContext.EMPTY_VALUE, ctx.getId());
		
		
	}
	@Test
	public void checkInitializationFalse() throws Exception {
		LogContextFilter filter = new LogContextFilter();
		filter.alwaysInitialize = false;
		
		PropagationContext ctx = PropagationContextFactory.get();
		Assert.assertEquals(PropagationContext.EMPTY_VALUE,ctx.getId());
		ctx.setId("my-id");
		
		filter.doFilter(new ServletReqMock(), new ServletRespMock(), new FilterChain() {
			
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1)
					throws IOException, ServletException {
				
				PropagationContext ctx = PropagationContextFactory.get();
				
				Assert.assertEquals("my-id", ctx.getId());
			}
		} );
		
		//unset would have done this
		Assert.assertEquals(PropagationContext.EMPTY_VALUE, ctx.getId());
		
		
	}
	@Test
	public void ensureIdCreated() throws Exception {
		LogContextFilter filter = new LogContextFilter();
		PropagationContext ctx = PropagationContextFactory.get();
		
		Assert.assertEquals(PropagationContext.EMPTY_VALUE,ctx.getId());
		
		filter.doFilter(new ServletReqMock(), new ServletRespMock(), new FilterChain() {
			
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1)
					throws IOException, ServletException {
				
				PropagationContext ctx = PropagationContextFactory.get();
				System.out.println("ID: " + ctx.getId());
				Assert.assertNotSame(PropagationContext.EMPTY_VALUE, ctx.getId());
			}
		} );
		
				
	}
	
	@Test
	public void ensureIdDestroyed() throws Exception {
		LogContextFilter filter = new LogContextFilter();
		PropagationContext ctx = PropagationContextFactory.get();
		
		Assert.assertEquals(PropagationContext.EMPTY_VALUE,ctx.getId());
		
		filter.doFilter(new ServletReqMock(), new ServletRespMock(), new FilterChain() {
			
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1)
					throws IOException, ServletException {
				
				PropagationContext ctx = PropagationContextFactory.get();
				
				System.out.println("ID: " + ctx.getId());
				System.out.println("Next ID: " + new UIDTypeIDImpl().generateID());
				Assert.assertNotSame(PropagationContext.EMPTY_VALUE, ctx.getId());
				Thread.currentThread().interrupt();
			}
		} );
		Assert.assertEquals(PropagationContext.EMPTY_VALUE,ctx.getId());
				
	}
	
	@Test
	public void headerPopulation() throws Exception {
		
	}
}

class FilterChainMock implements FilterChain {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
class ServletRespMock implements HttpServletResponse {

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
class ServletReqMock implements HttpServletRequest {

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getHeaderNames() {
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public Enumeration getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
}