package com.orcchg.javatask.cubes.struct;

public enum Orientation implements Cloneable {
  UP, DOWN, RIGHT, LEFT;
  
  public static Orientation[] entries = values();  // to avoid unnecessary instances
  public static final int size = entries.length;
  
  public boolean mirror = false;  // indicates whether it necessary to mirror cube
  public boolean reversed = false;  // indicates, which cube matches via reversed side with another cube's side in normal direction
  
  public static Orientation[] getValidOrientation(Orientation lhs, Orientation rhs) {
    Orientation orientation[] = new Orientation[2];
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.UP && rhs == Orientation.UP) {
      orientation[0] = Orientation.UP;  // lhs
      orientation[1] = Orientation.DOWN;  // rhs
      orientation[1].reversed = true;
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.UP && rhs == Orientation.DOWN) {
      orientation[0] = Orientation.UP;  // lhs
      orientation[1] = Orientation.UP;  // rhs
      return orientation;
    }
    if (lhs == Orientation.UP && rhs == Orientation.RIGHT) {
      orientation[0] = Orientation.UP;  // lhs
      orientation[1] = Orientation.RIGHT;  // rhs
      orientation[1].reversed = true;
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.UP && rhs == Orientation.LEFT) {
      orientation[0] = Orientation.UP;  // lhs
      orientation[1] = Orientation.LEFT;  // rhs
      return orientation;
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.DOWN && rhs == Orientation.UP) {
      orientation[0] = Orientation.DOWN;  // lhs
      orientation[1] = Orientation.DOWN;  // rhs
      return orientation;
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.DOWN) {
      orientation[0] = Orientation.DOWN;  // lhs
      orientation[1] = Orientation.UP;  // rhs
      orientation[0].reversed = true;
      return orientation;  // reversed 1
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.RIGHT) {
      orientation[0] = Orientation.DOWN;  // lhs
      orientation[1] = Orientation.RIGHT;  // rhs
      return orientation;
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.LEFT) {
      orientation[0] = Orientation.DOWN;  // lhs
      orientation[1] = Orientation.LEFT;  // rhs
      orientation[0].reversed = true;
      return orientation;  // reversed 1
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.RIGHT && rhs == Orientation.UP) {
      orientation[0] = Orientation.LEFT;  // lhs
      orientation[1] = Orientation.DOWN;  // rhs
      orientation[1].reversed = true;
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.DOWN) {
      orientation[0] = Orientation.LEFT;  // lhs
      orientation[1] = Orientation.UP;  // rhs
      return orientation;
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.RIGHT) {
      orientation[0] = Orientation.LEFT;  // lhs
      orientation[1] = Orientation.RIGHT;  // rhs
      orientation[1].reversed = true;
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.LEFT) {
      orientation[0] = Orientation.LEFT;  // lhs
      orientation[1] = Orientation.LEFT;  // rhs
      return orientation;
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.LEFT && rhs == Orientation.UP) {
      orientation[0] = Orientation.RIGHT;  // lhs
      orientation[1] = Orientation.DOWN;  // rhs
      return orientation;
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.DOWN) {
      orientation[0] = Orientation.RIGHT;  // lhs
      orientation[1] = Orientation.UP;  // rhs
      orientation[0].reversed = true;
      return orientation;  // reversed 1
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.RIGHT) {
      orientation[0] = Orientation.RIGHT;  // lhs
      orientation[1] = Orientation.RIGHT;  // rhs
      return orientation;
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.LEFT) {
      orientation[0] = Orientation.RIGHT;  // lhs
      orientation[1] = Orientation.LEFT;  // rhs
      orientation[0].reversed = true;
      return orientation;  // reversed 1
    }
    return orientation;
  }
}
