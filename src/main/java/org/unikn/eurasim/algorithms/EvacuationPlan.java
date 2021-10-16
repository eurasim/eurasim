package org.unikn.eurasim.algorithms;

import java.util.ArrayList;

import org.unikn.eurasim.model.StreetPath;

public class EvacuationPlan {
	private  ArrayList<StreetPath> plans = new ArrayList<StreetPath>();
	
	public  ArrayList<StreetPath> getPlans(){
		return this.plans;
	}
	
	public  void addPath(StreetPath path) {
		plans.add(path);
	}
}
