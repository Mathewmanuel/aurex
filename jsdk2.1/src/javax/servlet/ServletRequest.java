/*
 * $Id: ServletRequest.java,v 1.4 1999/04/20 20:37:40 sahmed Exp $
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

package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;



/**
 * Defines an object that a servlet engine uses to give
 * a servlet information about a client request. 
 *
 * <p>A <code>ServletRequest</code> object provides data, including
 * parameter name and values, attributes, and an input stream.
 * Interfaces that extend <code>ServletRequest</code> can provide
 * additional protocol-specific data (for example, HTTP data is
 * provided by {@link javax.servlet.http.HttpServletRequest}.
 * This interface and the interfaces that descend from
 * it provide the servlet's only access to this data.
 * 
 * <p>A servlet request is a Multipurpose Internet Mail Extension (MIME) 
 * body request, and the response is a MIME body response. MIME bodies
 * are either text or binary data. When they are text,including character
 * encodings, use the <code>getReader</code> method. When they are binary data, 
 * use <code>getInputStream</code>. Multipart MIME bodies are treated
 * as binary data.
 * 
 * @author 	Various
 * @version 	$Version$
 *
 * @see 	javax.servlet.http.HttpServletRequest
 *
 */

public interface ServletRequest {




    /**
     *
     * Returns the value of the named attribute as an <code>Object</code>. 
     * This method allows the servlet engine to give the servlet
     * custom information about a request. This method returns
     * <code>null</code> if no attribute of the given name exists.
     *
     * <p>Attribute names should follow the same conventions as package
     * names. This specification reserves names matching <code>java.*</code>,
     * <code>javax.*</code>, and <code>sun.*</code>. 
     *
     * @param name	a <code>String</code> specifying the name of 
     *			the attribute
     *
     * @return		an <code>Object</code> containing the value 
     *			of the attribute, or <code>null</code> if
     *			the attribute does not exist
     *
     */

    public Object getAttribute(String name);
    
    

    /**
     * Returns an <code>Enumeration</code> containing the
     * names of the attributes available to this request. 
     * This method returns an empty <code>Enumeration</code>
     * if the request has no attributes available to it.
     * 
     *
     * @return		an <code>Enumeration</code> of strings 
     *			containing the names 
     * 			of the request's attributes
     *
     */

    public Enumeration getAttributeNames();
    
    
    
    
    /**
     * Returns the name of the character encoding style used in this
     * request. This method returns <code>null</code> if the request
     * does not use character encoding. 
     * 
     *
     * @return		a <code>String</code> containing the name of 
     *			the chararacter encoding style, or <code>null</code>
     *			if the request does not use character encoding
     *
     */

    public String getCharacterEncoding ();
    
    
    
    
    /**
     * Returns the length, in bytes, of the content contained in the
     * request and sent by way of the input stream or -1 if the
     * length is not known. Same as the value
     * of the CGI variable CONTENT_LENGTH.
     *
     * @return		an integer containing the length of the content 
     * 			in the request or -1 if the length is not known
     *
     */

    public int getContentLength();
    
    
    

    /**
     * Returns the MIME type of the content of the request, or 
     * <code>null</code> if the type is not known. Same as the value
     * of the CGI variable CONTENT_TYPE.
     *
     * @return		a <code>String</code> containing the name 
     *			of the MIME type of 
     * 			the request, or -1 if the type is not known
     *
     */

    public String getContentType();
    
    
    

    /**
     * Retrieves binary data from the body of the request as 
     * a {@link ServletInputStream}, which
     * gives you the ability to read one line at a time.
     *
     * @return					a {@link ServletInputStream} object containing
     * 						the body of the request
     *
     * @exception IllegalStateException   	if the {@link #getReader} method
     * 						has already been called for this request
     *
     * @exception IOException    		if an input or output exception occurred
     *
     */

    public ServletInputStream getInputStream() throws IOException; 
     
    
    

    /**
     * Returns the value of a request parameter as a <code>String</code>,
     * or <code>null</code> if the parameter does not exist. Request parameters
     * are extra information sent with the request. 
     *
     * <p>You should only use this method when you are sure the
     * parameter has only one value. If the parameter might have
     * more than one value, use {@link #getParameterValues}.
     *
     * <p>If you use this method with a multivalued
     * parameter, the servlet engine determines the return value.
     *
     *
     * @param name 	a <code>String</code> specifying the 
     *			name of the parameter
     *
     * @return		a <code>String</code> representing the 
     *			single value of the parameter
     *
     * @see 		#getParameterValues
     *
     */

    public String getParameter(String name);
    
    
    

    /**
     *
     * Returns an <code>Enumeration</code> of <code>String</code>
     * objects containing the names of the parameters contained
     * in this request. If the request has 
     * no parameters or if the input stream is empty, returns an 
     * empty <code>Enumeration</code>. The input stream is empty 
     * when all the data returned by {@link #getInputStream} has 
     * been read.
     *
     * @return		an <code>Enumeration</code> of <code>String</code>
     *			objects, each <code>String</code> containing
     * 			the name of a request parameter; or an 
     *			empty <code>Enumeration</code> if the
     *			request has no parameters
     *
     */
     
    public Enumeration getParameterNames();
    
    
    

    /**
     * Returns an array of <code>String</code> objects containing 
     * all of the values the
     * given request parameter has, or <code>null</code> if the 
     * parameter does not exist. For example, in an HTTP servlet, 
     * this method returns an array of <code>String</code> objects 
     * containing the values of a query string or posted form.
     *
     * <p>If the parameter has a single value, the array has a length
     * of 1.
     *
     * @param name	a <code>String</code> containing the name of 
     *			the parameter whose value is requested
     *
     * @return		an array of <code>String</code> objects 
     *			containing the parameter's values
     *
     * @see		#getParameter
     *
     */

    public String[] getParameterValues(String name);
    
    
    

    /**
     * Returns the name and version of the protocol the request uses
     * in the form <i>protocol/majorVersion.minorVersion</i>, for 
     * example, HTTP/1.1. The value
     * returned is the same as the value of the CGI variable 
     * <code>SERVER_PROTOCOL</code>.
     *
     * @return		a <code>String</code> containing the protocol 
     *			name and version number
     *
     */
    
    public String getProtocol();
    
    
    

    /**
     * Returns the name of the scheme used to make this request, 
     * for example,
     * <code>http</code>, <code>https</code>, or <code>ftp</code>.
     * Different schemes have different rules for constructing URLs,
     * as noted in RFC 1738.
     *
     * <p>You can reconstruct the URL used to make this request by 
     * using this scheme, the server name and port, the pathname to
     * the Web page on the server (also known as the Universal 
     * Resource Identifier), and the query string..
     *
     * @return		a <code>String</code> containing the name 
     *			of the scheme used to make this request
     *
     */

    public String getScheme();
    
    
    

    /**
     * Returns the host name of the server that received the request.
     * Same as the value of the CGI variable <code>SERVER_NAME</code>.
     *
     * @return		a <code>String</code> containing the name 
     *			of the server to which the request was sent
     */

    public String getServerName();
    
    
    

    /**
     * Returns the port number on which this request was received.
     * Same as the value of the CGI variable <code>SERVER_PORT</code>.
     *
     * @return		an integer specifying the port number
     *
     */

    public int getServerPort();
    
    
    

    /**
     * Returns the body of the request as a <code>BufferedReader</code>
     * that translates character set encodings.
     * 
     *
     * @return					a <code>BufferedReader</code>
     *						containing the body of the request	
     *
     * @exception UnsupportedEncodingException 	if the character set encoding
     * 						used is not supported and the 
     *						text cannot be decoded
     *
     * @exception IllegalStateException   	if {@link #getInputStream} method
     * 						has been called on this request
     *
     * @exception IOException  			if an input or output exception occurred
     *
     * @see 					#getInputStream
     *
     */

    public BufferedReader getReader () throws IOException;
    
    
    

    /**
     * Returns the Internet Protocol (IP) address of the client 
     * that sent the request.
     * Same as the value of the CGI variable <code>REMOTE_ADDR</code>.
     *
     * @return		a <code>String</code> containing the 
     *			IP address of the client that sent the request
     *
     */
    
    public String getRemoteAddr();
    
    
    

    /**
     * Returns the fully qualified name of the client that sent the
     * request. Same as the value of the CGI variable <code>REMOTE_HOST</code>.
     *
     * @return		a <code>String</code> containing the fully qualified name 
     *			of the client
     *
     */

    public String getRemoteHost();
    
    
    

    /**
     *
     * Stores an attribute in the context of this request.
     * Attributes are reset between requests.
     *
     * <p>Attribute names should follow the same conventions as
     * package names. Names beginning with <code>java.*</code>,
     * <code>javax.*</code>, and <code>com.sun.*</code>, are
     * reserved for use by Sun Microsystems.
     *
     *
     * @param key			a <code>String</code> specifying 
     *					the name of the attribute
     *
     * @param o				an <code>Object</code> containing 
     *					the context of the request
     *
     * @exception IllegalStateException	if the specified attribute already has a value
     *
     */

    public void setAttribute(String key, Object o);
    
    
    

    /**
     * 
     * @deprecated 	As of Version 2.1 of the Java Servlet API,
     * 			use {@link ServletContext#getRealPath} instead.
     *
     */

    public String getRealPath(String path);
    
    
}

