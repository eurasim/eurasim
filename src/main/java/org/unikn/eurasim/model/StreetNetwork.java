package org.unikn.eurasim.model;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.geotools.referencing.GeodeticCalculator;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.STRtree;

public class StreetNetwork extends DefaultUndirectedWeightedGraph<StreetNode,StreetEdge> {

    private GeodeticCalculator calc;
    public STRtree spatialIndex;
    private Long2ObjectOpenHashMap<StreetNode> nodeMap;

    private static GeometryFactory geometryFactory = null;
    private long edgeCounter;

    public StreetNetwork() {
        super(StreetEdge.class);
        this.spatialIndex = new STRtree();
        this.nodeMap = new Long2ObjectOpenHashMap<>();
        this.edgeCounter = 0;
    }

    public boolean addVertex(long nodeId, double lon, double lat) {
        StreetNode streetNode = new StreetNode();
        streetNode.setId(nodeId);
        streetNode.setLocation(lon,lat);
        this.nodeMap.put(nodeId, streetNode);
        return this.addVertex(streetNode);
    }

    public boolean addVertex(StreetNode node) {
        this.nodeMap.put(node.getId(),node);
        return super.addVertex(node);
    }

    public static GeometryFactory getGeometryFactory() {
        if(geometryFactory == null)
            geometryFactory = new GeometryFactory();
        return geometryFactory;
    }

    public StreetNode findClosestNode(double x, double y) {
        StreetNode nearestNeighbor = null;
        double minDist = Double.MAX_VALUE;
        for(StreetNode n : this.vertexSet()) {
            this.calc = new GeodeticCalculator();
            this.calc.setStartingGeographicPoint(x,y);
            this.calc.setDestinationGeographicPoint(n.getLongitude(),n.getLatitude());
            double curDist = this.calc.getOrthodromicDistance();
            if(curDist < minDist) {
                minDist = curDist;
                nearestNeighbor = n;
            }
        }
        return nearestNeighbor;
    }

    public boolean addEdge(long src, long trg, double weight, LineString lineString, int clazz, int capacity, int travelTime) {
        StreetEdge e = this.addEdge(this.nodeMap.get(src), this.nodeMap.get(trg));
        if(e != null) {
            e.setId(this.edgeCounter++);
            this.setEdgeWeight(e,weight);
            e.setLineString(lineString);
            e.setClazz(clazz);
            e.setMaxFlowCapacity(capacity);
            e.setTravelTime(travelTime);
            this.spatialIndex.insert(e.getBounds(),e);
            return true;
        }
        return false;
    }

    public boolean addEdgeNoIndex(long src, long trg, double weight, LineString lineString, int clazz, int capacity, int travelTime) {
        StreetEdge e = this.addEdge(this.nodeMap.get(src), this.nodeMap.get(trg));
        if(e != null) {
            e.setId(this.edgeCounter++);
            this.setEdgeWeight(e,weight);
            e.setLineString(lineString);
            e.setClazz(clazz);
            e.setMaxFlowCapacity(capacity);
            e.setTravelTime(travelTime);
            return true;
        }
        return false;
    }
    public boolean addEdgeNoIndex(long src, long trg, double weight, int capacity) {
        StreetEdge e = this.addEdge(this.nodeMap.get(src), this.nodeMap.get(trg));
        if(e != null) {
            e.setId(this.edgeCounter++);
            this.setEdgeWeight(e,weight);
            e.setLineString(null);
            e.setClazz(-1);
            e.setMaxFlowCapacity(capacity);
            e.setTravelTime(1);
            return true;
        }
        return false;
    }

    public boolean containsVertex(long nid) {
        return this.nodeMap.containsKey(nid);
    }

    public StreetNode getVertexById(long nodeId) {
        return this.nodeMap.get(nodeId);
    }
}
