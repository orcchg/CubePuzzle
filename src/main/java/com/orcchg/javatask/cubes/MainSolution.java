package com.orcchg.javatask.cubes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.orcchg.javatask.cubes.struct.Cube;
import com.orcchg.javatask.cubes.struct.Matrix;
import com.orcchg.javatask.cubes.struct.Orientation;
import com.orcchg.javatask.cubes.struct.Side;
import com.orcchg.javatask.cubes.struct.Solver;
import com.orcchg.javatask.cubes.util.Util;

public class MainSolution {
  private Solver mSolver;

  public static void printUsage() {
    System.out.println("<MainSolution> input.txt");
  }
  
  public MainSolution() {
    mSolver = new Solver();
  }
  
  public static void main(String[] args) {
    if (args.length < 1) {
      printUsage();
      return;
    }
    MainSolution instance = new MainSolution();
    instance.readCubes(args[0]);
    
//    System.err.println(instance.mSolver.match(
//        instance.mSolver.getCube(1).rotate(),
//        Orientation.RIGHT,
//        instance.mSolver.getCube(3).rotate().rotate().rotate(),
//        Orientation.LEFT));
    List<Integer> list = new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5}));
    List<List<Integer>> answer = Util.allConjunctions(list, 4);
    System.out.println("SIZE: " + answer.size());
    Util.printListOfLists(answer);
  }

  /* Private methods */
  // --------------------------------------------------------------------------
  private void readCubes(String filename) {
    Matrix matrix = new Matrix();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line = null;
      int line_counter = 0;
      while ((line = br.readLine()) != null && line_counter < 10) {
        for (int column_index = 0; column_index < 15; ++column_index) {
          char character = line.charAt(column_index);
          matrix.data[line_counter][column_index] = character == 'o' ? Matrix.FULL : Matrix.EMPTY;
        }
        ++line_counter;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    int cube_id = 0;
    
    // top line
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[0][0], matrix.data[0][1], matrix.data[0][2], matrix.data[0][3], matrix.data[0][4]),   // up
        new Side(matrix.data[4][0], matrix.data[4][1], matrix.data[4][2], matrix.data[4][3], matrix.data[4][4]),   // down
        new Side(matrix.data[0][4], matrix.data[1][4], matrix.data[2][4], matrix.data[3][4], matrix.data[4][4]),   // right
        new Side(matrix.data[0][0], matrix.data[1][0], matrix.data[2][0], matrix.data[3][0], matrix.data[4][0])    // left
        ));
    
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[0][5], matrix.data[0][6], matrix.data[0][7], matrix.data[0][8], matrix.data[0][9]),   // up
        new Side(matrix.data[4][5], matrix.data[4][6], matrix.data[4][7], matrix.data[4][8], matrix.data[4][9]),   // down
        new Side(matrix.data[0][9], matrix.data[1][9], matrix.data[2][9], matrix.data[3][9], matrix.data[4][9]),   // right
        new Side(matrix.data[0][5], matrix.data[1][5], matrix.data[2][5], matrix.data[3][5], matrix.data[4][5])    // left
        ));
    
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[0][10], matrix.data[0][11], matrix.data[0][12], matrix.data[0][13], matrix.data[0][14]),   // up
        new Side(matrix.data[4][10], matrix.data[4][11], matrix.data[4][12], matrix.data[4][13], matrix.data[4][14]),   // down
        new Side(matrix.data[0][14], matrix.data[1][14], matrix.data[2][14], matrix.data[3][14], matrix.data[4][14]),   // right
        new Side(matrix.data[0][10], matrix.data[1][10], matrix.data[2][10], matrix.data[3][10], matrix.data[4][10])    // left
        ));
    
    // bottom line
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[5][0], matrix.data[5][1], matrix.data[5][2], matrix.data[5][3], matrix.data[5][4]),   // up
        new Side(matrix.data[9][0], matrix.data[9][1], matrix.data[9][2], matrix.data[9][3], matrix.data[9][4]),   // down
        new Side(matrix.data[5][4], matrix.data[6][4], matrix.data[7][4], matrix.data[8][4], matrix.data[9][4]),   // right
        new Side(matrix.data[5][0], matrix.data[6][0], matrix.data[7][0], matrix.data[8][0], matrix.data[9][0])    // left
        ));
    
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[5][5], matrix.data[5][6], matrix.data[5][7], matrix.data[5][8], matrix.data[5][9]),   // up
        new Side(matrix.data[9][5], matrix.data[9][6], matrix.data[9][7], matrix.data[9][8], matrix.data[9][9]),   // down
        new Side(matrix.data[5][9], matrix.data[6][9], matrix.data[7][9], matrix.data[8][9], matrix.data[9][9]),   // right
        new Side(matrix.data[5][5], matrix.data[6][5], matrix.data[7][5], matrix.data[8][5], matrix.data[9][5])    // left
        ));
    
    mSolver.addCube(new Cube(
        cube_id++,
        new Side(matrix.data[5][10], matrix.data[5][11], matrix.data[5][12], matrix.data[5][13], matrix.data[5][14]),   // up
        new Side(matrix.data[9][10], matrix.data[9][11], matrix.data[9][12], matrix.data[9][13], matrix.data[9][14]),   // down
        new Side(matrix.data[5][14], matrix.data[6][14], matrix.data[7][14], matrix.data[8][14], matrix.data[9][14]),   // right
        new Side(matrix.data[5][10], matrix.data[6][10], matrix.data[7][10], matrix.data[8][10], matrix.data[9][10])    // left
        ));
  }
}
