package org.unikn.eurasim.test;

import org.junit.jupiter.api.Test;
import org.unikn.eurasim.model.*;

public class StreetNetworkTest {
    @Test
    void sampeGraphGeneration() {
        StreetNetwork g = new StreetNetwork();
        StreetNode v1 = new StreetNode();
        StreetNode v2 = new StreetNode();
        StreetNode v3 = new StreetNode();
        StreetNode v4 = new StreetNode();
        StreetNode v5 = new StreetNode();
        StreetNode v6 = new StreetNode();
        StreetNode v7 = new StreetNode();
        StreetNode v8 = new StreetNode();

        v1.setLocation(1,1);
        v1.setMaxFlowCapacity(5);
        v1.setId(2351425);

        v2.setLocation(1,1);
        v2.setMaxFlowCapacity(5);
        v2.setId(2331425);

        v3.setLocation(1,1);
        v3.setMaxFlowCapacity(5);
        v3.setId(2351455);

        v4.setLocation(1,1);
        v4.setMaxFlowCapacity(5);
        v4.setId(2751455);

        v5.setLocation(1,1);
        v5.setMaxFlowCapacity(5);
        v5.setId(1351425);

        v6.setLocation(1,1);
        v6.setMaxFlowCapacity(5);
        v6.setId(2339425);

        v7.setLocation(1,1);
        v7.setMaxFlowCapacity(5);
        v7.setId(2741455);

        v8.setLocation(1,1);
        v8.setMaxFlowCapacity(5);
        v8.setId(8751455);

        assert(g.addVertex(v1));
        assert(g.addVertex(v2));
        assert(g.addVertex(v3));
        assert(g.addVertex(v4));
        assert(g.addVertex(v5));
        assert(g.addVertex(v6));
        assert(g.addVertex(v7));
        assert(g.addVertex(v8));

        assert(!g.addVertex(v1));
        assert(!g.addVertex(v2));
        assert(!g.addVertex(v3));
        assert(!g.addVertex(v4));
        assert(!g.addVertex(v5));
        assert(!g.addVertex(v6));
        assert(!g.addVertex(v7));
        assert(!g.addVertex(v8));

        /*
        g.addEdge(v1, v2).setLength(10).setcapacity(1);
        g.addEdge(v2, v3).setLength(10).setcapacity(1);
        g.addEdge(v3, v4).setLength(10).setcapacity(1);
        g.addEdge(v5, v6).setLength(20).setcapacity(1);
        g.addEdge(v6, v7).setLength(20).setcapacity(1);
        g.addEdge(v7, v8).setLength(20).setcapacity(1);
        g.addEdge(v5, v1).setLength(5).setcapacity(1);
        g.addEdge(v6, v2).setLength(5).setcapacity(1);
        g.addEdge(v7, v3).setLength(5).setcapacity(1);
        g.addEdge(v8, v4).setLength(5).setcapacity(1);
        */

        assert(g.vertexSet().size() == 8);
        assert(g.edgeSet().size() == 10);
    }
}
