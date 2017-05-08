package jp.georepublic.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProxyWFS
 */
@WebServlet(description = "Proxy Geoserver WFS Service", urlPatterns = { "/pwfs" })
public class ProxyWFS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProxyWFS() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, 
	        HttpServletResponse response) throws ServletException, IOException {

        ResourceBundle rb = ResourceBundle.getBundle("properties/ProxyWFS");
        String wfsUrl     = rb.getString("GEOSERVER_WFS_URL");

        OutputStream out = null;
        InputStream in = null;
        HttpURLConnection geoConn = null;
        OutputStreamWriter wr = null;

        try {

            final BufferedReader reader = request.getReader();
            final StringBuffer param    = new StringBuffer();
            
            String tmp = "";
            
            while ((tmp = reader.readLine()) != null) {
                param.append(tmp);
            }

            final URL geoURL = new URL( wfsUrl );
            
            geoConn = (HttpURLConnection) geoURL.openConnection();
            geoConn.setDoOutput(true);
            geoConn.setDoInput(true);
            geoConn.setRequestMethod( "POST" );

            HttpURLConnection.setFollowRedirects(false);
            
            geoConn.setInstanceFollowRedirects(false); 
            
            if( param.length() != 0 ) {
                geoConn.setRequestProperty("Content-Type", "text/xml");
            }
            
            wr = new OutputStreamWriter(geoConn.getOutputStream(), "UTF-8");

            // Writing the WFS XML Parameters into Geoserver
            if( param.length() != 0 ) {
                //XML Request
                wr.write( param.toString() ); 
            }
            else {
                //Request Parameter
               wr.write( request.getQueryString() );
            }

            wr.flush();
            
            in  = geoConn.getInputStream();
            out = response.getOutputStream();

            int wtmp = 0;

            while ((wtmp = in.read()) != -1) {

                out.write(wtmp);
            }


        }
        catch( Exception e ) {
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
