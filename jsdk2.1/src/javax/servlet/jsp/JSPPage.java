/**
 * This is the interface that a JSP processor-generated class must
 * satisfy.
 */

package javax.servlet.jsp;

import javax.servlet.*;

public interface JSPPage extends Servlet {
    /**
     * Methods that can be defined by the JSP author
     * either directly (via a declaration) or via an event handler
     * (in JSP 1.1).
     */

    /**
     * jspInit() is invoked when the JSPPage is initialized.
     * At this point getServletConfig() will return the desired value.
     */
    void jspInit();
    

    /**
     * jspDestroy() is invoked when the JSPPage is about to be destroyed
     */
    void jspDestroy();
}

