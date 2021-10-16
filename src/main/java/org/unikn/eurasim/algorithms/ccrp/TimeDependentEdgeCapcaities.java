package org.unikn.eurasim.algorithms.ccrp;

import java.util.HashMap;

import org.unikn.eurasim.model.*;

public class TimeDependentEdgeCapcaities {

	private HashMap<Integer, HashMap<StreetEdge, Integer>> times = new HashMap<Integer, HashMap<StreetEdge, Integer>>();

	public Integer getCapacities(Integer time, StreetEdge edge) {

		if (times.get(time) == null) {
			return edge.getMaxFlowCapacity();
		}

		else if (times.get(time).get(edge) == null) {
			return edge.getMaxFlowCapacity();
		} else {
			return times.get(time).get(edge);
		}
	}

	public void reduceCapacity(Integer time, StreetEdge edge, int minFlow) {
		if (times.get(time) == null) {
			 int newCapacitiy = edge.getMaxFlowCapacity()-minFlow;
			 HashMap<StreetEdge, Integer> edgeCapacities = new  HashMap<StreetEdge, Integer>();
			 edgeCapacities.put(edge, newCapacitiy);
			 times.put(time, edgeCapacities);
			
		}

		else if (times.get(time).get(edge) == null) {
			int newCapacity = edge.getMaxFlowCapacity()-minFlow;
			times.get(time).put(edge, newCapacity);
			
		} else {
			int newCapacity = times.get(time).get(edge)-minFlow;
			times.get(time).put(edge,  newCapacity);
		}

	}
}
