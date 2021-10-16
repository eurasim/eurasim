package org.unikn.eurasim.util;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.unikn.eurasim.model.StreetEdge;
import org.unikn.eurasim.model.StreetNode;



public class ReturnOfLoad {
private ArrayList<StreetNode> sourcesReal;
private ArrayList<StreetNode> targestReal;
private Graph<StreetNode, StreetEdge> g;

public void setSourcesReal(ArrayList<StreetNode> sourcesReal) {
	
	this.sourcesReal=sourcesReal;
}

public void setTargetsReal(ArrayList<StreetNode> targestReal) {
	this.targestReal= targestReal;
}

public void setGraph(Graph<StreetNode, StreetEdge> g) {
	this.g=g;
}
public Graph<StreetNode, StreetEdge>  getGraph(){
	return this.g;
}

public ArrayList<StreetNode> getRealSources() {
	return this.sourcesReal;
}

public ArrayList<StreetNode> getTargetReal(){
	return this.targestReal;
}

	
	
	
}
