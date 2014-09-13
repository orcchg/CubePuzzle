package com.orcchg.javatask.cubes.struct;

public class Matrix {
  public static final int EMPTY = 0;
  public static final int FULL = 1;
  
  public int[][] data = new int[10][15];
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 0; row < 10; ++row) {
      for (int col = 0; col < 15; ++col) {
        builder.append(data[row][col] == FULL ? 'o' : ' ');
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
