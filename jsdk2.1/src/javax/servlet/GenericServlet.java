/*
 * $Id: GenericServlet.java,v 1.6 1999/04/20 20:36:52 sahmed Exp $
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
 */

package javax.servlet;

import java.io.IOException;
import java.util.Enumeration;

/**
 *
 * Defines a generic, protocol-independent
 * servlet. To write an HTTP servlet to use with
 * a Web site, you must extend {@link javax.servlet.http.HttpServlet}.
 *
 * <p><code>GenericServlet</code> implements the <code>Servlet</code>
 * and <code>ServletConfig</code> interfaces. When you write a servlet, 
 * you usually extend <code>GenericServlet</code> or its subclass 
 * <code>HttpServlet</code>, unless the servlet needs another superclass. 
 * If a servlet needs to extend a class other than <code>GenericServlet</code> 
 * or <code>HttpServlet</code>, the servlet must implement the <code>Servlet</code>
 * interface directly.
 *
 * <p><code>GenericServlet</code> makes writing servlets
 * easier. It provides simple versions of the lifecycle methods 
 * <code>init</code> and <code>destroy</code> and of the methods 
 * in the <code>ServletConfig</code> interface. <code>GenericServlet</code>
 * also implements the <code>log></code> method, declared in the
 * <code>ServletContext</code> interface. 
 *
 * <p>To write a generic servlet, you need only
 * override the <code>service</code> method, which is declared as
 * an abstract method with no body. If you are writing a servlet
 * engine, you should override <code>getServletInfo</code> and specialize
 * the <code>init</code> and <code>destroy</code> methods if 
 * the engine will manage expensive servlet-wide
 * resources. 
 *
 *
 * @author 	Various
 * @version 	$Version$
 *
 *
 *
 */

 
public abstract class GenericServlet 
    implements Servlet, ServletConfig, java.io.Serializable
{

    private transient ServletConfig config;
    

    /**
     *
     * Does nothing, because this
     * is an abstract class. All of the servlet initialization
     * is done by one of the <code>init</code> methods.
     *
     */

    public GenericServlet () { }
    
    
    
   /**
     * Destroys the servlet, cleaning up whatever resources are being
     * held. 
     *
     * <p>The servlet engine calls this method once, 
     * automatically, when it removes the servlet. After the engine
     * calls <code>destroy</code>, it cannot call <code>destroy</code>
     * again on this instance of the servlet.
     *
     * <p>The engine calls <code>destroy</code> after all
     * calls to the <code>service</code> method have completed
     * or a specified amount of time has passed, whichever occurs
     * first. In the latter case, the <code>service</code>
     * method might stil be servicing requests from other threads.
     * When you write your servlet, make sure that any
     * threads still running in the <code>service</code> method 
     * complete before the servlet is destroyed.
     * 
     */

    public void destroy() {
	log("destroy");
    }
    
    
    
    /**
     * Returns a <code>String</code> containing the value of the named
     * initialization parameter. If the servlet does not have
     * a parameter of the specified name, this method returns
     * <code>null</code>.
     *
     * <p>An initialization parameter has a single <code>String</code>
     * value, which you must interpret. 
     *
     * <p>This method is supplied for convenience. It gets the 
     * value of the named parameter from the <code>ServletConfig</code> 
     * object, which is passed
     * to the servlet by the <code>init</code> method.
     *
     * @param name 		a <code>String</code> specifying the name 
     *				of the initialization parameter
     *
     * @return String 		a <code>String</code> containing the value
     *				of the initalization parameter
     *
     */ 

    public String getInitParameter(String name) {
	return getServletConfig().getInitParameter(name);
    }
    
    

   /**
    * Returns the names of the initialization parameters for this
    * servlet as an enumeration of <code>String</code> objects. 
    *
    * <p>If the servlet has no initialization paramaters, 
    * this method returns an empty enumeration.
    * 
    * <p>This method is supplied for convenience. It gets the 
    * parameter names from the <code>ServletConfig</code> object, 
    * which the <code>init</code> method passes to the servlet. 
    *
    *
    * @return Enumeration 	an enumeration of <code>String</code>
    *				objects containing the names of 
    *				the servlet's initialization parameters
    *
    */

    public Enumeration getInitParameterNames() {
	return getServletConfig().getInitParameterNames();
    }   
    
     
 
     

    /**
     * Returns a {@link ServletConfig} object, which gives
     * a servlet its initialization parameters. The initialization 
     * parameters supply the initial or default values the 
     * servlet runs with.
     *
     * @return ServletConfig 	the <code>ServletConfig</code> object
     *				that initialized this servlet
     *
     */
    
    public ServletConfig getServletConfig() {
	return config;
    }
    
    
 
    
    /**
     * Returns a {@link ServletContext} object, which contains 
     * information about the servlet engine on which the servlet 
     * is running.
     *
     * <p>This method is supplied for convenience. The 
     * <code>ServletContext</code> object is contained within the 
     * <code>ServletConfig</code> object, which is passed to the 
     * servlet by the <code>init</code> method when the servlet is 
     * initialized.
     *
     *
     * @return ServletContext 	the <code>ServletContext</code> object
     *				passed to this servlet by the <code>init</code>
     *				method
     *
     */

    public ServletContext getServletContext() {
	return getServletConfig().getServletContext();
    }



 

    /**
     * Returns a <code>String</code> that contains information about 
     * the servlet such as its author, version, and copyright information. 
     * You must override this method before it returns this information. 
     * If you do not override this method, it returns an empty string.
     *
     *
     * @return String 		a empty <code>String</code> until
     *				you override this method
     *
     */
    
    public String getServletInfo() {
	return "";
    }




    /**
     *
     * Initializes this servlet.
     *
     * <p>The servlet engine calls this method once,
     * automatically, each time it loads the servlet. This
     * method is guaranteed to finish before the servlet accepts any
     * requests to its <code>service</code> method. If a fatal
     * error occurs while the servlet is being initialized, 
     * the servlet engine should throw
     * an <code>UnavailableException</code>, rather than
     * calling the <code>System.exit</code> method.
     *
     * <p>The <code>init</code> method stores the 
     * {@link ServletConfig}
     * object it receives from the servlet engine. If you override <code>init</code>,
     * you should either call <code>super.init</code> 
     * or store the <code>ServletConfig</code> object in the new 
     * <code>init</code> method. If you decide to store the
     * <code>ServletConfig</code> object in a different location,
     * you should also override the {@link #getServletConfig}
     * method.
     *
     * @param config 			the <code>ServletConfig</code> object
     *					that contains initialization parameters
     *					for this servlet
     *
     * @exception ServletException 	if an exception occurs that
     *					interrupts the servlet's normal
     *					operation
     *
     * 
     * @see 				UnavailableException
     *
     */

    public void init(ServletConfig config) throws ServletException {
	this.config = config;
	log("init");
	this.init();
    }





    /**
     *
     * Acts as a convenience method, so that you do not have to
     * store a {@link ServletConfig} object to use as a
     * parameter.
     *
     * <p>If you extend <code>GenericServlet</code>, simply override
     * this method and it will be called by
     * <code>GenericServlet.init(ServletConfig config)</code>.
     *
     * @exception ServletException 	if an exception occurs that
     *					interrupts the servlet's
     *					normal operation
     *
     */
    
    public void init() throws ServletException {

    }
    



    /**
     * 
     * Writes the servlet class name and a servlet
     * exception message to the servlet log file. You should 
     * override this method if the servlet has more than one 
     * instance (for example, if the servlet engine runs the 
     * servlet for multiple virtual hosts). The specialized 
     * method should log the message, along with an instance j
     * identifier and perhaps a thread identifier. 
     *
     * <p>The default message prefix, which is the servlet
     * class name, does not allow the servlet log entries
     * to be distinguished from one another.
     *
     * <p>The servlet log file is an event log file whose 
     * name is specific to the server.
     *
     * @param msg 	a <code>String</code> specifying
     *			a servlet exception message 
     *
     */
     
    public void log(String msg) {
	getServletContext().log(getClass().getName() + ": "+ msg);
    }
   
   
   
   
    /**
     * Writes a system exception message to the servlet log file.
     * If a system exception occurs, this method adds the exception's
     * class, name, and message to the log file.
     *
     *
     *
     * @param message 		a <code>String</code> containing a 
     *				description of a system exception
     *
     * @param t			an exception of type 
     *				<code>java.lang.Throwable</code>
     *
     *
     */
   
    public void log(String message, Throwable t) {
	getServletContext().log(getClass().getName() + ": " + message, t);
    }
    
    
    
    /**
     * 
     * Carries out a single request from the client. 
     *
     * <p>Requests sent to this method are handled
     * after servlet initialization is complete. If any requests
     * are received while the servlet is being initializaed, they
     * are blocked. 
     *
     * <p>The {@link ServletRequest} object the client passes
     * to this method contains parameters the client provides, as
     * well as an input stream that gives the servlet data.
     * The {@link ServletResponse} object contains an output
     * stream that the servlet can use to return information to the
     * client.
     *
     * <p>Servlets typically run inside multithreaded servlet engines,
     * which can handle multiple <code>service</code> requests concurrently.
     * Therefore, you must synchronize access to any shared resources 
     * such as database or network connections. The simplest way to do 
     * this is to synchronize the entire <code>service</code> call. 
     * This can have a major performance impact, however, and should be
     * avoided whenever possible. For more information on synchronization, 
     * see the 
     * <a href="http://java.sun.com/Series/Tutorial/java/threads/multithreaded.html">
     * Java tutorial on multithreaded programming</a>.
     *
     *
     *
     * @param req 	the <code>ServletRequest</code> object
     *			that contains the client's request
     *
     * @param res 	the <code>ServletResponse</code> object
     *			that will contain the servlet's response
     *
     * @exception ServletException 	if an exception that
     *					interfered with the servlet's
     *					normal operation occurred
     *
     * @exception IOException 		if an input or output
     *					exception occurred
     *
     */

    public abstract void service(ServletRequest req, ServletResponse res)
	throws ServletException, IOException;
    
 
}
