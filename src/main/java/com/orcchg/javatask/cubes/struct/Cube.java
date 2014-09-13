package com.orcchg.javatask.cubes.struct;

public class Cube {
  private Orientation mOrientation = Orientation.UP;
  private Side[] mSides = new Side[4];
  private Side[] mTemp = new Side[4];
  
  public Cube(Side up, Side down, Side right, Side left) {
    mSides[0] = up;
    mSides[1] = down;
    mSides[2] = right;
    mSides[3] = left;
    backup();
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
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
//    switch (mOrientation) {
//      case UP:
//        builder.append(mSides[0]).append("\n");
//        builder.append(mSides[3].cells[1].toChar()).append("ooo").append(mSides[2].cells[1].toChar()).append("\n");
//        builder.append(mSides[3].cells[2].toChar()).append("ooo").append(mSides[2].cells[2].toChar()).append("\n");
//        builder.append(mSides[3].cells[3].toChar()).append("ooo").append(mSides[2].cells[3].toChar()).append("\n");
//        builder.append(mSides[1]);
//        break;
//      case DOWN:
//        builder.append(mSides[1]).append("\n");
//        builder.append(mSides[2].cells[1].toChar()).append("ooo").append(mSides[3].cells[1].toChar()).append("\n");
//        builder.append(mSides[2].cells[2].toChar()).append("ooo").append(mSides[3].cells[2].toChar()).append("\n");
//        builder.append(mSides[2].cells[3].toChar()).append("ooo").append(mSides[3].cells[3].toChar()).append("\n");
//        builder.append(mSides[0]);
//        break;
//      case RIGHT:
//        builder.append(mSides[3]).append("\n");
//        builder.append(mSides[1].cells[1].toChar()).append("ooo").append(mSides[0].cells[1].toChar()).append("\n");
//        builder.append(mSides[1].cells[2].toChar()).append("ooo").append(mSides[0].cells[2].toChar()).append("\n");
//        builder.append(mSides[1].cells[3].toChar()).append("ooo").append(mSides[0].cells[3].toChar()).append("\n");
//        builder.append(mSides[2]);
//        break;
//      case LEFT:
//        builder.append(mSides[2]).append("\n");
//        builder.append(mSides[0].cells[1].toChar()).append("ooo").append(mSides[1].cells[1].toChar()).append("\n");
//        builder.append(mSides[0].cells[2].toChar()).append("ooo").append(mSides[1].cells[2].toChar()).append("\n");
//        builder.append(mSides[0].cells[3].toChar()).append("ooo").append(mSides[1].cells[3].toChar()).append("\n");
//        builder.append(mSides[3]);
//        break;
//    }
    builder.append(mSides[0]).append("\n");
    builder.append(mSides[3].cells[1].toChar()).append("ooo").append(mSides[2].cells[1].toChar()).append("\n");
    builder.append(mSides[3].cells[2].toChar()).append("ooo").append(mSides[2].cells[2].toChar()).append("\n");
    builder.append(mSides[3].cells[3].toChar()).append("ooo").append(mSides[2].cells[3].toChar()).append("\n");
    builder.append(mSides[1]);
    return builder.toString();
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private void backup() {
    mTemp[0] = mSides[0].clone();
    mTemp[1] = mSides[1].clone();
    mTemp[2] = mSides[2].clone();
    mTemp[3] = mSides[3].clone();
  }
}
