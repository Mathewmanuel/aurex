/**
 * This is the interface that a JSP processor-generated class for the
 * HTTP protocol must satisfy. 
 */
package javax.servlet.jsp;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public interface HttpJspPage extends JSPPage {

    /**
     * jspPageService() corresponds to the body of the JSP page. This
     * method is defined automatically by the JSP processor and should 
     * never be defined by the JSP author. 
     */
    void _jspService(HttpServletRequest request,
		     HttpServletResponse response)
	throws ServletException, IOException;

}
