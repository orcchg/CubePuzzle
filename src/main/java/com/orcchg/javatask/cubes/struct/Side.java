package com.orcchg.javatask.cubes.struct;

public class Side implements Cloneable {
  public static enum Cell {
    EMPTY, FULL;
    
    public char toChar() {
      return this == Cell.FULL ? 'o' : ' ';
    }
  }
  
  public Cell[] cells = new Cell[5];
  
  public Side(int[] side) {
    for (int i = 0; i < 5; ++i) {
      cells[i] = side[i] == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
    }
  }
  
  public Side(Cell[] cells) {
    System.arraycopy(cells, 0, this.cells, 0, 5);
  }
  
  public Side(int prev_far, int prev_close, int middle, int next_close, int next_far) {
    cells[0] = prev_far == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
    cells[1] = prev_close == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
    cells[2] = middle == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
    cells[3] = next_close == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
    cells[4] = next_far == Matrix.FULL ? Cell.FULL : Cell.EMPTY;
  }
  
  public Side reverse() {
    for (int i = 0; i < 2; ++i) {
      Cell temp = cells[i];
      cells[i] = cells[cells.length - 1 - i];
      cells[cells.length - 1 - i] = temp;
    }
    return this;
  }
  
  public byte getNumericRepresentation() {
    byte number = 0;
    int power = 0;
    for (Cell cell : cells) {
      number += cell == Cell.FULL ? Math.pow(2, power) : 0;
      ++power;
    }
    return number;
  }
  
  @Override
  public Side clone() {
    Side side = new Side(cells);
    return side;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Cell cell : cells) {
      char label = cell == Cell.FULL ? 'o' : ' ';
      builder.append(label);
    }
    return builder.toString();
  }
}
