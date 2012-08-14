package edu.umn.genomics.phylogeny;

import javax.swing.tree.*;

public class PhylogeNode implements TreeDistance {
  String name;
  double distance;
  double distanceFromRoot;
  String comment;
  public PhylogeNode() {
    this("",Double.NaN,null);
  }
  public PhylogeNode(String name, double distance, String comment) {
    setName(name);
    setDistance(distance); 
    setComment(comment); 
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public double getDistance() {
    return distance;
  }
  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getDistanceFromRoot() {
    return distanceFromRoot;
  }
  public void setDistanceFromRoot(double distance) {
    this.distanceFromRoot = distance;
  }

  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
  public String toString() {
    return name;
  }
}

