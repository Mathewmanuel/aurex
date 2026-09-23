/*
 * $Id: HttpSessionBindingListener.java,v 1.2 1999/04/20 20:37:51 sahmed Exp $
 * 
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
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

import java.util.EventListener;


 
 

/**
 * Causes an object to be notified when it is bound to
 * or unbound from a session. The object is notified
 * by an {@link HttpSessionBindingEvent} object.
 *
 *
 * @author		Various
 * @version		$Version$
 *
 * @see HttpSession
 * @see HttpSessionBindingEvent
 *
 */

public interface HttpSessionBindingListener extends EventListener {



    /**
     *
     * Notifies the object that is being bound to
     * a session and identifies the session.
     *
     * @param event		the event that identifies the
     *				session 
     *
     * @see #valueUnbound
     *
     */ 

    public void valueBound (HttpSessionBindingEvent event);
    
    

    /**
     *
     * Notifies the object that is being unbound
     * from a session and identifies the session.
     *
     * @param event		the event that identifies
     *				the session 
     *	
     * @see #valueBound
     *
     */

    public void valueUnbound (HttpSessionBindingEvent event);
    
    
}

