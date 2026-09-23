
/*
 * $Id: HttpSession.java,v 1.4 1999/04/20 20:37:49 sahmed Exp $
 * 
 * Copyright (c) 1997-1999 Sun Microsystems, Inc. All Rights Reserved.
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
 */

package javax.servlet.http;

/**
 *
 * Provides a way to identify a user across more than one page
 * request or visit to a Web site.
 *
 * <p>The servlet engine uses this interface to create a session
 * between an HTTP client and an HTTP server. The session persists
 * for a specified time period, across more than one connection or
 * page request from the user. A session usually corresponds to one 
 * user, who may visit a site many times. The server can maintain a 
 * session either by using cookies or by rewriting URLs.
 *
 * <p>This interface allows servlets to 
 * <ul>
 * <li>View and manipulate information about a session, such as
 * the session identifier, creation time, or context
 * <li>Bind objects to sessions, allowing you to use an
 * online shopping cart
 * to hold data that persists across multiple user connections
 * </ul>
 *
 * <p> <code>HttpSession</code> defines methods that
 * store these types of data:
 * <ul>
 * <li>Standard session properties, such as a session identifier
 * or session context
 * <li>Data that the application provides, accessed using this
 * interface and stored using a dictionary-like interface
 * </ul>
 *
 * <p>The servlet can obtain data from the session, modify it,
 * and return it to the session, as this example shows:
 *
 * <code>
 * //Get the session object - "request" represents the HTTP servlet request
 * HttpSession session = request.getSession(true);
 *
 * //Get the session data value - an Integer object is read from 
 * //the session, incremented, then written back to the session.
 * //sessiontest.counter identifies values in the session
 * Integer ival = (Integer) session.getValue("sessiontest.counter");
 * if (ival==null) 
 *    ival = new Integer(1);
 * else 
 *   ival = new Integer(ival.intValue() + 1);
 * session.putValue("sessiontest.counter", ival);
 * </code>
 *
 * <p>When an application stores an object in or removes it from a
 * session, the session checks whether the object implements
 * {@link HttpSessionBindingListener}. If it does, 
 * the servlet notifies the object that it has been bound to or unbound 
 * from the session.
 * 
 * <p>An HTTP session represents the server's view of the session.
 * The server considers a session new under any of these conditions:
 * <ul>
 * <li>The client does not yet know about the session
 * <li>The session has not yet begun
 * <li>The client chooses not to join the session, for example,
 * if the server supports only cookies and the client rejects the
 * cookies the server sends
 * </ul>
 * When the session is new, the {@link #isNew} method 
 * returns <code>true</code>.
 *
 * <p>You must write your servlet to handle cases in which
 * the client has not joined a session. The following code
 * example calls <code>isNew</code> to determine whether
 * a session is new. If it is, the server requires the client
 * to start a session by directing the client to a welcome
 * page which the user must enter before gaining access to
 * other pages on the site.
 *
 * <code>
 * //Get the session object - "request" represents the HTTP servlet request
 * HttpSession session = request.getSession(true);
 *
 * //insist that the client starts a session
 * //before access to data is allowed
 * //"response" represents the HTTP servlet response
 * if (session.isNew()) {
 *   response.sendRedirect (welcomeURL);
 * }
 * </code>
 *
 *
 * @author	Various
 * @version	$Version$
 *
 *
 * @see 	HttpSessionBindingListener
 * @see 	HttpSessionContext
 *
 */

public interface HttpSession {




    /**
     *
     * Returns the time when this session was created, measured
     * in milliseconds since midnight January 1, 1970 GMT.
     *
     * @return					a <code>long</code> integer specifying
     * 						when this session was created relative
     *						to 1-1-1970 GMT
     *
     * @exception IllegalStateException		if you attempt to get the session's
     *						creation time after the session has
     *						been invalidated
     *
     */

    public long getCreationTime ();
    
    
    
    
    /**
     *
     * Returns a string containing the unique identifier assigned 
     * to this session. The identifier is assigned 
     * by the servlet engine and is implementation dependent.
     * 
     * @return				a string specifying the identifier
     *					assigned to this session
     *
     * @exeption IllegalStateException	if the session is invalid
     *
     */

    public String getId ();
    
    
    

    /**
     *
     * Returns the last time the client sent a request associated with
     * this session, as the number of milliseconds since midnight
     * January 1, 1970 GMT. 
     *
     * <p>Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * <p>The last accessed time can help you manage sessions. For example,
     * the sessions can be sorted according to age to optimize some task.
     *
     * @return					a <code>long</code> integer 
     *						representing the last time 
     *						the client sent a request associated 
     *						with this session, expressed in 
     *						milliseconds since 1-1-1970 GMT
     *
     * @exception IllegalStateException		if the session is invalid
     *
     */

    public long getLastAccessedTime ();
    
    
    
   /**
    * Returns the maximum time interval, in seconds, that 
    * the servlet engine will keep this session open between 
    * client requests. You can set the maximum time interval
    * with the <code>setMaxInactiveInterval</code> method.
    *
    *  
    *
    * @return		an integer specifying the number of
    *			seconds this session remains open
    *			between client requests
    *
    * @see		#setMaxInactiveInterval
    *
    *
    */
    

    public int getMaxInactiveInterval();
    
    

   /**
    *
    * @deprecated 	As of Version 2.1, this method is
    *			deprecated and has no replacement.
    *			It will be removed in a future
    *			version of the Java Servlet API.
    *
    */

    public HttpSessionContext getSessionContext ();
    
    
    
    
    /**
     *
     * Returns the object bound with the specified name
     * in this session or null if no object of
     * that name exists.
     *
     * @param name				a string specifying the name of the object
     *
     * @return					the object with the specified name
     *
     * @exception IllegalStateException		if the session is invalid
     *
     */
  
    public Object getValue (String name);
    
    
    

    /**
     *
     * Returns an array containing the names of all the objects
     * bound to this session. This method is useful, for example,
     * when you want to delete all the objects bound to this
     * session.
     *
     * @return					an array of strings specifying the
     *						names of all the objects bound to
     *						this session
     *
     * @exception IllegalStateException		if the session is invalid
     *
     */
    
    public String [] getValueNames();
    
    
    

    /**
     *
     * Invalidates this session and unbinds any objects bound
     * to it.
     *
     * @exception IllegalStateException		if the session is already invalid
     *
     */

    public void invalidate ();
    
    
    
    
    /**
     *
     * Returns <code>true</code> if the Web server has created a session
     * but the client has not yet joined. 
     * For example, if the server used only cookie-based sessions, and
     * the client had disabled the use of cookies, then a session would
     * be new.
     *
     * @return 					<code>true</code> if the 
     *						server has created a session, 
     *						but the client has not yet joined
     *
     * @exception IllegalStateException		if the session is invalid
     *
     *
     */

    public boolean isNew ();
    



    
    /**
     * Binds an object to this session, using the name specified.
     * If an object of the same name is already bound to the session,
     * the object is replaced.
     *
     *
     * @param name				the name to which the object is bound;
     *						cannot be null
     *
     * @param value				the object to be bound; cannot be null
     *
     * @exception IllegalStateException		if the session is invalid
     *
     */
 
    public void putValue (String name, Object value);





    /**
     *
     * Removes the object bound with the specified name from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * <p>After this method executes, and if the object
     * implements <code>HttpSessionBindingListener</code>,
     * the object calls 
     * <code>HttpSessionBindingListener.valueUnbound</code>.
     * 
     * 
     *
     * @param name				the name of the object to
     *						remove from this session
     *
     * @exception IllegalStateException		if the session is invalid
     *
     */

    public void removeValue (String name);
    
    
    

    /**
     *
     * Specifies the maximum length of time, in seconds, that the
     * servlet engine keeps this session if no user requests
     * have been made of the session.
     *
     * @param interval		An integer specifying the number
     * 				of seconds 
     *
     */
    
    public void setMaxInactiveInterval(int interval);

}

