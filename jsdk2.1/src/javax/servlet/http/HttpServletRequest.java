/*
 * $Id: HttpServletRequest.java,v 1.4 1999/04/20 20:37:47 sahmed Exp $
 * 
 * Copyright (c) 1995-1999 Sun Microsystems, Inc. All Rights Reserved.
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
 */

package javax.servlet.http;

import javax.servlet.ServletRequest;
import java.util.Enumeration;

/**
 *
 * Extends the {@link javax.servlet.ServletRequest} interface
 * to provide additional functionality for the request object
 * that is passed to an HTTP servlet. 
 *
 * <p>The servlet engine implements
 * this interface to create <code>HttpServletRequest</code> objects,
 * which pass information from the client to the <code>service<code>
 * method of an <code>HttpServlet</code>.
 *
 *
 * @author 	Various
 * @version	$Version$
 *
 *
 */

public interface HttpServletRequest extends ServletRequest {



    /**
     * Returns the name of the authentication scheme the
     * server uses, for example, "BASIC" or "SSL," or <code>null</code>
     * if the server does not have an authentication scheme. 
     *
     * <p>The authentication scheme provides a challenge-response
     * model in which the server challenges the client,
     * and the client provides authentication information.
     * Same as the value of the CGI variable AUTH_TYPE.
     *
     *
     * @return		a <code>String</code> specifying the name of
     *			the authentication scheme, or
     *			<code>null</code> if the server
     *			does not have an authentication
     *			scheme
     *
     */
   
    public String getAuthType();
    
   
    

    /**
     *
     * Returns an array containing all of the <code>Cookie</code>
     * objects the browser sent with this request.
     * This method returns <code>null</code> if the browser did 
     * not send any cookies.
     *
     * @return		an array of all the <code>Cookies</code>
     *			included with this request, or <code>null</code>
     *			if the request has no cookies
     *
     *
     */

    public Cookie[] getCookies();
    
    
    

    /**
     *
     * Returns the value of the specified request header
     * as a <code>long</code> value that represents a 
     * <code>Date</code> object. Use this method with
     * headers that contain dates, such as
     * <code>If-Modified-Since</code>. 
     *
     * <p>The date is returned as
     * the number of milliseconds since January 1, 1970 GMT.
     * The header name is case insensitive.
     *
     * <p>If the request did not have a header of the
     * specified name, this method returns -1. If the header
     * can't be converted to a date, the method returns
     * an <code>IllegalArgumentException</code>.
     *
     * @param name		a <code>String</code> specifying the
     *				name of the header
     *
     * @return			a <code>long</code> value
     *				representing the date specified
     *				in the header expressed as
     *				the number of milliseconds
     *				since January 1, 1970 GMT,
     *				or -1 if the named header
     *				was not included with the
     *				reqest
     *
     */

    public long getDateHeader(String name);
    
    
    

    /**
     *
     * Returns the value of the specified request header
     * as a <code>String</code>. If the named header wasn't 
     * sent with
     * the request, this method returns <code>null</code>.
     * The header name is case insensitive. You can use
     * this method with any request header.
     *
     * @param name		a <code>String</code> specifying the
     *				header name
     *
     * @return			a <code>String</code> containing the
     *				value of the requested
     *				header, or <code>null</code>
     *				if the request does not
     *				have a header of that name
     *
     */			

    public String getHeader(String name); 
    
    
    
    

    /**
     *
     * Returns an enumeration of all the header names
     * this request contains. If the request has no
     * headers, this method returns an empty enumeration.
     *
     * <p>Some servlet engines do not allow do not allow
     * servlets to access headers using this method, in
     * which case this method returns <code>null</code>
     *
     * @return			an enumeration of all the
     *				header names sent with this
     *				request; if the request has
     *				no headers, an empty enumeration;
     *				if the servlet engine does not
     *				allow servlets to use this method,
     *				<code>null</code>
     *
     */

    public Enumeration getHeaderNames();
    
    
    

    /**
     *
     * Returns the value of the specified request header
     * as an integer. If the request does not have a header
     * of the specified name, this method returns -1. If the
     * header cannot be converted to an integer, this method
     * throws a <code>NumberFormatException</code>.
     *
     * <p>The header name is case insensitive.
     *
     * @param name		a <code>String</code> specifying the name
     *				of a request header
     *
     * @return			an integer expressing the value 
     * 				of the request header or -1
     *				if the request doesn't have a
     *				header of this name
     *
     */

    public int getIntHeader(String name);
    
    
    

    /**
     *
     * Returns the name of the HTTP method with which this 
     * request was made, for example, GET, POST, or PUT.
     * The returned <code>String</code> is the same as
     * the value of the CGI variable REQUEST_METHOD.
     *
     * @return			a <code>String</code> 
     *				specifying the name
     *				of the method with which
     *				this request was made
     *
     */
 
    public String getMethod();
    
    
    

    /**
     *
     * Returns any extra path information associated with
     * the URL the client sent when it made this request.
     * The extra path information follows the servlet path
     * (the URI of the URL) but precedes the query string.
     * This method returns <code>null</code> if there
     * was no extra path information.
     *
     * <p>The information this method returns is
     * the same as the value of the CGI variable PATH_INFO.
     *
     *
     * @return		a <code>String</code> specifying 
     *			extra path information that comes
     *			after the servlet path but before
     *			the query string in the request URL;
     *			or <code>null</code> if the URL does not have
     *			any extra path information
     *
     */
     
    public String getPathInfo();
    

 

    /**
     *
     * Returns any extra path information after the servlet name
     * but before the query string, and translates it to a real
     * path. Same as the value of the CGI variable PATH_TRANSLATED.
     *
     * <p>If the URL does not have any extra path information,
     * this method returns <code>null</code>.
     *
     *
     * @return		a <code>String</code> specifying the
     *			real path, or <code>null</code> if
     *			the URL does not have any extra path
     *			information
     *
     *
     */

    public String getPathTranslated();
    
    
    

    /**
     *
     * Returns the query string that is contained in the request
     * URL after the path. This method returns <code>null</code>
     * if the URL does not have a query string. Same as the value
     * of the CGI variable QUERY_STRING.
     *
     * @return		a <code>String</code> containing the query
     *			string or <code>null</code> if the URL 
     *			contains no query string
     *
     */

    public String getQueryString();
    
    
    

    /**
     *
     * Returns the name of the user making this request, if the
     * user has logged in using HTTP authentication. This method
     * returns <code>null</code> if the user login is not authenticated.
     * Whether the user name is sent with each subsequent request
     * depends on the browser. Same as the value of the CGI variable
     * REMOTE_USER.
     *
     * @return		a <code>String</code> specifying the name
     *			of the user making this request, or <code>null</code
     *			if the user name is not known
     *
     */

    public String getRemoteUser();
    
    
    

    /**
     *
     * Returns the session ID specified by the client. This may
     * not be the same as the ID of the actual session in use.
     * For example, if the request specified an old (expired)
     * session ID and the server has started a new session, this
     * method gets a new session with a new ID. If the request
     * did not specify a session ID, this method returns
     * <code>null</code>.
     *
     *
     * @return		a <code>String</code> specifying the session
     *			ID, or <code>null</code> if the request did
     *			not specify a session ID
     *
     * @see		#isRequestedSessionIdValid
     *
     */

    public String getRequestedSessionId ();
    
    
    
    
    /**
     *
     * Returns the part of this request's URL from the protocol
     * name up to the query string in the first line of the HTTP request.
     * For example:
     *
     * <blockquote>
     * <table>
     * <tr align=left><th>First line of HTTP request<th>
     * <th>Returned Value
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>http://foo.bar/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     * </blockquote>
     *
     * <p>To reconstruct an URL with a scheme and host, use
     * {@link HttpUtils#getRequestURL}, which
     * returns a <code>StringBuffer</code>.
     *
     * @return		a <code>String</code> containing
     *			the part of the URL from the 
     *			protocol name up to the query string
     *
     * @see		HttpUtils#getRequestURL
     *
     */

    public String getRequestURI();
    
    
    

    /**
     *
     * Returns the part of this request's URL that calls
     * the servlet. This includes either the servlet name or
     * a path to the servlet, but does not include any extra
     * path information or a query string. Same as the value 
     * of the CGI variable SCRIPT_NAME.
     *
     *
     * @return		a <code>String</code> containing
     *			the name or path of the servlet being
     *			called, as it is specified in
     *			the request URL 
     *
     *
     */

    public String getServletPath();
    
    
    

    /**
     *
     * Returns the current <code>HttpSession</code>
     * associated with this request or, if necessary, creates
     * a new session for the request. Use <code>true</code>
     * for <code>create</code> to create a new session,
     * or <code>false</code> to return the current
     * <code>HttpSession</code>. 
     *
     * <p>If <code>create</code> is <code>false</code>
     * and the request has no valid <code>HttpSession</code>,
     * this method returns <code>null</code>.
     *
     * <p>To make sure the session is properly maintained,
     * you must call this method at least once before 
     * you write any output to the response. Newly created 
     * sessions (that is, sessions for which 
     * <code>HttpSession.isNew</code> returns <code>true</code>)
     * do not have any application-specific state.
     *
     *
     *
     *
     * @param		<code>true</code> to create
     *			a new session for this request; 
     *			<code>false</code> to return the current
     *			session
     *			
     *
     * @return 		the <code>HttpSession</code> associated 
     *			with this request or <code>null</code> if
     * 			<code>create</code> is <code>false</code>
     *			and the request has no valid session
     *
     *
     *
     */

    public HttpSession getSession (boolean create);
    
    
    
   

    /**
     *
     * Returns the current session associated with this request,
     * or if the request does not have a session, creates one.
     * 
     * @return		the <code>HttpSession</code> associated
     *			with this request
     *
     */

    public HttpSession getSession();
    
    
    
    
    

    /**
     *
     * Checks whether this request has a valid session in the
     * current session context (which is a
     * {@link HttpSessionContext}). 
     * If the session is not valid,
     * the {@link #getSession} method never returns it.
     *
     * @return			<code>true</code> if this
     *				request has a valid session
     *				in the current session context;
     *				otherwise, <code>false</code>
     *
     * @see			#getRequestedSessionId
     * @see			#getSession
     * @see			HttpSessionContext
     *
     */

    public boolean isRequestedSessionIdValid ();
    
    
    

    /**
     *
     * Checks whether the session ID this request submitted
     * came in as a cookie, rather than from the {@link #getSession}
     * method.
     *
     * @return			<code>true</code> if the session ID
     *				came in as a
     *				cookie; otherwise, <code>false</code>
     *
     *
     * @see			#getSession
     *
     */ 

    public boolean isRequestedSessionIdFromCookie ();
    
    
    

    /**
     *
     * Checks whether the session ID this request submitted
     * came in as part of the request URL, rather than
     * from the {@link #getSession} method.
     *
     * @return			<code>true</code> if the session ID
     *				came in as part of an URL; otherwise,
     *				<code>false</code>
     *
     *
     * @see			#getSession
     *
     */
    
    public boolean isRequestedSessionIdFromURL();
    
    
    
    
    
    /**
     *
     * @deprecated		As of Version 2.1 of the Java Servlet
     *				API, use {@link #isRequestedSessionIdFromURL}
     *				instead.
     *
     */

    public boolean isRequestedSessionIdFromUrl ();


    
}
