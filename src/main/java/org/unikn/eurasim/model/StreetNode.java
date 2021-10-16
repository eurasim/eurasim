package org.unikn.eurasim.model;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class StreetNode {

	Point location;
	private long osmId;
	private int maxFlowCapacity;

	public void setMaxFlowCapacity(int maxFlowCapacity) {
		this.maxFlowCapacity = maxFlowCapacity;
	}

	public void setLocation(double lon, double lat) {
		this.location = StreetNetwork.getGeometryFactory().createPoint(new Coordinate(lon,lat));
	}

	public void setId(long id) {
		this.osmId = id;
	}

	public Point getLocation() {
		return this.location;
	}

	public double getLongitude() {
		return this.location.getX();
	}

	public double getLatitude() {
		return this.location.getY();
	}

	public int getMaxFlowCapacity() {
		return this.maxFlowCapacity;
	}

	public long getId() {
		return this.osmId;
	}
   
}
