/*
 * $Id: RequestDispatcher.java,v 1.5 1999/04/20 20:37:32 sahmed Exp $
 * 
 * Copyright (c) 1998-1999 Sun Microsystems, Inc. All Rights Reserved.
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


/**
 * Defines an object that receives requests from the client
 * and sends them to any resource (such as a servlet, 
 * HTML file, or JSP file) on the server. The servlet
 * engine creates the <code>RequestDispatcher</code> object,
 * which is used as a wrapper around a server resource located
 * at a particular path.
 *
 * <p>This interface is intended to wrap servlets,
 * but a servlet engine can create <code>RequestDispatcher</code>
 * objects to wrap any type of resource.
 *
 * @author 	Various
 * @version 	$Version$
 *
 * @see 	ServletContext#getRequestDispatcher(java.lang.String)
 *
 */
 
public interface RequestDispatcher {





/**
 * Forwards a {@link ServletRequest} object from
 * this servlet to a resource (servlet, JSP file, or
 * HTML file) on the server. You can use this method
 * when one servlet does preliminary processing of
 * a request and lets another resource generate
 * the response.
 *
 * <p>The <code>ServletRequest</code> object has its
 * path and other parameters adjusted to be relative
 * to the path of the target resource.
 *
 * <p>You cannot use <code>forward</code> if the target
 * resource has already returned a <code>ServletOutputStream</code>
 * or <code>PrintWriter</code> object to the servlet.
 * In that situation, <code>forward</code> throws
 * an <code>IllegalStateException</code>.
 *
 *
 * @param request		a {@link ServletRequest} object
 *				that represents the request the client
 * 				makes of the servlet
 *
 * @param response		a {@link ServletResponse} object
 *				that represents the response the servlet
 *				returns to the client
 *
 * @exception ServletException	if the target resource is a servlet and
 *				throws an exception
 *
 * @exception IOException	if an input or output exception occurs
 *
 * @exception IllegalStateException	if the target resource
 *					returned a <code>ServletOutputStream</code>
 *					or <code>PrintWriter</code> object before
 *					this method was called
 *
 */

    public void forward(ServletRequest request, ServletResponse response)
	throws ServletException, IOException;




    /**
     *
     * Includes the content a resource (servlet, JSP page,
     * HTML file) on the Web server
     * generates in the response this servlet sends to
     * another servlet. In essence, this method enables 
     * programmatic server-side includes.
     *
     * <p>The {@link ServletResponse} object passed to this
     * servlet has the caller's request path. The response
     * object only has access to this servlet's 
     * <code>ServletOutputStream</code> and <code>PrintWriter</code>
     * objects.
     *
     * <p>A servlet that calls <code>include</code> cannot
     * set response headers. If the servlet that calls
     * <code>include</code> also calls methods that set
     * headers, <code>include</code> is not guaranteed
     * to work.
     * 
     *
     * @param request 			a {@link ServletRequest} object 
     *					that contains the client's request
     *
     * @param response 			a {@link ServletResponse} object 
     * 					that contains the servlet's response
     *
     * @exception ServletException 	if the target servlet throws a servlet
     *					exception
     *
     * @exception IOException 		if the servlet has already obtained
     *					a {@link ServletOutputStream} or
     *					print writer object from the
     *					<code>ServletResponse</code> object
     *
     *
     */
     
    public void include(ServletRequest request, ServletResponse response)
	throws ServletException, IOException;
}








