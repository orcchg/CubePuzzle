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
  
  public static class Feature {
    private final Orientation mOrientation;
    private final boolean mIsReversed;
    private boolean mIsMirrored;
    
    public Feature(final Builder builder) {
      mOrientation = builder.mOrientation;
      mIsReversed = builder.mIsReversed;
      mIsMirrored = builder.mIsMirrored;
    }
    
    public static class Builder {
      private Orientation mOrientation = Orientation.UP;
      private boolean mIsReversed = false;
      private boolean mIsMirrored = false;
      
      public Builder setOrientation(final Orientation orientation) {
        mOrientation = orientation;
        return this;
      }
      
      public Builder setReversed(boolean flag) {
        mIsReversed = flag;
        return this;
      }
      
      public Builder setMirrored(boolean flag) {
        mIsMirrored = flag;
        return this;
      }
      
      public Feature build() {
        Feature feature = new Feature(this);
        return feature;
      }
    }
    
    public Orientation getOrientation() {
      return mOrientation;
    }
    
    public boolean isReversed() {
      return mIsReversed;
    }
    
    public boolean isMirrored() {
      return mIsMirrored;
    }
    
    public void setMirrored(boolean flag) {
      mIsMirrored = flag;
    }
    
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(mOrientation)
             .append(":")
             .append(mIsReversed)
             .append(":")
             .append(mIsMirrored);
      return builder.toString();
    }
  }
  
  /**
   * @brief Transforms side while rotating cube from UP to 'rotation' orientation
   */
  public Feature transform(final Orientation side, final Orientation rotation) {
    switch (side) {
      case UP:
        switch (rotation) {
          case UP:
            return new Feature.Builder().setOrientation(Orientation.UP).build();
          case DOWN:
            return new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
          case RIGHT:
            return new Feature.Builder().setOrientation(Orientation.RIGHT).build();
          case LEFT:
            return new Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
        }
        break;
      case DOWN:
        switch (rotation) {
          case UP:
            return new Feature.Builder().setOrientation(Orientation.DOWN).build();
          case DOWN:
            return new Feature.Builder().setOrientation(Orientation.UP).setReversed(true).build();
          case RIGHT:
            return new Feature.Builder().setOrientation(Orientation.LEFT).build();
          case LEFT:
            return new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();
        }
        break;
      case RIGHT:
        switch (rotation) {
          case UP:
            return new Feature.Builder().setOrientation(Orientation.RIGHT).build();
          case DOWN:
            return new Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
          case RIGHT:
            return new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
          case LEFT:
            return new Feature.Builder().setOrientation(Orientation.UP).build();
        }
        break;
      case LEFT:
        switch (rotation) {
          case UP:
            return new Feature.Builder().setOrientation(Orientation.LEFT).build();
          case DOWN:
            return new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();
          case RIGHT:
            return new Feature.Builder().setOrientation(Orientation.UP).setReversed(true).build();
          case LEFT:
            return new Feature.Builder().setOrientation(Orientation.DOWN).build();
        }
        break;
    }
    return null;
  }
  
  public static Orientation mirror(final Orientation orientation) {
    switch (orientation) {
      case UP:
      case DOWN:
        return orientation;
      case RIGHT:
        return Orientation.LEFT;
      case LEFT:
        return Orientation.RIGHT;
    }
    return null;
  }
  
  public static boolean makeMirrored(Orientation lhs, Orientation rhs, boolean direct, boolean reversed) {
    if (lhs == Orientation.UP && rhs == Orientation.UP) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.UP && rhs == Orientation.DOWN) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.UP && rhs == Orientation.RIGHT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.UP && rhs == Orientation.LEFT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.DOWN && rhs == Orientation.UP) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.DOWN) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.RIGHT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.LEFT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.RIGHT && rhs == Orientation.UP) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.DOWN) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.RIGHT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.LEFT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.LEFT && rhs == Orientation.UP) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.DOWN) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.RIGHT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return false;
      } else if (reversed) {
        return true;
      } else {
        return false;
      }
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.LEFT) {
      if (direct && reversed) {
        return false;
      } else if (direct) {
        return true;
      } else if (reversed) {
        return false;
      } else {
        return false;
      }
    }
    return false;
  }
  
  /**
   * @brief two cubes are matched with 'lhs' and 'rhs' sides correspondingly.
   *     In order to combine them into vertical straight line, we should
   *     rotate these cubes, as this method says
   *     
   * @param left cube side
   * @param rigth cube side
   * 
   * @return pair of orientations for cubes, which they should be placed
   *     in order to get continuous vertical segment of two pieces,
   *     adjacent along these two given sides
   * 
   * @details method also collects information about reversed order of matching
   *     between two sides
   * 
   * @note this method only works if one needs to put two pieces into vertical
   *     straight segment, left piece at the bottom, right piece at the top of segment
   */
  public static Feature[] getValidOrientation(Orientation lhs, Orientation rhs) {
    Feature orientation[] = new Feature[2];
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.UP && rhs == Orientation.UP) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.UP).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();  // rhs
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.UP && rhs == Orientation.DOWN) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.UP).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.UP).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.UP && rhs == Orientation.RIGHT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.UP).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();  // rhs
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.UP && rhs == Orientation.LEFT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.UP).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // rhs
      return orientation;
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.DOWN && rhs == Orientation.UP) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.DOWN).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.DOWN).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.DOWN) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.UP).build();  // rhs
      return orientation;  // reversed 1
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.RIGHT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.DOWN).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.RIGHT).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.DOWN && rhs == Orientation.LEFT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // rhs
      return orientation;  // reversed 1
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.RIGHT && rhs == Orientation.UP) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();  // rhs
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.DOWN) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.UP).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.RIGHT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();  // rhs
      return orientation;  // reversed 2
    }
    if (lhs == Orientation.RIGHT && rhs == Orientation.LEFT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // rhs
      return orientation;
    }
    
    // ------------------------------------------------------------------------
    if (lhs == Orientation.LEFT && rhs == Orientation.UP) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.RIGHT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.DOWN).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.DOWN) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.UP).build();  // rhs
      return orientation;  // reversed 1
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.RIGHT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.RIGHT).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.RIGHT).build();  // rhs
      return orientation;
    }
    if (lhs == Orientation.LEFT && rhs == Orientation.LEFT) {
      orientation[0] = new Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();  // lhs
      orientation[1] = new Feature.Builder().setOrientation(Orientation.LEFT).build();  // rhs
      return orientation;  // reversed 1
    }
    return orientation;
  }
}
