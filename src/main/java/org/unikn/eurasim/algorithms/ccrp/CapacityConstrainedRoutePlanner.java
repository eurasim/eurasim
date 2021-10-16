package org.unikn.eurasim.algorithms.ccrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.jgrapht.Graphs;
import org.unikn.eurasim.algorithms.DangerZone;
import org.unikn.eurasim.algorithms.EvacuationPlan;
import org.unikn.eurasim.algorithms.EvacuationPlanningAlgorithm;
import org.unikn.eurasim.algorithms.SafeArea;
import org.unikn.eurasim.model.*;

public class CapacityConstrainedRoutePlanner extends EvacuationPlanningAlgorithm {

	public static final long SUPER_SOURCE_ID = -999999;

	private TimeDependentEdgeCapcaities tec = new TimeDependentEdgeCapcaities();
	private TimeDependentNodeCapacities tnc = new TimeDependentNodeCapacities();

	ArrayList<StreetNode> nodeSources;
	ArrayList<StreetNode> nodeTargets;

	private Long2IntOpenHashMap nodeEvacueesMap;

	public CapacityConstrainedRoutePlanner(StreetNetwork g, ArrayList<DangerZone> sources, ArrayList<SafeArea> targets) {
		super(g,sources,targets);
		nodeSources = new ArrayList<>();
		nodeTargets = new ArrayList<>();

		sources.forEach(s -> {
			StreetNode n = this.addTempNetworkNode(s.getLocation().getX(),s.getLocation().getY());
			s.setAssignedStreetNetworkNode(n);
			nodeSources.add(n);
		});
		targets.forEach(t -> {
			StreetNode n = this.addTempNetworkNode(t.getLocation().getX(),t.getLocation().getY());
			t.setAssignedStreetNetworkNode(n);
			n.setMaxFlowCapacity(t.getCapacity());
			nodeTargets.add(n);
		});

		this.nodeEvacueesMap = new Long2IntOpenHashMap();
		this.graph.vertexSet().forEach(n -> this.nodeEvacueesMap.put(n.getId(),0));
		sources.forEach(s -> {
			this.nodeEvacueesMap.replace(s.getAssignedStreetNetworkNode().getId(),s.getNumberOfEvacuees());
			System.out.println(s.getAssignedStreetNetworkNode().getId()+" "+s.getNumberOfEvacuees()+ " "+this.nodeEvacueesMap.get(s.getAssignedStreetNetworkNode().getId()));
		});
	}

	public EvacuationPlan computeEvacuationPlan() {

		StreetNode s = appendSupersSource();
		EvacuationPlan result = new EvacuationPlan();
		boolean check = chekIfStillEvacuess();
		int pathId = -1;

		while (check) {
			pathId++;
			StreetPath newPath = dijkstra(s, this.nodeTargets, pathId);
			result.addPath(newPath);
			check = chekIfStillEvacuess();
		}

		return result;

	}

	// checks by looking if the extual real capacities

	private boolean chekIfStillEvacuess() {
		for (StreetNode n : this.nodeSources) {
			if (this.nodeEvacueesMap.get(n.getId()) > 0) {
				return true;
			}
		}
		return false;
	}

	private StreetNode appendSupersSource() {
		StreetNode superNode = new StreetNode();
		superNode.setMaxFlowCapacity(Integer.MAX_VALUE);
		superNode.setId(SUPER_SOURCE_ID);
		this.graph.addVertex(superNode);
		for (StreetNode s : this.nodeSources) {
			this.graph.addEdgeNoIndex(superNode.getId(), s.getId(), 1.0, Integer.MAX_VALUE);
		}
		return superNode;
	}

	private StreetPath dijkstra(StreetNode s, ArrayList<StreetNode> targets, int pathId) {
		PriorityQueue<NodeWrapper<StreetNode>> queue = new PriorityQueue<>();
		HashMap<StreetNode, NodeWrapper<StreetNode>> nodeWrappers = new HashMap<StreetNode, NodeWrapper<StreetNode>>();
		NodeWrapper<StreetNode> sourceWrapper = new NodeWrapper<>(s, 0, null, 0);
		Set<StreetNode> settledNodes = new HashSet<>();
		nodeWrappers.put(s, sourceWrapper);
		queue.add(sourceWrapper);
		int i = 0;
		while (!queue.isEmpty()) {
			NodeWrapper<StreetNode> nodeWrapper = queue.poll();
			StreetNode node = nodeWrapper.getNode();
			settledNodes.add(node);
			if (targets.contains(node)) 
				return buildPath(nodeWrapper, pathId, s);

			Set<StreetNode> neighbors = Graphs.neighborSetOf(this.graph,node);
			
			for (StreetNode neighbor : neighbors) {
				StreetNode test = neighbor;

				if (settledNodes.contains(neighbor))
					continue;
				
				int distance = this.graph.getEdge(node, neighbor).getTravelTime();
				int previous = nodeWrapper.getTotalDistance();
				int totalDistance = previous + distance;
				/*
				 * checks time dependent if node has capacity if not increament time intervall
				 * source nodes are excluded in other paths (where they are not source nodes)
				 * capacities for source nodes are handeld differentlx and represent number of
				 * evacuees still left at source node
				 *
				 */
				if (!this.nodeTargets.contains(test)) {
					if (nodeSources.contains(test)) {
						if (node.getId() == SUPER_SOURCE_ID && test.getMaxFlowCapacity() != 0) {
						} else
							continue;
					} else {
						if (tec.getCapacities(previous, this.graph.getEdge(node, neighbor)) > 0 && tnc.getCapacities(totalDistance, test) > 0) {
						} else {
							while (tec.getCapacities(previous, this.graph.getEdge(node, neighbor)) <= 0 || tnc.getCapacities(totalDistance, test) <= 0) {
								previous = totalDistance;
								totalDistance = totalDistance + distance;
							}
						}
					}
					// Previous / wait time is not taken into consideration
					NodeWrapper<StreetNode> neighborWrapper = nodeWrappers.get(neighbor);
					if (neighborWrapper == null) {
						neighborWrapper = new NodeWrapper<>(neighbor, totalDistance, nodeWrapper, previous);
						nodeWrappers.put(neighbor, neighborWrapper);
						queue.add(neighborWrapper);
					}

					else if (totalDistance < neighborWrapper.getTotalDistance()) {
						neighborWrapper.setTotalDistance(totalDistance);
						neighborWrapper.setPredecessor(nodeWrapper);
						neighborWrapper.setPrevious(previous);
						queue.remove(neighborWrapper);
						queue.add(neighborWrapper);
					}

				} else {
					if (test.getMaxFlowCapacity() == 0) {
						continue;
					} else {
						if (tec.getCapacities(previous, this.graph.getEdge(node, neighbor)) > 0) {
						} else {
							while (tec.getCapacities(previous, this.graph.getEdge(node, neighbor)) <= 0) {
								previous = totalDistance;
								totalDistance = totalDistance + distance;
							}
						}
						NodeWrapper<StreetNode> neighborWrapper = new NodeWrapper<>(neighbor, totalDistance, nodeWrapper, previous);
						nodeWrappers.put(neighbor, neighborWrapper);
						queue.add(neighborWrapper);
					}
				}
			}
		}
		return null;
	}

	private StreetPath buildPath(NodeWrapper<StreetNode> nodeWrapper, int pathId, StreetNode s) {
		StreetPath path = new StreetPath();
		path.setPathId(pathId);
		path.setTotalDistance(nodeWrapper.getTotalDistance());
		NodeWrapper<StreetNode> nodeWrapper2 = new NodeWrapper<StreetNode>(nodeWrapper.getNode(),
				nodeWrapper.getTotalDistance(), nodeWrapper.getPredecessor(), nodeWrapper.getPrevoius());
		// find minimum flow in path exclude source and super source node
		int min = minFlow(nodeWrapper);
		while (nodeWrapper != null) {
			if (nodeWrapper.getNode().getId() == SUPER_SOURCE_ID) {
				nodeWrapper = nodeWrapper.getPredecessor();
			} else {
				path.addNode(nodeWrapper.getNode());
				nodeWrapper = nodeWrapper.getPredecessor();
			}
		}

		path.reverse();
		// check if source node has min flow
		StreetNode first = path.getPath().get(0);
		if (this.nodeEvacueesMap.get(first.getId()) < min) {
			min = this.nodeEvacueesMap.get(first.getId());
		}

		int current = this.nodeEvacueesMap.get(first.getId());
		int newCap = current - min;
		// min number of evacuees passing thru this path
		path.setNumberOfEvacuees(min);
		this.nodeEvacueesMap.replace(first.getId(),newCap);

//reduce time dependent capacities along the way
		while (nodeWrapper2 != null) {
			if (!this.nodeTargets.contains(nodeWrapper2.getNode())) {
				if (nodeWrapper2.getNode().getId() == SUPER_SOURCE_ID) {
					nodeWrapper2 = nodeWrapper2.getPredecessor();
				} else {
					if (nodeWrapper2.getPredecessor().getNode().getId() == SUPER_SOURCE_ID) {
					} else {
						tec.reduceCapacity(nodeWrapper2.getPrevoius(), this.graph.getEdge(nodeWrapper2.getPredecessor().getNode(), nodeWrapper2.getNode()), min);
						tnc.reduceCapacity(nodeWrapper2.getTotalDistance(), nodeWrapper2.getNode(), min);
					}
					nodeWrapper2 = nodeWrapper2.getPredecessor();
				}
			} else {
				tec.reduceCapacity(nodeWrapper2.getPrevoius(), this.graph.getEdge(nodeWrapper2.getPredecessor().getNode(), nodeWrapper2.getNode()), min);
				nodeWrapper2.getNode().setMaxFlowCapacity(nodeWrapper2.getNode().getMaxFlowCapacity() - min);
				nodeWrapper2 = nodeWrapper2.getPredecessor();
			}
		}

		// if no more evacuees on source node remove connection
		if (this.nodeEvacueesMap.get(first.getId()) == 0) {
			this.graph.removeEdge(s, first);
		}

		return path;
	}

	private int minFlow(NodeWrapper<StreetNode> nodeWrapper) {

		int minF = Integer.MAX_VALUE;

		while (nodeWrapper != null) {
			if (nodeWrapper.getNode().getId() == SUPER_SOURCE_ID) {
				nodeWrapper = nodeWrapper.getPredecessor();
			} else {
				if (!this.nodeTargets.contains(nodeWrapper.getNode())) {
					int flowNode = tnc.getCapacities(nodeWrapper.getTotalDistance(), nodeWrapper.getNode());
					int flowEdge = tec.getCapacities(nodeWrapper.getPrevoius(), this.graph.getEdge(nodeWrapper.getPredecessor().getNode(), nodeWrapper.getNode()));
					if (minF > flowEdge && nodeWrapper.getPredecessor().getNode().getId() != SUPER_SOURCE_ID) {
						minF = flowEdge;
					}
					if (minF > flowNode && nodeWrapper.getPredecessor().getNode().getId() != SUPER_SOURCE_ID) {
						minF = flowNode;
					}
					nodeWrapper = nodeWrapper.getPredecessor();
				} else {
					int flowNode = nodeWrapper.getNode().getMaxFlowCapacity();
					int flowEdge = tec.getCapacities(nodeWrapper.getPrevoius(), this.graph.getEdge(nodeWrapper.getPredecessor().getNode(), nodeWrapper.getNode()));

					if (minF > flowEdge) {
						minF = flowEdge;
					}
					if (minF > flowNode) {
						minF = flowNode;
					}
					nodeWrapper = nodeWrapper.getPredecessor();
				}
			}
		}
		return minF;
	}
}
