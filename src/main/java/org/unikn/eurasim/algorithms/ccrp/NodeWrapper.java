package org.unikn.eurasim.algorithms.ccrp;

class NodeWrapper<V> implements Comparable<NodeWrapper<V>> {

	 private final V node;
	  private int totalDistance;
	  private NodeWrapper<V> predecessor;
      private int previous;
	  
	  NodeWrapper(V node, int totalDistance, NodeWrapper<V> predecessor, int previous) {
	    this.node = node;
	    this.totalDistance = totalDistance;
	    this.predecessor = predecessor;
	    this.previous =previous;
	  }
	
	  public int getPrevoius() {
		  return this.previous;
	  }
	  
	  public void setPrevious(int privious) {
		  this.previous = privious;
	  }
	
	  public  V getNode() {
		  return this.node;
	  }
	  
	  public  int getTotalDistance() {
		  return this.totalDistance;
	  }
	  
	  public  void setTotalDistance(int totalDistance) {
		   this.totalDistance = totalDistance;
	  }
	  
	  public  void setPredecessor(NodeWrapper<V> predecessor) {
		  this.predecessor = predecessor;
	  }

	  public  NodeWrapper<V> getPredecessor() {
		  return this.predecessor ;
	  }
	  
	 @Override
	  public int compareTo(NodeWrapper<V> o) {
	    return Integer.compare(this.totalDistance, o.totalDistance);
	  }
}
