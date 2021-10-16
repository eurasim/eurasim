package org.unikn.eurasim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreetPath {

	private List<StreetNode> path= new ArrayList<>();
	private int numberOfEvacuees; 
	private int pathId;
	private int totalDistance;
	
	public void addNode(StreetNode streetnode) {
		path.add(streetnode);
	}
	
	public void reverse() {
		Collections.reverse(path);
	}

	public void setNodeList(List<StreetNode> nodes) {
		this.path = nodes;
	}
	
	public List<StreetNode> getPath(){
		return this.path;
	}
	
	public void setNumberOfEvacuees(int number) {
		this.numberOfEvacuees = number;
	}
	
	public int getNumberOfEvacuees() {
		return this.numberOfEvacuees;
	}
	
	public void setPathId(int pathId) {
		this.pathId = pathId;
	}
	
	public int getPathId() {
		return this.pathId;
	}

	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}
	
	public int getTotalDistance() {
		return this.totalDistance;
	}
	
}
