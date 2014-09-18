package com.orcchg.javatask.cubes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.orcchg.javatask.cubes.struct.Cube;
import com.orcchg.javatask.cubes.struct.Matrix;
import com.orcchg.javatask.cubes.struct.Side;
import com.orcchg.javatask.cubes.struct.Solver;

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
    
    long start = System.currentTimeMillis();
    MainSolution instance = new MainSolution();
    instance.readCubes(args[0]);
    
    // instance.mSolver.testMirror();
    // instance.mSolver.testFlip();
    
    instance.mSolver.exhaustiveSolve();
    instance.writeToFile("output.txt", instance.mSolver.getSolution());
    long elapsed = System.currentTimeMillis() - start;
    
    System.out.println("Total solutions in T unfolded form: " + instance.mSolver.totalUnfoldedT() +
                       "\nTotal solutions in X unfolded form: " + instance.mSolver.totalUnfoldedX() +
                       "\nSolutions are in file 'output.txt'" +
                       "\nTime elapsed: " + elapsed / 1000 + " s");
  }

  public void readCubes(String filename) {
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
  
  public void writeToFile(final String filename, final String content) {
    File file = new File(filename);
    
    try (FileOutputStream fout = new FileOutputStream(file);) {
      if (!file.exists()) {
        file.createNewFile();
      }
      
      byte[] contentInBytes = content.getBytes();
      
      fout.write(contentInBytes);
      fout.flush();
      fout.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
