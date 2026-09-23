/*
 * $Id: ServletResponse.java,v 1.5 1999/04/20 22:00:42 duncan Exp $
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * Sends MIME-encoded data from the servlet to the client. The servlet
 * engine creates a <code>ServletResponse</code> object and passes it 
 * as an argument to the servlet's <code>service</code> method.
 *
 * <p>To send binary data in a MIME body response, use
 * the {@link ServletOutputStream} returned by {@link #getOutputStream}.
 * Likewise, to send text data, use the <code>PrintWriter</code> object 
 * returned by {@link #getWriter}. If you need to mix binary and text data,
 * for example, if you are creating a multipart response, use a
 * <code>ServletOutputStream</code> to write the multipart headers, and
 * then use the headers to build your own text bodies.
 *
 * <p>If you do not specify a character set for the MIME body response
 * with {@link #setContentType}, the servlet engine will select one
 * and modify the content accordingly. Call <code>setContentType</code>
 * before you call <code>getWriter</code> or <code>getOutputStream</code>.
 * 
 * <p>See the Internet RFCs such as RFC 2045 at 
 * <a href="http://info.internet.isi.edu/in-notes/rfc/files/rfc2045.txt">RFC 2045</a> 
 * for more information on MIME. Protocols such as SMTP
 * and HTTP define profiles of MIME, and those standards
 * are still evolving.
 *
 * @author 	Various
 * @version 	$Version$
 *
 * @see		ServletOutputStream
 *
 */
 
public interface ServletResponse {


    
    /**
     * Returns the name of the character set encoding used for
     * the MIME body sent by this response.
     *
     * <p>The character encoding is either the one specified in
     * the content itself or another one the client understands.
     * If no character encoding has been assigned, it is implicitly
     * set to <i>text/plain</i>.
     *
     * <p>See RFC 2047 (http://ds.internic.net/rfc/rfc2045.txt)
     * for more information about character encoding and MIME.
     *
     * @return		a <code>String</code> specifying the
     *			name of the character set encoding, for
     *			example, <i>text/plain</i>
     *
     */
  
    public String getCharacterEncoding ();
    
    

    /**
     * Returns a {@link ServletOutputStream} suitable for writing binary 
     * data in the response. The servlet engine does not encode the
     * binary data.
     *
     * @return				a {@link ServletOutputStream} for writing binary data	
     *
     * @exception IllegalStateException if you have already called the <code>getWriter</code> method
     * 					for the same response
     *
     * @exception IOException 		if an input or output exception occurred
     *
     * @see 				#getWriter
     *
     */

    public ServletOutputStream getOutputStream() throws IOException;
    
    

    /**
     * Returns a <code>PrintWriter</code> object that you
     * can use to send character text to the client. 
     * The character encoding used is the one specified 
     * in the <code>charset=</code> property of the
     * {@link #setContentType} method, which you must call
     * <i>before</i> you call this method. 
     *
     * <p>If necessary, the MIME type of the response is 
     * modified to reflect the character encoding used.
     *
     * <p> You cannot use this method if you have already
     * called {@link #getOutputStream} for this 
     * <code>ServletResponse</code> object.
     *
     * 
     * @return 					a <code>PrintWriter</code> object that 
     *						can return text to the client 
     *
     * @exception UnsupportedEncodingException  if the character encoding specified in
     *						<code>setContentType</code> cannot be
     *						used
     *
     * @exception IllegalStateException    	if the <code>getOutputStream</code>
     * 						method has already been called for this 
     *						response object; in that case, you can't
     *						use this method
     *
     * @exception IOException   		if an input or output exception occurred
     *
     * @see 					#getOutputStream
     * @see 					#setContentType
     *
     */

    public PrintWriter getWriter() throws IOException;
    
    
    
    

    /**
     * Sets the length of the content the server returns
     * to the client. In HTTP servlets, this method sets the
     * HTTP Content-Length header.
     *
     *
     * @param len 	an integer specifying the length of the 
     * 			content being returned to the client; sets
     *			the Content-Length header
     *
     */

    public void setContentLength(int len);
    
    

    /**
     * Sets the content type of the response the server sends to
     * the client. The content type may include the type of character
     * encoding used, for example, <code>text/html; charset=ISO-8859-4</code>.
     *
     * <p>You can only use this method once, and you should call it
     * before you obtain a <code>PrintWriter</code> or 
     * {@link ServletOutputStream} object to return a response.
     *
     *
     * @param type 	a <code>String</code> specifying the MIME 
     *			type of the content
     *
     * @see 		#getOutputStream
     * @see 		#getWriter
     *
     */

    public void setContentType(String type);


}
