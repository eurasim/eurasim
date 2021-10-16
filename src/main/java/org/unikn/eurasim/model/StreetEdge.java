package org.unikn.eurasim.model;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.index.strtree.Boundable;
import org.unikn.eurasim.servlets.Configuration;

public class StreetEdge extends DefaultWeightedEdge implements Boundable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long eid;
	private int clazz;
	private int width;

	private int travelTime;
	private int maxFlowCapacity;

	private LineString lineString;

	public void setId(long eid) {
		this.eid=eid;
	}

	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	public void setMaxFlowCapacity(int maxFlowCapacity) {
		this.maxFlowCapacity = maxFlowCapacity;
	}
	
	public int getMaxFlowCapacity() {
		return this.maxFlowCapacity;
	}
	
	public int getTravelTime() {
		return this.travelTime;
	}

	public long getId() {
		return this.eid;
	}

	public LineString getLineString() {
		return lineString;
	}

	public void setLineString(LineString lineString) {
		this.lineString = lineString;
	}

	@Override
	public Envelope getBounds() {
		Envelope env = new Envelope();
		Coordinate[] lsCoordinates = lineString.getCoordinates();
		for(Coordinate lsCoordinate : lsCoordinates) {
			env.expandToInclude(lsCoordinate);
		}
		//System.out.println("[EDGE_BOUNDS] "+env.toString());
		return env;
	}

	public int getClazz() {
		return clazz;
	}

	public void setClazz(int clazz) {
		this.clazz = clazz;
		this.width = Configuration.EDGE_WIDTH_MAP.get(clazz);
	}

	public int getWidth() {
		return this.width;
	}
}
