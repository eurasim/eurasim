package org.unikn.eurasim.algorithms;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.unikn.eurasim.model.StreetNetwork;
import org.unikn.eurasim.model.StreetNode;

public class DangerZone {
    private StreetNode assignedStreetNetworkNode;
    private Point location;



    private int numberOfEvacuees;

    public DangerZone(double longitude, double latitude, int numberOfEvacuees) {
        this.location = StreetNetwork.getGeometryFactory().createPoint(new Coordinate(longitude,latitude));
        this.numberOfEvacuees = numberOfEvacuees;
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

    public int getNumberOfEvacuees() {
        return this.numberOfEvacuees;
    }

    public void setNumberOfEvacuees(int numberOfEvacuees) {
        this.numberOfEvacuees = numberOfEvacuees;
    }

    @Override
    public String toString() {
        return "location = ("+this.location.getX()+","+this.location.getY()+"), #evacuees = "+this.numberOfEvacuees;
    }
}
