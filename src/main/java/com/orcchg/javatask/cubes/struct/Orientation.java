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
  
  /**
   * @brief checks whether rhs-cube should be mirrored in case it's 'rhs'-side
   *     matches 'lhs' side of lhs-cube in direct or reversed order, and then
   *     two cubes form vertical straight line
   *     
   * @param lhs - left cube side
   * @param rhs - right cube side
   * @param direct - two sides match in direct order
   * @param reversed - two sides match in reversed order
   * @return should rhs-cube be mirrored to fulfill the match forming vertical segment
   */
  public static boolean makeMirroredVertical(Orientation lhs, Orientation rhs, boolean direct, boolean reversed) {
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
   * @brief given an assumption that rhs-cube is in vertical direction,
   *     this method indicates, whether to mirror or not lhs-cube which matches
   *     to rhs-cube's left side with its 'lhs' side, forming horizontal straight line
   *     
   * @param lhs - left cube side
   * @param direct - left cube side matches left side of rhs-cube in direct order
   * @param reversed - left cube side matches left side of rhs-cube in reversed order
   * @return should lhs-cube be mirrored to form horizontal straight line
   */
  public static boolean makeMirroredLeft(Orientation lhs, boolean direct, boolean reversed) {
    switch (lhs) {
      case UP:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return false;
        } else if (reversed) {
          return true;
        } else {
          return false;
        }
      case DOWN:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return true;
        } else if (reversed) {
          return false;
        } else {
          return false;
        }
      case RIGHT:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return false;
        } else if (reversed) {
          return true;
        } else {
          return false;
        }
      case LEFT:
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
   * @brief given an assumption that lhs-cube is in vertical direction,
   *     this method indicates, whether to mirror or not rhs-cube which matches
   *     to lhs-cube's right side with its 'rhs' side, forming horizontal straight line
   *     
   * @param rhs - right cube side
   * @param direct - right cube side matches right side of lhs-cube in direct order
   * @param reversed - right cube side matches right side of lhs-cube in reversed order
   * @return should rhs-cube be mirrored to form horizontal straight line
   */
  public static boolean makeMirroredRight(Orientation rhs, boolean direct, boolean reversed) {
    switch (rhs) {
      case UP:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return true;
        } else if (reversed) {
          return false;
        } else {
          return false;
        }
      case DOWN:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return false;
        } else if (reversed) {
          return true;
        } else {
          return false;
        }
      case RIGHT:
        if (direct && reversed) {
          return false;
        } else if (direct) {
          return true;
        } else if (reversed) {
          return false;
        } else {
          return false;
        }
      case LEFT:
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
    return false;
  }
  
  /**
   * @brief two cubes are matched with 'lhs' and 'rhs' sides correspondingly.
   *     In order to combine them into vertical straight line, we should
   *     rotate these cubes, as this method says
   *     
   * @param lhs - left cube side
   * @param rhs - rigth cube side
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
