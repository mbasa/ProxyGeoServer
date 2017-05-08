package jp.georepublic.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class ProxyWMS
 */
@WebServlet(description = "Proxy Geoserver WMS Service", urlPatterns = { "/pwms" })
public class ProxyWMS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProxyWMS() {
        super();
    }

    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
     */
    protected void service(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException,IOException {

        ResourceBundle rb = ResourceBundle.getBundle("properties/ProxyWMS");
        String wmsUrl     = rb.getString("GEOSERVER_WMS_URL");
        String reqArr[]   = rb.getString("ALLOWED_REQUESTS").split(",");


        String paramName   = new String();
        String wmsRequest  = new String();
        String contentType = new String();

        final Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            paramName = params.nextElement();

            if (paramName.compareToIgnoreCase("request") == 0) {

                wmsRequest = request.getParameter(paramName);

            } else if (paramName.compareToIgnoreCase("format") == 0) {

                contentType = request.getParameter(paramName);
            }
        }

        ArrayList<String> reqList = new ArrayList<String>(Arrays.asList(reqArr));

        if( !reqList.contains(wmsRequest) ) 
            return;

        OutputStream out = null;
        InputStream in = null;
        HttpURLConnection geoConn = null;
        OutputStreamWriter wr = null;

        try {

            final URL geoURL = new URL( wmsUrl );

            geoConn = (HttpURLConnection) geoURL.openConnection();
            geoConn.setDoOutput(true);
            geoConn.setDoInput(true);
            geoConn.setRequestMethod( "POST" );

            HttpURLConnection.setFollowRedirects(false);
            geoConn.setInstanceFollowRedirects(false);

            wr = new OutputStreamWriter(geoConn.getOutputStream(), "UTF-8");

            // Writing the WMS Request Parameters into Geoserver
            wr.write( request.getQueryString() );
            wr.flush();

            in = geoConn.getInputStream();

            if (wmsRequest != null && contentType != null &&
                    (wmsRequest.equalsIgnoreCase("getmap") ||
                     wmsRequest.equalsIgnoreCase("getlegend"))) {

                // setting the Content Type to image/xxx
                response.setContentType(contentType);

            }

            out = response.getOutputStream();

            int tmp = 0;

            while ((tmp = in.read()) != -1) {
                out.write(tmp);
            }
        } 
        catch (final Exception e) {
            e.printStackTrace();
        } 
        finally {

            try {

                if (out != null) {
                    out.flush();
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

                if (wr != null) {
                    wr.close();
                }

                if (geoConn != null) {
                    geoConn.disconnect();
                }

            } 
            catch (final Exception e) {
                e.printStackTrace();
            }
        }	    
    }
}
