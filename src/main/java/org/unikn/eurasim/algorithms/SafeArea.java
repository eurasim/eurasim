package org.unikn.eurasim.algorithms;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.unikn.eurasim.model.StreetNetwork;
import org.unikn.eurasim.model.StreetNode;

public class SafeArea {
    private StreetNode assignedStreetNetworkNode;
    private Point location;

    private int capacity;

    public SafeArea(double longitude, double latitude, int capacity) {
        this.location = StreetNetwork.getGeometryFactory().createPoint(new Coordinate(longitude,latitude));
        this.capacity = capacity;
    }

    public StreetNode getAssignedStreetNetworkNode() {
        return this.assignedStreetNetworkNode;
    }

    public void setAssignedStreetNetworkNode(StreetNode temporaryStreetNetworkNodel) {
        this.assignedStreetNetworkNode = temporaryStreetNetworkNodel;
    }

    public Point getLocation() {
        return this.location;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "location = ("+this.location.getX()+","+this.location.getY()+"), #evacuees = "+this.capacity;
    }
}
