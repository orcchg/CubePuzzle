package com.orcchg.javatask.cubes.struct;

/**
 * @brief class represents the orientation of the cube (piece)
 *     and also is used to mark certain side of cube
 */
public enum Orientation implements Cloneable {
  UP("UP"), DOWN("DOWN"), RIGHT("RIGHT"), LEFT("LEFT");
  
  public static Orientation[] entries = values();  // to avoid unnecessary instances
  public static final int size = entries.length;
  
  private final String text;
  
  private Orientation(final String text) {
    this.text = text;
  }
  
  // --------------------------------------------------------------------------
  @Override
  public String toString() {
    return text;
  }
}
