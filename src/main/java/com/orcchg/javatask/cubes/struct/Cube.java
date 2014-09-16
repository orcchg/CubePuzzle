package com.orcchg.javatask.cubes.struct;

/** 
 * @brief Represents single piece of puzzle
 * @note Sorry for strange class name :(, the reason is historical
 */
public class Cube {
  private final int mID;
  private Orientation mOrientation = Orientation.UP;
  private Side[] mSides = new Side[4];
  private Side[] mTemp = new Side[4];
  
  public Cube(int id, Side up, Side down, Side right, Side left) {
    mID = id;
    mSides[0] = up;
    mSides[1] = down;
    mSides[2] = right;
    mSides[3] = left;
    backup();
  }
  
  public int getID() {
    return mID;
  }
  
  public Side getSide(Orientation orientation) {
    Side side = null;
    switch (orientation) {
      case UP:
        side = mSides[0];
        break;
      case DOWN:
        side = mSides[1];
        break;
      case RIGHT:
        side = mSides[2];
        break;
      case LEFT:
        side = mSides[3];
        break;
    }
    return side;
  }
  
  public boolean isSymmetric() {
    return (mSides[0].equals(mSides[1]) && mSides[2].equals(mSides[3]));
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + mID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Cube other = (Cube) obj;
    if (mID != other.mID)
      return false;
    return true;
  }
  
  public Cube setOrientation(Orientation orientation) {
    switch (mOrientation) {
      case UP:
        switch (orientation) {
          case UP:
            break;
          case DOWN:
            rotate(); rotate();
            break;
          case RIGHT:
            rotate();
            break;
          case LEFT:
            rotate(); rotate(); rotate();
            break;
        }
        break;
      case DOWN:
        switch (orientation) {
          case UP:
            rotate(); rotate();
            break;
          case DOWN:
            break;
          case RIGHT:
            rotate(); rotate(); rotate();
            break;
          case LEFT:
            rotate();
            break;
        }
        break;
      case RIGHT:
        switch (orientation) {
          case UP:
            rotate(); rotate(); rotate();
            break;
          case DOWN:
            rotate();
            break;
          case RIGHT:
            break;
          case LEFT:
            rotate(); rotate();
            break;
        }
        break;
      case LEFT:
        switch (orientation) {
          case UP:
            rotate();
            break;
          case DOWN:
            rotate(); rotate(); rotate();
            break;
          case RIGHT:
            rotate(); rotate();
            break;
          case LEFT:
            break;
        }
        break;
    }
    mOrientation = Orientation.UP;
    return this;
  }
  
  public Cube getOriented(Orientation orientation) {
    Cube oriented = new Cube(this);
    oriented.setOrientation(orientation);
    return oriented;
  }

  public Cube rotate() {  // clockwise rotation by 90 degrees
    switch (mOrientation) {
      case UP:
        mOrientation = Orientation.RIGHT;
        mSides[0] = mTemp[3].clone().reverse();
        mSides[1] = mTemp[2].clone().reverse();
        mSides[2] = mTemp[0].clone();
        mSides[3] = mTemp[1].clone();
        break;
      case DOWN:
        mOrientation = Orientation.LEFT;
        mSides[0] = mTemp[2].clone();
        mSides[1] = mTemp[3].clone();
        mSides[2] = mTemp[1].clone().reverse();
        mSides[3] = mTemp[0].clone().reverse();
        break;
      case RIGHT:
        mOrientation = Orientation.DOWN;
        mSides[0] = mTemp[1].clone().reverse();
        mSides[1] = mTemp[0].clone().reverse();
        mSides[2] = mTemp[3].clone().reverse();
        mSides[3] = mTemp[2].clone().reverse();
        break;
      case LEFT:
        mOrientation = Orientation.UP;
        mSides[0] = mTemp[0].clone();
        mSides[1] = mTemp[1].clone();
        mSides[2] = mTemp[2].clone();
        mSides[3] = mTemp[3].clone();
        break;
    }
    return this;
  }
  
  public Cube getRotated() {  // clockwise rotation by 90 degrees
    Cube rotated = new Cube(this);  // deep copy
    rotated.rotate();
    return rotated;
  }
  
  public Cube mirror() {  // mirror at vertical line
    mSides[0] = mTemp[0].clone().reverse();
    mSides[1] = mTemp[1].clone().reverse();
    mSides[2] = mTemp[3].clone();
    mSides[3] = mTemp[2].clone();
    backup();
    
    switch (mOrientation) {
      case UP:
      case DOWN:
        break;
      case RIGHT:
        mOrientation = Orientation.LEFT;
        break;
      case LEFT:
        mOrientation = Orientation.RIGHT;
        break;
    }
    
    return this;
  }
  
  public Cube getMirrored() {
    Cube mirrored = new Cube(this);
    mirrored.mirror();
    return mirrored;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(mSides[0]).append("\n");
    builder.append(mSides[3].cells[1].toChar()).append("ooo").append(mSides[2].cells[1].toChar()).append("\n");
    builder.append(mSides[3].cells[2].toChar()).append("ooo").append(mSides[2].cells[2].toChar()).append("\n");
    builder.append(mSides[3].cells[3].toChar()).append("ooo").append(mSides[2].cells[3].toChar()).append("\n");
    builder.append(mSides[1]);
    return builder.toString();
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  Cube(final Cube rhs) {  // deep copy
    mID = rhs.mID;
    
    switch (rhs.mOrientation) {
      case UP:
        mOrientation = Orientation.UP;
        break;
      case DOWN:
        mOrientation = Orientation.DOWN;
        break;
      case RIGHT:
        mOrientation = Orientation.RIGHT;
        break;
      case LEFT:
        mOrientation = Orientation.LEFT;
        break;
    }
    
    mSides[0] = rhs.mSides[0].clone();
    mSides[1] = rhs.mSides[1].clone();
    mSides[2] = rhs.mSides[2].clone();
    mSides[3] = rhs.mSides[3].clone();
    backup();
  }
  
  private void backup() {
    mTemp[0] = mSides[0].clone();
    mTemp[1] = mSides[1].clone();
    mTemp[2] = mSides[2].clone();
    mTemp[3] = mSides[3].clone();
  }
}
