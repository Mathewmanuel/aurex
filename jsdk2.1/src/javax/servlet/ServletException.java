/*
 * $Id: ServletException.java,v 1.3 1999/04/20 20:37:37 sahmed Exp $
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


/**
 * Defines a general exception a servlet throws when it
 * encounters difficulty.
 *
 * @author 	Various
 * @version 	$Version$
 *
 */


public class ServletException extends Exception {

    private Throwable rootCause;





    /**
     * Constructs a new servlet exception.
     *
     */

    public ServletException() {
	super();
    }
    
   

    

    /**
     * Constructs a new servlet exception with the
     * message you specify. The message can be written 
     * to the server log file or displayed for the user. 
     *
     * @param message 		a <code>String</code> 
     *				specifying the text of 
     *				the exception message
     *
     */

    public ServletException(String message) {
	super(message);
    }
    
   
   
    

    /**
     * Constructs a new servlet exception when the servlet 
     * needs to throw an exception and include a message 
     * about another exception that interfered with its 
     * normal operation.
     *
     *
     * @param message 		a <code>String</code> containing 
     *				the text of the exception message
     *
     * @param rootCause		the <code>Throwable</code> exception 
     *				that interfered with the servlet's
     *				normal operation, making this servlet
     *				exception necessary
     *
     */
    
    public ServletException(String message, Throwable rootCause) {
	super(message);
	this.rootCause = rootCause;
    }





    /**
     * Constructs a new servlet exception as
     * a result of a system exception and uses the localized
     * system exception message.
     *
     * <p>The system exception is a <code>Throwable</code> exception.
     * This method calls the <code>getLocalizedMessage</code> method
     * on the <code>Throwable</code> exception to get a localized exception
     * message. If you subclass <code>ServletException</code>, you can
     * override this method to create an exception message designed for
     * a specific locale.
     *
     * @param rootCause 	the system exception that 
     * 				makes this servlet exception necessary
     *
     */

    public ServletException(Throwable rootCause) {
	super(rootCause.getLocalizedMessage());
	this.rootCause = rootCause;
    }
  
  
 
 
    
    /**
     * Returns the <code>Throwable</code> system exception 
     * that makes this servlet exception necessary. 
     *
     * <p>You can use any of the methods defined
     * in <code>java.lang.Throwable</code> on the <code>Throwable</code> object,
     * including <code>getMessage</code> to get a text message about the
     * exception, <code>getLocalizedMessage</code> to get a localized 
     * exception message, and <code>printStackTrace</code>.
     *
     *
     * @return			the <code>Throwable</code> system exception 
     *				that makes this servlet exception necessary
     *
     */
    
    public Throwable getRootCause() {
	return rootCause;
    }
}





