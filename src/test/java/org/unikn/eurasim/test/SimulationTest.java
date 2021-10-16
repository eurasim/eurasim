package org.unikn.eurasim.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nlogo.headless.HeadlessWorkspace;
import org.unikn.eurasim.algorithms.DangerZone;
import org.unikn.eurasim.algorithms.EvacuationPlan;
import org.unikn.eurasim.algorithms.EvacuationPlanningAlgorithm;
import org.unikn.eurasim.algorithms.SafeArea;
import org.unikn.eurasim.algorithms.ccrp.CapacityConstrainedRoutePlanner;
import org.unikn.eurasim.algorithms.greedy.GreedyEvacuationRouteSelection;
import org.unikn.eurasim.model.StreetNetwork;
import org.unikn.eurasim.model.StreetNode;
import org.unikn.eurasim.model.StreetPath;
import org.unikn.eurasim.netlogo.WriteToFile;
import org.unikn.eurasim.servlets.Configuration;
import org.unikn.eurasim.util.PostgresLoader;
import sun.net.www.http.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class SimulationTest {

    static StreetNetwork g = null;

    @BeforeAll
    static void loadNetwork() {
        Configuration.load("file.config");
        g = PostgresLoader.loadGraph();
    }

    @Test
    void evacuationPlanSimulationTest() {
        System.out.println("[BEGIN] Street network nodes = "+g.vertexSet().size());
        System.out.println("[BEGIN] street network edges = "+g.edgeSet().size());
        ArrayList<DangerZone> dangerZones = new ArrayList<>();
        dangerZones.add(new DangerZone(13.395340962888355, 52.53755164382079,20));
        dangerZones.add(new DangerZone(13.428299947263355, 52.53984863772234,20));

        ArrayList<SafeArea> safeAreas = new ArrayList<>();
        safeAreas.add(new SafeArea(13.413193746092162, 52.520424811067386,35));
        safeAreas.add(new SafeArea(13.431733174803327, 52.52585599335927,10));

        EvacuationPlanningAlgorithm algo = new CapacityConstrainedRoutePlanner(g,dangerZones,safeAreas);
        EvacuationPlan test = algo.computeEvacuationPlan();
        System.out.println("[TEST] Path test = "+test.getPlans().size());

        for(StreetPath p : test.getPlans()) {
            System.out.println(p.getPathId()+") dist="+p.getTotalDistance()+", evacuees="+ p.getNumberOfEvacuees());
        }

        ArrayList<StreetNode> sourcesT = new ArrayList<>();
        ArrayList<StreetNode> targetsT = new ArrayList<>();
        dangerZones.forEach(dz -> sourcesT.add(dz.getAssignedStreetNetworkNode()));
        safeAreas.forEach(sa -> targetsT.add(sa.getAssignedStreetNetworkNode()));
        WriteToFile.writeTofiles(test, Configuration.MOVEMENT_SPEED, sourcesT, targetsT, g, Configuration.TICK_SPEED);

        algo.deleteTemporaryNodes();

        System.out.println("[END] Street network nodes = "+g.vertexSet().size());
        System.out.println("[END] Street network edges = "+g.edgeSet().size());

        HeadlessWorkspace workspace = HeadlessWorkspace.newInstance() ;
        int prevTicks = -1, numberOfTicks = 0;
        try {
            workspace.open(Configuration.NETLOGO_FILES_PATH+"eurasim.nlogo",true);
            workspace.command("random-seed 0");
            workspace.command("setup");
            do {
                prevTicks = numberOfTicks;
                workspace.command("repeat 100 [ bestgo if not any? turtles [ stop ]]") ;
                String currentTicks = workspace.report("ticks").toString();
                numberOfTicks = (int)Float.parseFloat(currentTicks);
                System.out.println("Current ticks = " + numberOfTicks + ", prev = " + prevTicks);
                workspace.command("show count walkers-on turtle-set list_of_destinations");
                //System.out.println("Report = "+workspace.report("show count walkers-on turtle-set list_of_destinations"));
            } while(prevTicks != numberOfTicks);
            //workspace.command("show count walkers-on turtle-set list_of_destinations");
            workspace.dispose();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
