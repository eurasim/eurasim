package org.unikn.eurasim.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "POIRetriever", value = "/POIRetriever")
public class POIRetriever extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String resultString = "{\"type\": \"geojson\",\"data\": {\"type\": \"FeatureCollection\",\"features\": [";

        System.out.println("[SERVLET] This is a call to POI Retriever");

        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/simevac", "postgres", "12345");

            PreparedStatement ps = c.prepareStatement("SELECT name, latitude, longitude FROM public.pois WHERE category = ? ");
            ps.setString(1, category);
            ResultSet res = ps.executeQuery();

            if(res.next()) {
                resultString += "{\"type\": \"Feature\",\"geometry\": {\"type\": \"Point\",\"coordinates\": ["
                        + res.getDouble(3)
                        + ","
                        + res.getDouble(2)
                        + "]},"
                        + "\"properties\": {\"description\": \""
                        + res.getString(1)
                        + "\"}}";
            }

            while(res.next()) {
                resultString += ",{\"type\": \"Feature\",\"geometry\": {\"type\": \"Point\",\"coordinates\": ["
                        + res.getDouble(3)
                        + ","
                        + res.getDouble(2)
                        + "]},"
                        + "\"properties\": {\"description\": \""
                        + res.getString(1)
                        + "\"}}";
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        //resultString = resultString.substring(0, resultString.length() - 1);

        resultString += "]}}";

        //System.out.println(resultString);
        //response.getWriter().append(resultString);

	    /*
	    String sampleGeoJSON = "{"
	    		+ "\"type\": \"geojson\","
	    		+ "\"data\": {"
	    		+ "\"type\": \"FeatureCollection\","
	    		+ "\"features\": ["
	    			+ "{\"type\": \"Feature\","
	    			+ "\"geometry\": {"
	    			+ "\"type\": \"Point\","
	    			+ "\"coordinates\": [13.404954,52.520007]},"
	    			+ "\"properties\": {\"title\": \"Mapbox DC\"}},"
	    			+ "{\"type\": \"Feature\","
	    			+ "\"geometry\": {"
	    			+ "\"type\": \"Point\","
	    			+ "\"coordinates\": [13.534954,52.120007]"
	    			+ "},"
	    			+ "\"properties\": {\"title\": \"Mapbox DC\"}},"
	    			+ "{\"type\": \"Feature\","
	    			+ "\"geometry\": {"
	    			+ "\"type\": \"Point\","
	    			+ "\"coordinates\": [13.002954,52.920007]"
	    			+ "},"
	    			+ "\"properties\": {\"title\": \"Mapbox DC\"}}"
	    		+ "]"
	    		+ "}"
	    		+ "}";
	    */
        response.getWriter().append(resultString);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
