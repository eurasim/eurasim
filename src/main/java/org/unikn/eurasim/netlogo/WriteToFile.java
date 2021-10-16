package org.unikn.eurasim.netlogo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.unikn.eurasim.algorithms.EvacuationPlan;
import org.unikn.eurasim.model.StreetPath;
import org.unikn.eurasim.model.StreetEdge;
import org.unikn.eurasim.model.StreetNode;
import org.unikn.eurasim.servlets.Configuration;

public class WriteToFile {
	private static ArrayList<StreetEdge> alreadySeen = new ArrayList<StreetEdge>();

	public static void writeTofiles(EvacuationPlan plan, double speed, ArrayList<StreetNode> sources,
									ArrayList<StreetNode> targets, Graph<StreetNode, StreetEdge> g, double tickspeed) {

		writeNodes(plan, sources, targets);
		System.out.println("wrote nodes");
		writeEdges(plan, speed, g, tickspeed);
		System.out.println("wrote edges");
		writeWalkers(plan);

	}

	private static void writeNodes(EvacuationPlan plan, ArrayList<StreetNode> sources, ArrayList<StreetNode> targets) {
		// node-id X-Position Y-Position is-source is-destination capacity pathid
		try {
			FileWriter writer = new FileWriter(Configuration.NETLOGO_FILES_PATH+"Nodes.txt");

			for (StreetPath path : plan.getPlans()) {
				for (StreetNode node : path.getPath()) {
					writer.write(String.valueOf(node.getId()));
					writer.write(" ");
				
					if (sources.contains(node)) {
						writer.write(String.valueOf(1));
					} else {
						writer.write(String.valueOf(0));
					}
					writer.write(" ");
					if (targets.contains(node)) {
						writer.write(String.valueOf(1));
					} else {
						writer.write(String.valueOf(0));
					}
					writer.write(" ");
					writer.write(String.valueOf(node.getMaxFlowCapacity()));
					writer.write(" ");
					writer.write(String.valueOf(path.getPathId()));
					writer.write("\r\n"); // write new line

				}

			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeEdges(EvacuationPlan plan, double speed, Graph<StreetNode, StreetEdge> g,
			double tickspeed) {

		try {
			FileWriter writer = new FileWriter(Configuration.NETLOGO_FILES_PATH+"interNodes.txt");
			FileWriter writer2 = new FileWriter(Configuration.NETLOGO_FILES_PATH+"edges.txt");
			int maxId = g.vertexSet().size();
			int previousID = 0;

			for (StreetPath path : plan.getPlans()) {

				for (int i = 0; i < path.getPath().size() - 1; i++) {
					StreetNode source = path.getPath().get(i);
					StreetNode target = path.getPath().get(i + 1);
					StreetEdge current = g.getEdge(source, target);
					if (alreadySeen.contains(current)) {

					} else {
						int numberOfIntermediates = 0;

						numberOfIntermediates = (int) getNumberOfIntermediateNodes(current.getTravelTime(), speed, tickspeed);
						if(numberOfIntermediates==0) {
							numberOfIntermediates= 1;
						}
						for (int j = 0; j < numberOfIntermediates + 1; j++) {
							// from source to first intermediate
							int capacity ;
							if((int)current.getMaxFlowCapacity()/numberOfIntermediates<1){
								capacity = 1;
							}else {
								 capacity =(int)current.getMaxFlowCapacity();
							}
							
							
							if (j == 0) {
								writer.write(String.valueOf(maxId + 1));
								writer.write(" ");
								writer.write(String.valueOf(target.getId()));
								writer.write(" ");
								writer.write(String.valueOf(capacity));
								writer.write("\r\n");
								writer2.write(String.valueOf(source.getId()));
								writer2.write(" ");
								writer2.write(String.valueOf(maxId + 1));
								writer2.write("\r\n");

								previousID = maxId + 1;
								maxId = maxId + 1;
							} // last intermediate to target
							else if (j == numberOfIntermediates) {

								writer2.write(String.valueOf(previousID));
								writer2.write(" ");
								writer2.write(String.valueOf(target.getId()));
								writer2.write("\r\n");

							} // intermediate to intermediate
							else {

								writer.write(String.valueOf(maxId + 1));
								writer.write(" ");
								writer.write(String.valueOf(target.getId()));
								writer.write(" ");
								writer.write(String.valueOf(capacity));
								writer.write("\r\n");
								writer2.write(String.valueOf(previousID));
								writer2.write(" ");
								writer2.write(String.valueOf(maxId + 1));
								writer2.write("\r\n");

								previousID = maxId + 1;
								maxId = maxId + 1;

							}

							alreadySeen.add(current);
						}


					}

				}

			}

			writer.close();
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeWalkers(EvacuationPlan plan) {

		try {
			FileWriter writer = new FileWriter(Configuration.NETLOGO_FILES_PATH+"walkers.txt");
			for (StreetPath path : plan.getPlans()) {
			
			writer.write(String.valueOf(path.getPathId()));
			writer.write(" ");
			writer.write(String.valueOf(path.getNumberOfEvacuees()));
			writer.write(" ");
			writer.write(String.valueOf(path.getPath().get(0).getId()));
			writer.write("\r\n"); // write new line
			}
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static double getNumberOfIntermediateNodes(double edgeLength, double movementSpeed, double tickspeed) {
		return (edgeLength / movementSpeed * tickspeed) - 1;
	}

}
