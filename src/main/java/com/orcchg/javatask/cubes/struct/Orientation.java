package com.orcchg.javatask.cubes.struct;

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
    private final boolean mIsMirrored;
    
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
