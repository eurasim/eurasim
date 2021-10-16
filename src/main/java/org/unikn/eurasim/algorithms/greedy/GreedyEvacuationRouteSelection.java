package org.unikn.eurasim.algorithms.greedy;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.unikn.eurasim.algorithms.DangerZone;
import org.unikn.eurasim.algorithms.EvacuationPlan;
import org.unikn.eurasim.algorithms.EvacuationPlanningAlgorithm;
import org.unikn.eurasim.algorithms.SafeArea;
import org.unikn.eurasim.model.StreetEdge;
import org.unikn.eurasim.model.StreetNetwork;
import org.unikn.eurasim.model.StreetNode;
import org.unikn.eurasim.model.StreetPath;
import org.unikn.eurasim.servlets.Configuration;

import java.util.ArrayList;

public class GreedyEvacuationRouteSelection extends EvacuationPlanningAlgorithm {

    public GreedyEvacuationRouteSelection(StreetNetwork graph, ArrayList<DangerZone> dangerZones, ArrayList<SafeArea> safeAreas) {
        super(graph, dangerZones, safeAreas);

        // Initialize map maintaining evacuees per node
        this.dangerZones.forEach(dZone -> dZone.setAssignedStreetNetworkNode(this.addTempNetworkNode(dZone.getLocation().getX(),dZone.getLocation().getY())));
        this.safeAreas.forEach(sArea -> sArea.setAssignedStreetNetworkNode(this.addTempNetworkNode(sArea.getLocation().getX(),sArea.getLocation().getY())));
        //System.out.println("Nodes matched properly.");
    }

    @Override
    public EvacuationPlan computeEvacuationPlan() {
        EvacuationPlan result = new EvacuationPlan();
        int pathId = 0;

        while(evacueesRemain()) {
            DangerZone sourceDangerZone = null;
            for(DangerZone dz : this.dangerZones) {
                if(sourceDangerZone == null || sourceDangerZone.getNumberOfEvacuees() < dz.getNumberOfEvacuees())
                    sourceDangerZone = dz;
            }

            StreetPath newPath = new StreetPath();
            GraphPath<StreetNode, StreetEdge> path = null;
            SafeArea targetSafeArea = null;
            for(SafeArea sa : this.safeAreas) {
                if(sa.getCapacity() == 0)
                    continue;
                GraphPath<StreetNode, StreetEdge> tempPath =  DijkstraShortestPath.findPathBetween(graph,sourceDangerZone.getAssignedStreetNetworkNode(),sa.getAssignedStreetNetworkNode());
                if(path == null) {
                    path = tempPath;
                    targetSafeArea = sa;
                }
                else if(tempPath.getWeight() < path.getWeight()) {
                    path = tempPath;
                    targetSafeArea = sa;
                }
            }

            if(sourceDangerZone.getNumberOfEvacuees() <= targetSafeArea.getCapacity()) {
                newPath.setNumberOfEvacuees(sourceDangerZone.getNumberOfEvacuees());
                targetSafeArea.setCapacity(targetSafeArea.getCapacity() - sourceDangerZone.getNumberOfEvacuees());
                sourceDangerZone.setNumberOfEvacuees(0);
            }
            else {
                newPath.setNumberOfEvacuees(targetSafeArea.getCapacity());
                sourceDangerZone.setNumberOfEvacuees(sourceDangerZone.getNumberOfEvacuees() - targetSafeArea.getCapacity());
                targetSafeArea.setCapacity(0);
            }
            newPath.setPathId(pathId++);
            newPath.setTotalDistance((int)(path.getWeight()/ Configuration.MOVEMENT_SPEED));
            newPath.setNodeList(path.getVertexList());
            result.addPath(newPath);
        }
        return result;
    }

    private boolean evacueesRemain() {
        //boolean flag = false;
        //System.out.println("----- ITERATION -----");
        for(DangerZone dz : this.dangerZones) {
            //System.out.println("NodeId = "+dz.getAssignedStreetNetworkNode().getId()+", evacuees = "+dz.getNumberOfEvacuees());
            if(dz.getNumberOfEvacuees() > 0)
                return true;
                //flag = true;
        }
        for(SafeArea sa : this.safeAreas) {
            //System.out.println("NodeId = "+sa.getAssignedStreetNetworkNode().getId()+", capacity = "+sa.getCapacity());
        }
        return false; //flag;
    }

}
