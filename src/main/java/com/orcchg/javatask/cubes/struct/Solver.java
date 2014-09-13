package com.orcchg.javatask.cubes.struct;

public class Solver {
  private Cube[] mCubes;
  private static int internalCounter = 0;
  
  public Solver() {
    mCubes = new Cube[6];
  }
  
  public void addCube(final Cube cube) {
    if (internalCounter >= 6) {
      return;
    }
    mCubes[internalCounter++] = cube;
  }
  
  public Cube getCube(int index) {
    return mCubes[index];
  }
  
  public void printCubes() {
    for (Cube cube : mCubes) {
      System.out.println(cube);
    }
  }
}
