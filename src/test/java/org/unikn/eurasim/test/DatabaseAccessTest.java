package org.unikn.eurasim.test;

import org.junit.jupiter.api.Test;
import org.unikn.eurasim.model.StreetEdge;
import org.unikn.eurasim.model.StreetNetwork;
import org.unikn.eurasim.servlets.Configuration;
import org.unikn.eurasim.util.PostgresLoader;

public class DatabaseAccessTest {

    protected static double MOVEMENT_SPEED = 1.419;
    protected static double DENSITY = 6.6;
    protected static double WIDTH = 6;

    @Test
    void sampleDatabaseAccess() {
        Configuration.load("test");
        StreetNetwork g = PostgresLoader.loadGraph();

        System.out.println("[DEBUG] Street network nodes = "+g.vertexSet().size()+".");
        System.out.println("[DEBUG] Street network edges = "+g.edgeSet().size()+".");
        System.out.println("[DEBUG] Index size = "+g.spatialIndex.size()+".");
    }
}
