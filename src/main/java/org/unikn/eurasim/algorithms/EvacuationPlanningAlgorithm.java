package org.unikn.eurasim.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.unikn.eurasim.model.*;

public abstract class EvacuationPlanningAlgorithm {
	
	protected List<DangerZone> dangerZones;
	protected List<SafeArea> safeAreas;
	protected StreetNetwork graph;

	protected long tempNodeId = -1;
	
	public EvacuationPlanningAlgorithm(StreetNetwork graph, ArrayList<DangerZone> dangerZones, ArrayList<SafeArea> safeAreas) {
		this.setGraph(graph);
		this.dangerZones = dangerZones;
		this.safeAreas = safeAreas;
	}
	
	public void setGraph(StreetNetwork graph) {
		this.graph = graph;
	}
	
	public StreetNetwork getGraph() {
		return graph;
	}
	
	public void setSources(List<DangerZone> dangerZones) {
		this.dangerZones = dangerZones;
	}
	
	public List<DangerZone> getSources() {
		return this.dangerZones;
	}

	public void setTargets(List<SafeArea> safeAreas) {
		this.safeAreas = safeAreas;
	}
	
	public List<SafeArea> getTargets() {
		return this.safeAreas;
	}

	public StreetNode addTempNetworkNode(double longitude, double latitude) {
		//System.out.println("RUNNING addTemporaryNode");
		Envelope env = new Envelope(new Coordinate(longitude,latitude));

		List result;
		int counter = 0;
		do {
			result = this.graph.spatialIndex.query(env);
			env.expandBy(0.0001);
			counter++;
			//System.out.println("Matching attempt");
		} while(result.size() == 0 && counter < 50);

		if(result.size() == 0) {
			System.out.println("[ERROR] No match");
			return null;
		}

		StreetEdge matchedEdge = (StreetEdge) result.get(0); // get first matched edge

		//System.out.println("Matched edge found.");

		// Adding new node for the POI

		long newNodeId = this.tempNodeId--;
		this.graph.addVertex(newNodeId, longitude, latitude);
		StreetNode tempNode = this.graph.getVertexById(newNodeId);
		tempNode.setMaxFlowCapacity(Integer.MAX_VALUE);

		//System.out.println("Temp vertex added.");

		// Add new node and edges in the street network.
		StreetNode matchedEdgeSource = this.graph.getEdgeSource(matchedEdge);
		StreetNode matchedEdgeTarget = this.graph.getEdgeTarget(matchedEdge);

		GeodeticCalculator gc = new GeodeticCalculator();  // Geodesic distance source-target of matched edge
		gc.setStartingGeographicPoint(matchedEdgeSource.getLocation().getX(), matchedEdgeSource.getLocation().getY());
		gc.setDestinationGeographicPoint(matchedEdgeTarget.getLocation().getX(), matchedEdgeTarget.getLocation().getY());
		double orthoDistSrcTrg = gc.getOrthodromicDistance();
		//System.out.println("orthoDistSrcTrg = "+orthoDistSrcTrg);

		gc = new GeodeticCalculator(); // Geodesic distance source-new node of matched edge
		gc.setStartingGeographicPoint(matchedEdgeSource.getLocation().getX(), matchedEdgeSource.getLocation().getY());
		gc.setDestinationGeographicPoint(longitude, latitude);
		double orthoDistSrcPoi = gc.getOrthodromicDistance();
		//System.out.println("orthoDistSrcPoi = "+orthoDistSrcPoi);

		gc = new GeodeticCalculator(); // Geodesic distance new node-target of matched edge
		gc.setStartingGeographicPoint(longitude, latitude);
		gc.setDestinationGeographicPoint(matchedEdgeTarget.getLocation().getX(), matchedEdgeTarget.getLocation().getY());
		double orthoDistPoiTrg = gc.getOrthodromicDistance();
		//System.out.println("orthoDistPoiTrg = "+orthoDistPoiTrg);

		double poiDistToSource = (orthoDistSrcPoi/orthoDistSrcTrg)*this.graph.getEdgeWeight(matchedEdge);
		if(poiDistToSource <= 0)
			poiDistToSource = 1;

		double poiDistToTarget = (orthoDistPoiTrg/orthoDistSrcTrg)*this.graph.getEdgeWeight(matchedEdge);
		if(poiDistToTarget <= 0)
			poiDistToTarget = 1;

		//System.out.println("Just before adding new edges.");

		Coordinate [] coordsSrc = {matchedEdgeSource.getLocation().getCoordinate(), tempNode.getLocation().getCoordinate()};
		this.graph.addEdgeNoIndex(tempNode.getId(),
				matchedEdgeSource.getId(),
				poiDistToSource,
				StreetNetwork.getGeometryFactory().createLineString(coordsSrc),
				matchedEdge.getClazz(),
				(int)((poiDistToSource/this.graph.getEdgeWeight(matchedEdge))*matchedEdge.getMaxFlowCapacity()),
				(int)((poiDistToSource/this.graph.getEdgeWeight(matchedEdge))*matchedEdge.getTravelTime()));

		Coordinate [] coordsTrg = {matchedEdgeTarget.getLocation().getCoordinate(), tempNode.getLocation().getCoordinate()};
		this.graph.addEdgeNoIndex(tempNode.getId(),
				matchedEdgeTarget.getId(),
				poiDistToTarget,
				StreetNetwork.getGeometryFactory().createLineString(coordsTrg),
				matchedEdge.getClazz(),
				(int)((poiDistToTarget/this.graph.getEdgeWeight(matchedEdge))*matchedEdge.getMaxFlowCapacity()),
				(int)((poiDistToTarget/this.graph.getEdgeWeight(matchedEdge))*matchedEdge.getTravelTime()));

		//System.out.println("Matched node ended.");

		return tempNode;
	}

	public void deleteTemporaryNodes() {
		ArrayList<StreetNode> toBeDeleted = new ArrayList<>();
		for(StreetNode sn : this.graph.vertexSet()) {
			if(sn.getId() < 0)
				toBeDeleted.add(sn);
		}
		//System.out.print("Deleting temporary nodes ");
		toBeDeleted.forEach( (n) -> this.graph.removeVertex(n) );
		//System.out.println("DONE.");
		this.tempNodeId = -1;
	}
	
	public abstract EvacuationPlan computeEvacuationPlan();

}
