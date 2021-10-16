package org.unikn.eurasim.algorithms.ccrp;

import java.util.HashMap;
import org.unikn.eurasim.model.*;

public class TimeDependentNodeCapacities {
	private HashMap<Integer, HashMap<StreetNode, Integer>> times = new HashMap<Integer, HashMap<StreetNode, Integer>>();

	public Integer getCapacities(Integer time, StreetNode node) {

		if (times.get(time) == null) {
			return node.getMaxFlowCapacity();
		}

		else if (times.get(time).get(node) == null) {
			return node.getMaxFlowCapacity();
		} else {
			return times.get(time).get(node);
		}
	}

	public void reduceCapacity(Integer time, StreetNode node, int minFlow) {
		if (times.get(time) == null) {
			 int newCapacitiy = node.getMaxFlowCapacity()-minFlow;
			 HashMap<StreetNode, Integer> nodeCapacities = new  HashMap<StreetNode, Integer>();
			 nodeCapacities.put(node, newCapacitiy);
			 times.put(time, nodeCapacities);
		}
		else if (times.get(time).get(node) == null) {
			int newCapacity = node.getMaxFlowCapacity()-minFlow;
			times.get(time).put(node, newCapacity);
			
		} else {
			int newCapacity = times.get(time).get(node)-minFlow;
			times.get(time).put(node,  newCapacity);
		}

	}

}
