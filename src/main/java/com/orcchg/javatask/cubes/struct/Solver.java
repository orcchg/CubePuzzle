package com.orcchg.javatask.cubes.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
  
  // match two cubes side (of lhs one) by side (of rhs one)
  public boolean match(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    byte lhs_bits = lhs.getSide(lhs_orient).getNumericRepresentation();
    byte rhs_bits = rhs.getSide(rhs_orient).getNumericRepresentation();
    byte mask = 31;
    byte flip = (byte) ((~lhs_bits) & mask);
    //System.out.println("LHS: " + lhs_bits + ", FLIP: " + flip + ", RHS: " + rhs_bits);
    return flip == rhs_bits;
  }
  
  public void printCubes() {
    for (Cube cube : mCubes) {
      System.out.println(cube);
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private void singleSolution() {
    List<Cube> answerT = new ArrayList<>();
    
    for (Cube cube : mCubes) {
      Set<Cube> set = new HashSet<>(Arrays.asList(mCubes));
      set.remove(cube);
      
      Queue<Orientation> orientations = new LinkedList<>(Arrays.asList(Orientation.entries));
      Queue<Cube> another_cubes = new LinkedList<>();
      another_cubes.addAll(set);
      
      while (!another_cubes.isEmpty()) {
        while (!orientations.isEmpty()) {
          Cube another_cube = another_cubes.peek();
          Orientation orientation = orientations.peek();
          boolean result = match(cube, Orientation.UP, another_cube, orientation);
          if (result) {
            answerT.add(cube);
            answerT.add(another_cube.setOrientation(orientation));
            orientations.poll();
            break;
          } else {
            orientations.poll();
          }
        }
      }
      
      
      
    }
  }
  
  private List<Cube> findMiddleElementsT() {
    List<Cube> mid_elems = new ArrayList<>();
    for (Cube cube : mCubes) {
      
    }
    return mid_elems;
  }
}
