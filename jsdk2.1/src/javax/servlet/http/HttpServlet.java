/*
 * $Id: HttpServlet.java,v 1.4 1999/04/20 20:37:45 sahmed Exp $
 * 
 * Copyright (c) 1996-1999 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 *
 */

package javax.servlet.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 *
 * Provides an abstract class that you can subclass to create
 * an HTTP servlet, which receives requests from and
 * sends responses to a Web site. When you subclass 
 * <code>HttpServlet</code>, you must override at least 
 * one method, usually one of these:
 *
 * <ul>
 * <li> <code>doGet</code>, if the servlet supports HTTP GET requests
 * <li> <code>doPost</code>, for HTTP POST requests
 * <li> <code>doPut</code>, for HTTP PUT requests
 * <li> <code>doDelete</code>, for HTTP DELETE requests
 * <li> <code>init</code> and <code>destroy</code>, 
 * which you override as a pair if you need to 
 * manage resources that are held for the life of the servlet
 * <li> <code>getServletInfo</code>, which the servlet uses to
 * provide information about itself 
 * </ul>
 *
 * <p>You usually will not override the <code>service</code>
 * method. <code>service</code> handles standard HTTP
 * requests by dispatching them to the handler methods
 * for each HTTP request type (the <code>do</code><i>xxx</i>
 * methods listed above).
 *
 * <p>Likewise, you usually will not override the 
 * <code>doOptions</code> and <code>doTrace</code> methods.
 * The <code>service</code> method supports HTTP 1.1
 * TRACE and OPTIONS requests by dispatching them to
 * <code>doTrace</code> and <code>doOptions</code>.
 * 
 * <p>Servlets typically run on multithreaded servers,
 * so you must write your servlet to handle concurrent
 * requests and synchronize access to shared resources.
 * Shared resources include in-memory data such as
 * instance or class variables and external objects
 * such as files, database connections, and network 
 * connections.
 * See the
 * <a href="http://java.sun.com/Series/Tutorial/java/threads/multithreaded.html">
 * Java Tutorial on Multithreaded Programming</a> for more
 * information on handling multiple threads in a Java program.
 *
 * @author	Various
 * @version	$Version$
 *
 */



public abstract class HttpServlet extends GenericServlet
    implements java.io.Serializable
{
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";

    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    
    private static final String LSTRING_FILE =
	"javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
	ResourceBundle.getBundle(LSTRING_FILE);
   
   
   
    
    /**
     * Does nothing, because this is an abstract class.
     * 
     */

    public HttpServlet () { }
    
    

    /**
     *
     * Receives an HTTP GET request from the protected
     * <code>service</code> method and handles the request. 
     * The GET method allows a client to read information
     * from the Web server, passing a query string appended
     * to an URL to tell the server what information
     * to return.
     *
     * <p>Overriding this method to support a GET request also
     * automatically supports an HTTP HEAD request. A HEAD
     * request is a GET request that returns no body in the
     * response, only the request header fields.
     *
     * <p>If you override this method, you should read data from
     * the request, set entity headers in the response, 
     * access the writer or output stream object, and finally,
     * write the response data. When you set headers, be
     * sure to include content type and encoding. If you use
     * a <code>PrintWriter</code> object to return the response,
     * you must set the content type before you access the
     * <code>PrintWriter</code> object.
     *
     * <p>The servlet engine must write the headers before
     * the response data, because the headers can be flushed
     * at any time after the data is written.
     *
     * <p>If you can set the Content-Length header (with the
     * {@link javax.servlet.ServletResponse.#contentType} method),
     * the servlet
     * can use a persistent connection to return its response
     * to the client, improving performance dramatically.
     * If you cannot set Content-Length, you can sometimes avoid
     * the performance penalty if the response fits in an internal
     * buffer.
     * 
     * <p>The GET method should be safe, that is, without
     * any side effects for which users are held responsible.
     * For example, most form queries have no side effects.
     * If a client request is intended to change stored data,
     * the request should use some other HTTP method.
     *
     * <p>The GET method should also be idempotent, meaning
     * that it can be safely repeated. Sometimes making a
     * method safe also makes it idempotent. For example, 
     * repeating queries is both safe and idempotent, but
     * buying a product online or modifying data is neither
     * safe nor idempotent. 
     *
     * <p>If the request is incorrectly formatted, <code>doGet</code>
     * returns an HTTP BAD_REQUEST message.
     * 
     *
     * @param req	an {@link HttpServletRequest} object that
     *			contains the request the client has made
     *			of the servlet
     *
     * @param resp	an {@link HttpServletResponse} object that
     *			contains the response the servlet sends
     *			to the object
     * 
     * @exception IOException	if an input or output error is 
     *				detected when the servlet handles
     *				the GET request
     *
     * @exception ServletException	if the request for the GET
     *					could not be handled
     *
     * 
     * @see javax.servlet.ServletResponse#setContentType
     *
     */

    protected void doGet (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	String protocol = req.getProtocol();
	String msg = lStrings.getString("http.method_get_not_supported");
	if (protocol.endsWith("1.1")) {
	    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
	} else {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
	}
    }





    /**
     *
     * Returns the time the <code>HttpServletRequest</code>
     * object was last modified,
     * in milliseconds since midnight January 1, 1970 GMT.
     * If the time is unknown, this method returns a negative
     * number.
     *
     * <p>Servlet engines that support HTTP GET requests
     * should override <code>getLastModified</code> to
     * provide an accurate object modification time. This
     * makes browser and proxy caches work more effectively,
     * reducing the load on server and network resources.
     *
     *
     * @param req	the <code>HttpServletRequest</code> 
     *			object that is sent to the servlet
     *
     * @return		a <code>long</code> integer specifying
     *			the time the <code>HttpServletRequest</code>
     *			object was last modified, in milliseconds
     *			since midnight, January 1, 1970 GMT, or
     *			-1 if the time is not known
     *
     */

    protected long getLastModified (HttpServletRequest req) {
	return -1;
    }




    /*
     * Private method; not a Javadoc comment
     *
     * <p>Receives an HTTP HEAD request from the protected
     * <code>service</code> method and handles the
     * request.
     * The client sends a HEAD request when it wants
     * to see only the headers of a response, such as
     * Content-Type or Content-Length. The HTTP HEAD
     * method counts the output bytes in the response
     * to set the Content-Length header accurately.
     *
     * <p>If you override this method, you can avoid computing
     * the response body and just set the response headers
     * directly to improve performance. Make sure that the
     * <code>doHead</code> method you write is both safe
     * and idempotent (that is, protects itself from being
     * called multiple times for one HTTP HEAD request).
     *
     * <p>If the HTTP HEAD request is incorrectly formatted,
     * <code>doHead</code> returns an HTTP BAD_REQUEST
     * message.
     *
     *
     * @param req	the request object that is passed
     *			to the servlet
     *			
     * @param resp	the response object that the servlet
     *			uses to return the headers to the clien
     *
     * @exception IOException		if an input or output error occurs
     *
     * @exception ServletException	if the request for the HEAD
     *					could not be handled
     */

    private void doHead (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	NoBodyResponse response = new NoBodyResponse(resp);
	
	doGet (req, response);
	response.setContentLength ();
    }
    




    /**
     *
     * Receives an HTTP POST request from the protected
     * <code>service</code> method and handles the request.
     * The HTTP POST method allows the client to send
     * data of unlimited length to the Web server once
     * and is useful when posting information such as
     * credit card numbers.
     *
     * <p>If you override this method, you should read data from
     * the <code>HttpServletRequest</code> object, set headers
     * for the response (including Content-Type and Content-Encoding), 
     * access a <code>PrintWriter</code> or
     * output stream object, and then write any response data
     * using a {@link javax.servlet.ServletOutputStream} object.
     *
     * <p>If you use a <code>PrintWriter</code> object to
     * write response data, set the Content-Type header before 
     * you access the <code>PrintWriter</code> object.
     * The servlet engine must write the headers before the
     * the response data, because the headers can be flushed at
     * any time after the servlet engine begins to write the body 
     * of the response.
     *
     * <p>If you use HTTP 1.1 chunked encoding (which means that
     * the response has a Transfer-Encoding header), do not set the
     * Content-Length header. If you do not use
     * chunked encoding, set the content length to allow the servlet
     * to take advantage of the HTTP "connection keep alive" feature,
     * If you cannot set the content length and therefore cannot
     * use "keep alive," you may be able to avoid the performance 
     * penalty if the response fits in an internal buffer.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example, 
     * updating stored data or buying items online.
     *
     * <p>If the HTTP POST request is incorrectly formatted,
     * <code>doPost</code> returns an HTTP BAD_REQUEST message.
     *
     *
     * @param req	an {@link HttpServletRequest} object that
     *			contains the request the client has made
     *			of the servlet
     *
     * @param resp	an {@link HttpServletResponse} object that
     *			contains the response the servlet sends
     *			to the object
     * 
     * @exception IOException	if an input or output error is 
     *				detected when the servlet handles
     *				the request
     *
     * @exception ServletException	if the request for the POST
     *					could not be handled
     *
     *
     * @see javax.servlet.ServletOutputStream
     * @see javax.servlet.ServletResponse#setContentType
     *
     *
     */

    protected void doPost (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	String protocol = req.getProtocol();
	String msg = lStrings.getString("http.method_post_not_supported");
	if (protocol.endsWith("1.1")) {
	    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
	} else {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
	}
    }




    /**
     * Receives an HTTP PUT request from the protected
     * <code>service</code> method and handles the
     * request. The PUT operation allows a client to 
     * place a file on the server and is similar to 
     * sending a file by FTP.
     *
     * <p>If you override this method, your method must leave intact
     * any content headers sent with the request (including
     * Content-Length, Content-Type, Content-Transfer-Encoding,
     * Content-Encoding, Content-Base, Content-Language, Content-Location,
     * Content-MD5, and Content-Range). If your method cannot
     * handle a content header, it must issue an error message
     * (HTTP 501 - Not Implemented) and discard the request.
     * For more information on HTTP 1.1, see RFC 2068
     * <a href="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc2068.txt"></a>.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations that <code>doPut</code> performs can have side
     * effects for which the user can be held accountable. If you
     * override this method, you may want to save a copy of the
     * affected URL in temporary storage.
     *
     * <p>If the HTTP PUT request is incorrectly formatted,
     * <code>doPut</code> returns an HTTP BAD_REQUEST message.
     *
     *
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				PUT request
     *
     * @exception ServletException	if the request for the PUT
     *					cannot be handled
     *
     */
  
    protected void doPut (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	String protocol = req.getProtocol();
	String msg = lStrings.getString("http.method_put_not_supported");
	if (protocol.endsWith("1.1")) {
	    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
	} else {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
	}
    }




    /**
     * 
     * Receives an HTTP DELETE request from the protected
     * <code>service</code> method and handles the
     * request. The DELETE
     * operation allows a client to remove a document
     * or Web page from the server.
     * 
     * <p>This method does not need to be either safe
     * or idempotent. Operations requested through
     * DELETE can have side effects for which users
     * can be held accountable. If you override this
     * method, you may want to save a copy of the affected
     * page in temporary storage.
     *
     * <p>If the HTTP DELETE request is incorrectly formatted,
     * <code>doDelete</code> returns an HTTP BAD_REQUEST
     * message.
     *
     *
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client				
     *
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				DELETE request
     *
     * @exception ServletException	if the request for the
     *					DELETE cannot be handled
     *
     */
     
    protected void doDelete (HttpServletRequest req,
			     HttpServletResponse resp)
	throws ServletException, IOException
    {
	String protocol = req.getProtocol();
	String msg = lStrings.getString("http.method_delete_not_supported");
	if (protocol.endsWith("1.1")) {
	    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
	} else {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
	}
    }
    




    private Method [] getAllDeclaredMethods (Class c) {
	if (c.getName().equals("javax.servlet.http.HttpServlet"))
	    return null;
	
	int j=0;
	Method [] parentMethods = getAllDeclaredMethods(c.getSuperclass());
	Method [] thisMethods = c.getDeclaredMethods();
	
	if (parentMethods!=null) {
	    Method [] allMethods =
		new Method [parentMethods.length + thisMethods.length];
	    for (int i=0; i<parentMethods.length; i++) {
		allMethods[i]=parentMethods[i];
		j=i;
	    }
	    j++;
	    for (int i=j; i<thisMethods.length+j; i++) {
		allMethods[i] = thisMethods[i-j];
	    }
	    return allMethods;
	}
	return thisMethods;
    }






    /**
     *
     * Receives an HTTP OPTIONS request from the protected
     * <code>service method and handles the request.
     * The OPTIONS request determines which HTTP methods 
     * the server supports and
     * returns an appropriate header. For example, if a servlet
     * overrides <code>doGet</code>, this method returns the
     * following header:
     *
     * <p><code>Allow: GET, HEAD, TRACE, OPTIONS</code>
     *
     * <p>You do not need to override this method unless your
     * servlet implements new HTTP methods, beyond those 
     * implemented by HTTP 1.1.
     *
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client				
     *
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				OPTIONS request
     *
     * @exception ServletException	if the request for the
     *					OPTIONS cannot be handled
     *
     */
         
    protected void doOptions (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	Method [] methods = getAllDeclaredMethods(this.getClass());
	
	boolean ALLOW_GET = false;
	boolean ALLOW_HEAD = false;
	boolean ALLOW_POST = false;
	boolean ALLOW_PUT = false;
	boolean ALLOW_DELETE = false;
	boolean ALLOW_TRACE = true;
	boolean ALLOW_OPTIONS = true;
	
	for (int i=0; i<methods.length; i++) {
	    Method m = methods[i];
	    
	    if (m.getName().equals("doGet")) {
		ALLOW_GET = true;
		ALLOW_HEAD = true;
	    }
	    if (m.getName().equals("doPost")) 
		ALLOW_POST = true;
	    if (m.getName().equals("doPut"))
		ALLOW_PUT = true;
	    if (m.getName().equals("doDelete"))
		ALLOW_DELETE = true;
	    
	}
	
	String allow = null;
	if (ALLOW_GET)
	    if (allow==null) allow=METHOD_GET;
	if (ALLOW_HEAD)
	    if (allow==null) allow=METHOD_HEAD;
	    else allow += ", " + METHOD_HEAD;
	if (ALLOW_POST)
	    if (allow==null) allow=METHOD_POST;
	    else allow += ", " + METHOD_POST;
	if (ALLOW_PUT)
	    if (allow==null) allow=METHOD_PUT;
	    else allow += ", " + METHOD_PUT;
	if (ALLOW_DELETE)
	    if (allow==null) allow=METHOD_DELETE;
	    else allow += ", " + METHOD_DELETE;
	if (ALLOW_TRACE)
	    if (allow==null) allow=METHOD_TRACE;
	    else allow += ", " + METHOD_TRACE;
	if (ALLOW_OPTIONS)
	    if (allow==null) allow=METHOD_OPTIONS;
	    else allow += ", " + METHOD_OPTIONS;
	
	resp.setHeader("Allow", allow);
    }
    
    
    
    
    /**
     *
     * Receives an HTTP TRACE request from the protected
     * <code>service</code> method and handles the request.
     * HTTP TRACE returns the headers sent with the TRACE
     * request to the client, so that they can be used in
     * debugging. You do not usually need to override this method. 
     *
     *
     *
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client				
     *
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				TRACE request
     *
     * @exception ServletException	if the request for the
     *					TRACE cannot be handled
     *
     */

    protected void doTrace (HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException
    {
	
	int responseLength;
	
	String CRLF = "\r\n";
	String responseString = "TRACE "+ req.getRequestURI()+
	    " " + req.getProtocol();
	
	Enumeration reqHeaderEnum = req.getHeaderNames();
	
	while( reqHeaderEnum.hasMoreElements() ) {
	    String headerName = (String)reqHeaderEnum.nextElement();
	    responseString += CRLF + headerName + ": " +
		req.getHeader(headerName); 
	}
	
	responseString += CRLF;
	
	responseLength = responseString.length();
	
	resp.setContentType("message/http");
	resp.setContentLength(responseLength);
	ServletOutputStream out = resp.getOutputStream();
	out.print(responseString);	
	out.close();
	return;
    }		





    /**
     *
     * Receives standard HTTP requests from the public
     * <code>service</code> method and dispatches
     * them to the <code>do</code><i>xxx</i> methods defined in 
     * this class. This method is an HTTP-specific version of the 
     * {@link javax.servlet.Servlet.service} method. You will probably
     * not need to override this method.
     *
     *
     *
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client				
     *
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				TRACE request
     *
     * @exception ServletException	if the request for the
     *					TRACE cannot be handled
     * 
     * @see 				javax.servlet.Servlet#service
     *
     */

    protected void service (HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
	String method = req.getMethod ();

	if (method.equals(METHOD_GET)) {
	    long lastModified = getLastModified(req);
	    if (lastModified == -1) {
		// servlet doesn't support if-modified-since, no reason
		// to go through further expensive logic
		doGet(req, resp);
	    } else {
		long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
		if (ifModifiedSince == -1) {
		    // if the client didn't ask for a if-modifed-since
		    // no need to go further -- just do it.
		    doGet(req, resp);
		} else {
		    // this is the most expensive path through
		    // but we know we need to do it at this point
		    maybeSetLastModified (resp, lastModified);
		    long now = System.currentTimeMillis();
		    if (now < ifModifiedSince ||
			ifModifiedSince < lastModified) {
			doGet(req, resp);
		    } else {
			// XXX
			// this is more of a message than an error, but
			// sendError does the job just fine. Maybe we
			// should have a response.sendMessage or some
			// such in a future api rev...
			resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
		    }
		}
	    }

	} else if (method.equals (METHOD_HEAD)) {
	    long lastModified = getLastModified (req);
	    maybeSetLastModified (resp, lastModified);
	    doHead (req, resp);

	} else if (method.equals (METHOD_POST)) {
	    doPost (req, resp);
	    
	} else if (method.equals (METHOD_PUT)) {
	    doPut(req, resp);	
	    
	} else if (method.equals (METHOD_DELETE)) {
	    doDelete(req, resp);
	    
	} else if (method.equals (METHOD_OPTIONS)) {
	    doOptions(req,resp);
	    
	} else if (method.equals (METHOD_TRACE)) {
	    doTrace(req,resp);
	    
	} else {
	    //
	    // Note that this means NO servlet supports whatever
	    // method was requested, anywhere on this server.
	    //

	    String errMsg = lStrings.getString("http.method_not_implemented");
	    Object[] errArgs = new Object[1];
	    errArgs[0] = method;
	    errMsg = MessageFormat.format(errMsg, errArgs);
	    
	    resp.sendError (HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
	}
    }
    




    /*
     * Sets the Last-Modified entity header field, if it has not
     * already been set and if the value is meaningful.  Called before
     * doGet, to ensure that headers are set before response data is
     * written.  A subclass might have set this header already, so we
     * check.
     */

    private void maybeSetLastModified (HttpServletResponse resp,
				       long lastModified) {
	if (resp.containsHeader(HEADER_LASTMOD))
	    return;
	if (lastModified >= 0)
	    resp.setDateHeader(HEADER_LASTMOD, lastModified);
    }
   
   
   
    
    /**
     *
     * Dispatches client requests to the protected
     * <code>service</code> method. You will usually not
     * override this method.
     *
     * 
     * @param req	the {@link HttpServletRequest} object that
     *			contains the request the client made of
     *			the servlet
     *
     *
     * @param resp	the {@link HttpServletResponse} object that
     *			contains the response the servlet returns
     *			to the client				
     *
     *
     * @exception IOException	if an input or output error occurs
     *				while the servlet is handling the
     *				TRACE request
     *
     * @exception ServletException	if the request for the
     *					TRACE cannot be handled
     *
     * 
     * @see javax.servlet.Servlet#service
     *
     */

    public void service(ServletRequest req, ServletResponse res)
	throws ServletException, IOException
    {
	HttpServletRequest	request;
	HttpServletResponse	response;
	
	try {
	    request = (HttpServletRequest) req;
	    response = (HttpServletResponse) res;
	} catch (ClassCastException e) {
	    throw new ServletException ("non-HTTP request or response");
	}
	service (request, response);
    }
}




/*
 * A response that includes no body, for use in (dumb) "HEAD" support.
 * This just swallows that body, counting the bytes in order to set
 * the content length appropriately.  All other methods delegate directly
 * to the HTTP Servlet Response object used to construct this one.
 */
// file private
class NoBodyResponse implements HttpServletResponse {
    private HttpServletResponse		resp;
    private NoBodyOutputStream		noBody;
    private PrintWriter			writer;
    private boolean			didSetContentLength;

    // file private
    NoBodyResponse (HttpServletResponse r) {
	resp = r;
	noBody = new NoBodyOutputStream ();
    }

    // file private
    void setContentLength () {
	if (!didSetContentLength)
	  resp.setContentLength (noBody.getContentLength ());
    }


    // SERVLET RESPONSE interface methods

    public void setContentLength (int len) {
	resp.setContentLength (len);
	didSetContentLength = true;
    }

    public void setContentType (String type)
      { resp.setContentType (type); }

    public ServletOutputStream getOutputStream () throws IOException
      { return noBody; }

    public String getCharacterEncoding ()
	{ return resp.getCharacterEncoding (); }

    public PrintWriter getWriter () throws UnsupportedEncodingException
    {
	if (writer == null) {
	    OutputStreamWriter	w;

	    w = new OutputStreamWriter (noBody, getCharacterEncoding ());
	    writer = new PrintWriter (w);
	}
	return writer;
    }


    // HTTP SERVLET RESPONSE interface methods

    public void addCookie(Cookie cookie)
      { resp.addCookie(cookie); }

    public boolean containsHeader (String name)
      { return resp.containsHeader (name); }

    public void setStatus (int sc, String sm)
      { resp.setStatus (sc, sm); }

    public void setStatus (int sc)
      { resp.setStatus (sc); }

    public void setHeader (String name, String value)
      { resp.setHeader (name, value); }

    public void setIntHeader (String name, int value)
      { resp.setIntHeader (name, value); }

    public void setDateHeader (String name, long date)
      { resp.setDateHeader (name, date); }

    public void sendError (int sc, String msg) throws IOException
      { resp.sendError (sc, msg); }

    public void sendError (int sc) throws IOException
      { resp.sendError (sc); }

    public void sendRedirect (String location) throws IOException
      { resp.sendRedirect (location); }
    
    public String encodeURL (String url) 
      { return resp.encodeURL(url); }

    public String encodeRedirectURL (String url)
      { return resp.encodeRedirectURL(url); }
      
      
      
      

    /**
     * @deprecated	As of Version 2.0, replaced by
     * 			{@link HttpServletResponse#encodeURL}.
     *
     */
     
     
    public String encodeUrl (String url) 
      { return this.encodeURL(url); }
      
      
      
      
      
      
      

    /**
     * @deprecated	As of Version 2.0, replaced by
     *			{@link HttpServletResponse#encodeRedirectURL}.
     *
     */
     
     
    public String encodeRedirectUrl (String url)
      { return this.encodeRedirectURL(url); }

}







/*
 * Servlet output stream that gobbles up all its data.
 */
 
// file private
class NoBodyOutputStream extends ServletOutputStream {

    private static final String LSTRING_FILE =
	"javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
	ResourceBundle.getBundle(LSTRING_FILE);

    private int		contentLength = 0;

    // file private
    NoBodyOutputStream () {}

    // file private
    int getContentLength () {
	return contentLength;
    }

    public void write (int b) {
	contentLength++;
    }

    public void write (byte buf [], int offset, int len)
	throws IOException
    {
	if (len >= 0) {
	    contentLength += len;
	} else {
	    // XXX
	    // isn't this really an IllegalArgumentException?
	    
	    String msg = lStrings.getString("err.io.negativelength");
	    throw new IOException ("negative length");
	}
    }
}
