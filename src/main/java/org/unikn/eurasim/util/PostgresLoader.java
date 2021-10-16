package org.unikn.eurasim.util;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.unikn.eurasim.model.*;
import org.unikn.eurasim.servlets.Configuration;

import java.sql.*;

public class PostgresLoader {

	public static StreetNetwork loadGraph() {

		System.out.println("[LOG] PostgresLoader - Loading graph from DB.");

		StreetNetwork g = new StreetNetwork();
		WKTReader wktreader = new WKTReader();
		
		try {
			Class.forName(Configuration.DB_DRIVER);
			Connection con = DriverManager.getConnection(Configuration.DB_HOST_URL, Configuration.DB_USER, Configuration.DB_PASSWORD);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT " +
					"id, " +
					"osm_source_id, " +
					"osm_target_id, " +
					"clazz, " +
					"ROUND((km*1000)::numeric,3) AS distance, " +
					"ST_X(source_location), " +
					"ST_Y(source_location), " +
					"ST_X(target_location), " +
					"ST_Y(target_location), " +
					"ST_AsText(geom_way) " +
				"FROM berlin " +
				"WHERE osm_source_id <> osm_target_id");

			while (rs.next()) {
				long edgeOsmId = rs.getLong(1);
				long sourceOsmId = rs.getLong(2);
				long targetOsmId = rs.getLong(3);
				int clazz = rs.getInt(4);
				double edgeLength = rs.getDouble(5);
				double sourceX = rs.getDouble(6);
				double sourceY = rs.getDouble(7);
				double targetX = rs.getDouble(8);
				double targetY = rs.getDouble(9);
				String ls = rs.getString(10);

				// Add source and target nodes to the graph if they do not already exist
				StreetNode source, target;
				if(!g.containsVertex(sourceOsmId)) {
					source = new StreetNode();
					source.setLocation(sourceX, sourceY);
					source.setId(sourceOsmId);
					g.addVertex(source);
				}
				else {
					source = g.getVertexById(sourceOsmId);
				}

				if(!g.containsVertex(targetOsmId)) {
					target = new StreetNode();
					target.setLocation(targetX, targetY);
					target.setId(targetOsmId);
					g.addVertex(target);
				}
				else {
					target = g.getVertexById(targetOsmId);
				}

				if (!g.containsEdge(source, target) || g.getEdgeWeight(g.getEdge(source,target)) > edgeLength) {
					if(!g.containsEdge(source, target)) {
						g.addEdge(source.getId(),
								target.getId(),
								edgeLength,
								StreetNetwork.getGeometryFactory().createLineString(wktreader.read(ls).getCoordinates()),
								clazz,
								calculateCapacity(clazz,edgeLength),
								calculateTravelTime(edgeLength));
					}
					else {
						StreetEdge se = g.getEdge(source, target);
						g.setEdgeWeight(se,edgeLength);
						se.setId(edgeOsmId);
						se.setLineString(StreetNetwork.getGeometryFactory().createLineString(wktreader.read(ls).getCoordinates()));
						se.setClazz(clazz);
						se.setMaxFlowCapacity(calculateCapacity(clazz,edgeLength));
						se.setTravelTime(calculateTravelTime(edgeLength));
					}

					// Call these after length and width have been set

				}
			}

			// Computing node capacities
			for(StreetNode sn : g.vertexSet())
				sn.setMaxFlowCapacity(calculateIntersectionCapacity(g,sn));

		} catch (SQLException | ClassNotFoundException | ParseException ex) {
			//Logger lgr = Logger.getLogger("NO");
			//lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		}
		System.out.println("[LOG] PostgresLoader - Loading graph completed.");
		return g;
	}

	// This loader assumes that the evacuation speed is constant. That is guaranteed by the maximum capacity of edges.
	// In other words, the speed is the maximum speed evacuees can move and cannot be lowered due to congestion.
	private static int calculateTravelTime(double length) {
		int travelTime = (int)Math.round(length*Configuration.MOVEMENT_SPEED);
		if(travelTime== 0)
			return 1;
		return travelTime;
	}

	private static int calculateCapacity(int clazz, double length) {
		double tmp = Configuration.DENSITY * (double)Configuration.EDGE_WIDTH_MAP.get(clazz) * length;
		return (int)Math.round(Math.floor(tmp));
	}

	// TODO: not ideal computation. It should be the product of widths of edges if edges have different types.
	private static int calculateIntersectionCapacity(StreetNetwork g, StreetNode sn) {
		int maxWidth = 0;
		for (StreetEdge e : g.edgesOf(sn)) {
			if(e.getWidth() > maxWidth)
				maxWidth = e.getWidth();
		}
		double tmp = Configuration.DENSITY * (double)maxWidth * (double)maxWidth;
		return (int)Math.round(Math.floor(tmp));
	}
}
