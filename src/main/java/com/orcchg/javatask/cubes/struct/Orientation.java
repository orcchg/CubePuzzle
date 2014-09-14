package com.orcchg.javatask.cubes.struct;

public enum Orientation {
  UP, DOWN, RIGHT, LEFT;
  
  public static Orientation[] entries = values();  // to avoid unnecessary instances
  public static final int size = entries.length;
}
