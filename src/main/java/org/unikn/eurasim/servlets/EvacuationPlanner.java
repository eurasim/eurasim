package org.unikn.eurasim.servlets;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.nlogo.headless.HeadlessWorkspace;

import org.unikn.eurasim.algorithms.*;
import org.unikn.eurasim.algorithms.ccrp.CapacityConstrainedRoutePlanner;
import org.unikn.eurasim.algorithms.greedy.GreedyEvacuationRouteSelection;
import org.unikn.eurasim.netlogo.WriteToFile;
import org.unikn.eurasim.mapbox.MapboxJSONFactory;
import org.unikn.eurasim.model.*;

@WebServlet(name = "EvacuationPlanner", value = "/EvacuationPlanner")
public class EvacuationPlanner extends HttpServlet {

    protected static final long serialVersionUID = 1L;

    protected static StreetNetwork g = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ArrayList<DangerZone> dangerZones = new ArrayList<>();
        ArrayList<SafeArea> safeAreas = new ArrayList<>();

        System.out.println("[LOG] Street network nodes = "+g.vertexSet().size()+".");
        System.out.println("[LOG] Street network edges = "+g.edgeSet().size()+".");
        System.out.println("[LOG] STRTree index size = "+g.spatialIndex.size()+".");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[LOG] Received at "+dtf.format(now));

        boolean simulate = false;
        if(request.getParameter("simulation") != null) {
            simulate = Boolean.parseBoolean(request.getParameter("simulation"));
            System.out.println("[LOG] Simulation = "+simulate);
        }
        else {
            System.out.println("[LOG] Simulation off.");
        }

        int i=1;
        while(request.getParameter("r"+i+"lon") != null
                && request.getParameter("r"+i+"lat") != null
                && request.getParameter("r"+i+"ev") != null) {

            System.out.print("[LOG] Risk Area "+i+": ");
            System.out.print("Lon="+request.getParameter("r"+i+"lon"));
            System.out.print(", Lat: "+request.getParameter("r"+i+"lat"));
            System.out.println(", # evacuees: "+request.getParameter("r"+i+"ev"));

            double longitude = Double.parseDouble(request.getParameter("r"+i+"lon"));
            double latitude = Double.parseDouble(request.getParameter("r"+i+"lat"));
            int evacuees = Integer.parseInt(request.getParameter("r"+i+"ev"));

            dangerZones.add(new DangerZone(longitude,latitude,evacuees));
            i++;
        }
        i=1;
        while(request.getParameter("a"+i+"lon") != null
                && request.getParameter("a"+i+"lat") != null
                && request.getParameter("a"+i+"cap") != null) {

            System.out.print("[LOG] Assembly Point "+i+": ");
            System.out.print("Lon="+request.getParameter("a"+i+"lon"));
            System.out.print(", Lat: "+request.getParameter("a"+i+"lat"));
            System.out.println(", Capacity: "+request.getParameter("a"+i+"cap"));

            double longitude = Double.parseDouble(request.getParameter("a"+i+"lon"));
            double latitude = Double.parseDouble(request.getParameter("a"+i+"lat"));
            int capacity = Integer.parseInt(request.getParameter("a"+i+"cap"));

            safeAreas.add(new SafeArea(longitude,latitude,capacity));
            i++;
        }

        String selectedAlgorithm = request.getParameter("algorithm");
        System.out.println("[LOG] Number of source points = "+dangerZones.size()+".");
        System.out.println("[LOG] Number of target points = "+safeAreas.size()+".");

        System.out.println("[LOG] --------------------------");
        System.out.println("[LOG] --- List of risk areas ---");
        for (DangerZone ra : dangerZones) {
            System.out.println("[LOG] Source risk area: " + ra);
        }

        System.out.println("[LOG] --- List of safe areas ---");
        for (SafeArea sa : safeAreas) {
            System.out.println("[LOG] Target safe area: " + sa);
        }
        System.out.println("[LOG] --------------------------");
        System.out.println("[LOG] New street network nodes = "+g.vertexSet().size()+".");
        System.out.println("[LOG] New street network edges = "+g.edgeSet().size()+".");

        //
        // COMPUTING EVACUATION PLAN
        //

        EvacuationPlanningAlgorithm algo = null;

        if(selectedAlgorithm.equals("ccrp")) {
            System.out.println("[LOG] Algorithm: "+selectedAlgorithm);
            algo = new CapacityConstrainedRoutePlanner(g,dangerZones,safeAreas);
        }
        else if (selectedAlgorithm.equals("greedy")) {
            System.out.println("[LOG] Algorithm: "+selectedAlgorithm);
            algo = new GreedyEvacuationRouteSelection(g,dangerZones,safeAreas);
        }

        EvacuationPlan test = algo.computeEvacuationPlan();

        for(int j=0;j<test.getPlans().size();j++) {
            StreetPath p = test.getPlans().get(j);
            System.out.println("[LOG] Path source: "+p.getPath().get(0).getId()+", target: "+p.getPath().get(p.getPath().size()-1).getId()+", evacuees: "+p.getNumberOfEvacuees()+", totalDistance: "+p.getTotalDistance());
        }

        int simulatedTime = -1;
        if(simulate) {
            System.out.println("Simulating plan");
            ArrayList<StreetNode> sourcesT = new ArrayList<>();
            ArrayList<StreetNode> targetsT = new ArrayList<>();
            dangerZones.forEach(dz -> sourcesT.add(dz.getAssignedStreetNetworkNode()));
            safeAreas.forEach(sa -> targetsT.add(sa.getAssignedStreetNetworkNode()));
            int numberOfTicks = this.simulateEvacuationPlan(test, sourcesT, targetsT);
            simulatedTime = (int)(numberOfTicks*Configuration.TICK_SPEED);
        }

        /*
         * GENERATING THE RESPONSE
         */

        String jsonResponse = MapboxJSONFactory.generatePlan(test.getPlans(),dangerZones, safeAreas,simulatedTime);

        System.out.println("[LOG] Reverted street network nodes = "+g.vertexSet().size()+".");
        System.out.println("[LOG] Reverted street network edges = "+g.edgeSet().size()+".");

        response.getWriter().append(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    private int simulateEvacuationPlan(EvacuationPlan test, ArrayList<StreetNode> sourcesT, ArrayList<StreetNode> targetsT) {

        WriteToFile.writeTofiles(test, Configuration.MOVEMENT_SPEED, sourcesT, targetsT, g, Configuration.TICK_SPEED);

        HeadlessWorkspace workspace = HeadlessWorkspace.newInstance() ;
        int prevTicks = -1, numberOfTicks = 0;
        try {
            workspace.open(Configuration.NETLOGO_FILES_PATH+"eurasim.nlogo",false);
            workspace.command("random-seed 0");
            workspace.command("setup");
            do {
                prevTicks = numberOfTicks;
                workspace.command("repeat 100 [ bestgo if not any? turtles [ stop ]]") ;
                String currentTicks = workspace.report("ticks").toString();
                numberOfTicks = (int)Float.parseFloat(currentTicks);
                if(numberOfTicks % 100 == 0) {
                    System.out.println("[LOG] Current ticks = " + numberOfTicks + ", prev = " + prevTicks);
                    System.out.println(workspace.report("count walkers-on turtle-set list_of_destinations"));
                }
            } while(prevTicks != numberOfTicks);
            workspace.dispose();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return numberOfTicks;
    }
}
