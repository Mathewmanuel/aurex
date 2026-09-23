/*
 * $Id: ServletContext.java,v 1.7 1999/04/20 21:56:34 duncan Exp $
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

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;


/**
 * 
 * Defines a set of methods that a servlet uses to communicate
 * with a servlet engine, for example, to get the MIME type of a file, 
 * locate other servlets running on the server, or
 * write to a servlet log file.
 *
 * <p>The servlet engine talks to the servlet by returning
 * a <code>ServletContext</code> object (defined by this interface)
 * that gives servlets information about their environment. Servlets use the
 * {@link ServletConfig#getServletContext} method to get
 * the <code>ServletContext</code> object.
 *
 * <p>If the server supports
 * multiple or virtual hosts, the <code>ServletContext</code> object
 * must be at least as unique as the host. Servlet engines can also 
 * create <code>ServletContext</code> objects
 * that are unique to a group of servlets
 * and are tied to a specific part of the host's URL namespace.
 * You can assign this grouping administratively or define it in
 * a deployment descriptor file.
 *
 * <p>The <code>ServletContext</code> object is contained within 
 * the {@link ServletConfig} object, which the
 * Web server provides the
 * servlet when the servlet is initialized. You can access
 * the <code>ServletConfig</code> object by using the 
 * {@link Servlet#getServletConfig} method.
 *
 * @author 	Various
 * @version 	$Version$
 *
 * @see 	Servlet#getServletConfig
 * @see 	ServletConfig#getServletContext
 *
 */

public interface ServletContext {


    /**
     * Returns a <code>ServletContext</code> object that 
     * corresponds to a specified URL on the server.
     *
     * <p>This method allows servlets to gain
     * access to the resources located at a specified URL and obtain
     * {@link RequestDispatcher} objects from it.
     * 
     * <p>In security conscious environments, the servlet engine can
     * return <code>null</code> for a given URL.
     *       
     * @param uripath 	a <code>String</code> specifying the URL for 
     *			which you are requesting a <code>ServletContext</code>
     *			object
     *
     * @return		the <code>ServletContext</code> object that
     *			corresponds to the named URL
     *
     * @see 		RequestDispatcher
     *
     */

    public ServletContext getContext(String uripath);
    
    

    /**
     * Returns the major version of the Java Servlet API that this
     * Web server supports. All implementations that comply
     * with Version 2.1 must have this method
     * return the integer 2.
     *
     * @return 		2
     *
     */
    
    public int getMajorVersion();
    
    

    /**
     * Returns the MIME type of the specified file, or <code>null</code> if 
     * the MIME type is not known. The MIME type is determined
     * by the configuration of the servlet engine. Common MIME
     * types are <code>"text/html"</code> and <code>"image/gif"</code>.
     *
     *
     * @param 		a <code>String</code> specifying the name
     *			of the file whose MIME type you want
     *			to check 
     *
     * @return 		a <code>String</code> specifying the MIME type
     *
     */

    public String getMimeType(String file);
    
    

    /**
     * Returns the minor version of the Servlet API that this
     * Web server supports. All implementations that comply
     * with Version 2.1 must have this method
     * return the integer 1.
     *
     * @return 		1
     *
     */

    public int getMinorVersion();
    
    

    /**
     * Returns the resource that is mapped to a specified
     * path. The path must be in the form
     * <code>/dir/dir/file.ext</code>.
     *
     * <p>This method allows the Web
     * server to make a resource available to a servlet from
     * any source. Resources 
     * can be located on a local or remote
     * file system, in a database, or on a remote network site.
     *
     * <p>This method can return <code>null</code>
     * if no resource is mapped to the pathname.
     *
     * <p>The servlet engine must implement the URL handlers
     * and <code>URLConnection</code> objects that are necessary
     * to access the resource.
     *
     * <p>This method has a different purpose than
     * <code>java.lang.Class.getResource</code>,
     * which looks up resources based on a class loader. This
     * method does not use class loaders.
     * 
     * @param path 				a <code>String</code> specifying
     *						the path to the resource,
     * 						in the form <code>/dir/dir/file.ext</code>
     *
     * @return 					the resource located at the named path,
     * 						or <code>null</code> if there is no resource
     *						at that path
     *
     * @exception MalformedURLException 	if the pathname is not given in 
     * 						the correct form
     *
     */
    
    public URL getResource(String path) throws MalformedURLException;
    



    /**
     * Returns the resource located at the named path as
     * an <code>InputStream</code> object.
     *
     * <p>The data in the <code>InputStream</code> can be 
     * of any type or length. The path must be of 
     * the form <code>/dir/dir/file.ext</code>. This method 
     * returns <code>null</code> if no resource exists at
     * the specified path. 
     * 
     * <p>Metainformation such as content length and content type
     * that is available when you use the <code>getResource</code>
     * method is lost when you use this method.
     *
     * <p>The servlet engine must implement the URL handlers
     * and <code>URLConnection</code> objects necessary to access
     * the resource.
     *
     * <p>This method is different from 
     * <code>java.lang.Class.getResourceAsStream</code>,
     * which uses a class loader. This method allows servlet engines 
     * to make a resource available
     * to a servlet from any location, without using a class loader.
     * 
     *
     * @param name 	a <code>String</code> specifying the path
     *			to the resource,
     * 			in the form <code>/dir/dir/file.ext</code>
     *
     * @return 		the <code>InputStream</code> returned to the 
     *			servlet, or <code>null</code> if no resource
     *			exists at the specified path 
     *
     *
     */

    public InputStream getResourceAsStream(String path);
    
    



    /**
     * 
     * Returns a {@link RequestDispatcher} object that acts
     * as a wrapper for the resource located at the named path.
     * You can use a <code>RequestDispatcher</code> object to forward 
     * a request to the resource or include a resource in a response.
     *
     * <p>The pathname must be in the form <code>/dir/dir/file.ext</code>.
     * This method returns <code>null</code> if the <code>ServletContext</code>
     * cannot return a <code>RequestDispatcher</code>.
     *
     * <p>The servlet engine is responsible for wrapping the resource
     * with a <code>RequestDispatcher</code> object.
     *
     * @param urlpath 	a <code>String</code> specifying the pathname
     *			to the resource
     *
     * @return 		a <code>RequestDispatcher</code> object
     *			that acts as a wrapper for the resource
     *			at the path you specify
     *
     * @see 		RequestDispatcher
     *
     */

    public RequestDispatcher getRequestDispatcher(String urlpath);
    
    
    
    
    /**
     *
     * @deprecated	As of Java Servlet API 2.1, with no replacement.
     *
     * <p>This method was originally defined to retrieve a servlet
     * from a <code>ServletContext</code>. In this version, this method 
     * always returns <code>null</code> and remains only to preserve 
     * binary compatibility. This method will be permanently removed 
     * in a future version of the Java Servlet API.
     *
     */

    public Servlet getServlet(String name) throws ServletException;
    
  
  
  
    

    /**
     *
     *@deprecated	As of Java Servlet API 2.0, with no replacement.
     *
     * <p>This method was originally defined to return an <code>Enumeration</code>
     * of all the servlets known to this servlet context. In this
     * version, this method always returns an empty enumeration and
     * remains only to preserve binary compatibility. This method
     * will be permanently removed in a future version of the Java
     * Servlet API.
     *
     */
    
    public Enumeration getServlets();
    
    
    
    
    

    /**
     * @deprecated	As of Java Servlet API 2.1, with no replacement.
     *
     * <p>This method was originally defined to return an 
     * <code>Enumeration</code>
     * of all the servlet names known to this context. In this version,
     * this method always returns an empty <code>Enumeration</code> and 
     * remains only to preserve binary compatibility. This method will 
     * be permanently removed in a future version of the Java Servlet API.
     *
     */
 
    public Enumeration getServletNames();
    
  
  
    
    
    /**
     *
     * Writes the specified message to a servlet log file, which is usually
     * an event log. The message provides explanatory information about
     * an exception or error or an action the servlet engine takes. The name 
     * and type of the servlet log file is specific to the servlet engine.
     *
     *
     * @param msg 	a <code>String</code> specifying the explanatory
     *			message to be written to the log file
     *
     */
     
    public void log(String msg);
    
    
    
    

    /**
     * @deprecated	As of Java Servlet API 2.1, use
     * 			{@link log(String message, Throwable throwable)} 
     *			instead.
     *
     * <p>This method was originally defined to write an 
     * exception's stack trace and an explanatory error message
     * to the servlet log file.
     *
     */

    public void log(Exception exception, String msg);
    
    
    
    

    /**
     * Writes the stack trace and an explanatory message
     * for a given <code>Throwable</code> exception
     * to the servlet log file. The stack trace is
     * part of the <code>Throwable</code> object, and
     * the message is the one you specify in the <code>message</code>
     * parameter. The name and type of the servlet log file is specific to 
     * the servlet engine, but it is usually an event log.
     *
     *
     * @param message 		a <code>String</code> that 
     *				describes the error or exception
     *
     * @param throwable 	the <code>Throwable</code> error 
     *				or exception
     *
     */
    
    public void log(String message, Throwable throwable);
    
    
    
    
    
    /**
     * Returns a <code>String</code> containing the real path 
     * that corresponds to a virtual path. A virtual path contains 
     * a servlet name followed by the name of a file the servlet 
     * should act upon, in the form 
     * <code><i>/dir/dir/servlet/file.ext</i></code>.
     * In this form, <i>file.ext</i> is a filename used instead
     * of the path to the file. The servlet locates the file and 
     * translates the file name to the path that locates the file.
     *
     * <p>The real path the servlet returns is in a form
     * appropriate to the computer and operating system on
     * which the servlet engine is running, including the
     * proper path separators. This method returns <code>null</code>
     * if the servlet engine cannot translate the virtual path
     * to a real path for any reason.
     *
     *
     * @param path 	a <code>String</code> specifying a virtual path,
     *			in the form 
     *			<code><i>/dir/dir/servlet/file.ext</i></code>
     *
     *
     * @return 		a <code>String</code> specifying the real path,
     *			with path separators appropriate for the system
     *			on which the servlet engine is running
     *			
     *
     */

    public String getRealPath(String path);
    
    


    /**
     * Returns the name and version of the servlet engine on which
     * the servlet is running. 
     *
     * <p>The form of the returned string is <i>servername</i>/<i>versionnumber</i>.
     * For example, the Java Web Server can return the string
     * <code>Java Web Server/1.1.3</code>.
     *
     * <p>You can design the servlet engine to have this method return 
     * other optional information in parentheses after the primary string, 
     * for example,
     * <code>Java Web Server/1.1.3 (JDK 1.1.6; Windows NT 4.0 x86)</code>.
     *
     *
     * @return 		a <code>String</code> containing at least the 
     *			servlet engine name and version number
     *
     */

    public String getServerInfo();
    
    

    /**
     * Returns the servlet engine attribute with the given name, 
     * or <code>null</code>
     * if there is none. An attribute allows a servlet engine to give the
     * servlet additional information not
     * already provided by this interface. See your
     * Web server documentation for information about its attributes.
     *
     * <p>The attribute is returned as a <code>java.lang.Object</code>.
     * Attribute names should follow the same convention as package
     * names. The Java Servlet API specification reserves names
     * matching <code>java.*</code>, <code>javax.*</code>,
     * and <code>sun.*</code>.
     *
     * @param name 	a <code>String</code> specifying the name 
     *			of the attribute
     *
     * @return 		an <code>Object</code containing the value 
     *			of the attribute, or <code>null</code>
     *			if no attribute exists matching the given
     *			name
     *
     *
     */
  
    public Object getAttribute(String name);
    
    
    

    /**
     * Returns an <code>Enumeration</code> containing the 
     * attribute names available
     * within this servlet context. You can use the
     * {@link #getAttribute} method with an attribute name
     * to get the value of an attribute.
     *
     * @return 		an <code>Enumeration</code> of attribute 
     *			names
     *
     * @see		#getAttribute
     *
     */

    public Enumeration getAttributeNames();
    
    
    
    
    /**
     *
     * Gives an attribute a name in this servlet context. If
     * the name specified is already used for an attribute, this
     * method will overwrite the old attribute and bind the name
     * to the new attribute.
     *
     * <p>Attribute names should follow the same convention as package
     * names. The Java Servlet API specification reserves names
     * matching <code>java.*</code>, <code>javax.*</code>, and
     * <code>sun.*</code>.
     *
     *
     * @param name 	a <code>String</code> specifying the name 
     *			of the attribute
     *
     *
     * @param object 	an <code>Object</code> representing the
     *			attribute to be given the name
     *
     *
     *
     */
    
    public void setAttribute(String name, Object object);
    
    



    /**
     * Removes the attribute with the given name from 
     * the servlet context. If you remove an attribute, and 
     * then use {@link #getAttribute} to retrieve the 
     * attribute's value, <code>getAttribute</code> returns <code>null</code>.
     *
     *
     * @param name	a <code>String</code> specifying the name 
     * 			of the attribute to be removed
     *
     */

    public void removeAttribute(String name);
}


