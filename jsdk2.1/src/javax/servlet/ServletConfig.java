/*
 * $Id: ServletConfig.java,v 1.4 1999/04/20 20:37:35 sahmed Exp $
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

import java.util.Enumeration;



/**
 * 
 * Defines a servlet configuration object, which a servlet engine
 * uses to pass information
 * to a servlet in order to initialize the servlet. 
 *
 * <p>The configuration information contains initialization parameters,
 * which are a set of name/value pairs, and a {@link ServletContext} object,
 * which gives the servlet information about the server.
 *
 * @author 	Various
 * @version 	$Version$
 *
 * @see 	ServletContext
 *
 */
 
public interface ServletConfig {




    /**
     * Returns the {@link ServletContext} object that the server
     * has passed to this servlet. The <code>ServletContext</code> 
     * object is part of the <code>ServletConfig</code> object this 
     * interface defines.
     *
     *
     * @return		a {@link ServletContext} object, which
     *			gives the servlet information about how
     *			to interact with the server
     * 
     * @see		ServletContext
     *
     */

    public ServletContext getServletContext();
    
    
    
    

    /**
     * Returns a <code>String</code> containing the value of the 
     * named initialization parameter, or <code>null</code> if 
     * the parameter does not exist.
     *
     * <p>The value of an initialization parameter is a single
     * <code>String</code>, which you must interpret.
     *
     *
     * @param name	a <code>String</code> containing the name
     *			of the parameter whose value is requested
     *
     * @return		a <code>String</code> representing the value 
     *			of the parameter
     *
     */

    public String getInitParameter(String name);
    
    
    

    /**
     * Returns the names of the servlet's initialization parameters
     * as an <code>Enumeration</code> of <code>String</code> objects, 
     * or an empty <code>Enumeration</code> if the servlet has
     * no initialization parameters.
     *
     * @return		an <code>Enumeration</code> of <code>String</code> 
     *			objects containing the names of the servlet's 
     *			initialization parameters
     *
     *
     *
     */

    public Enumeration getInitParameterNames();
    
}








