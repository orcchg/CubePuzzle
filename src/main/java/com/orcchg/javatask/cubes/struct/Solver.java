package com.orcchg.javatask.cubes.struct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.orcchg.javatask.cubes.util.Util;

public class Solver {
  private Map<Integer, Cube> mCubes;
  private static int internalCounter = 0;
  
  private static class Folding {
    private List<Cube> cubes;
    private boolean isUpper;
    private Form form;
    
    private enum Form { T, X };
    
    private Folding(final List<Cube> cubes, boolean isUpper, Form form) {
      this.cubes = cubes;
      this.isUpper = isUpper;
      this.form = form;
    }
  }
  
  private List<Folding> mUnfoldedT;
  private List<Folding> mUnfoldedX;
  
  public Solver() {
    mCubes = new HashMap<Integer, Cube>();
    mUnfoldedT = new ArrayList<Folding>();
    mUnfoldedX = new ArrayList<Folding>();
  }
  
  public void addCube(final Cube cube) {
    if (internalCounter >= 6) {
      return;
    }
    mCubes.put(cube.getID(), cube);
    ++internalCounter;
  }
  
  public Cube getCube(int index) {
    return mCubes.get(index);
  }
  
  public int totalUnfoldedT() {
    return mUnfoldedT.size();
  }
  
  public int totalUnfoldedX() {
    return mUnfoldedX.size();
  }
  
  // match two cubes side (of lhs one) by side (of rhs one)
  public static boolean match(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    Side lhs_side = lhs.getSide(lhs_orient);
    Side rhs_side = rhs.getSide(rhs_orient);
    boolean result = true;
    for (int i = 0; i < 5; ++i) {
      result = result && ((lhs_side.cells[i].toInt() + rhs_side.cells[i].toInt()) < 2);
    }
    return result;
  }
  
  public static boolean matchReversed(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    Side lhs_side = lhs.getSide(lhs_orient);
    Side rhs_side = rhs.getSide(rhs_orient).getReversed();
    boolean result = true;
    for (int i = 0; i < 5; ++i) {
      result = result && ((lhs_side.cells[i].toInt() + rhs_side.cells[i].toInt()) < 2);
    }
    return result;
  }
  
  public static boolean fullMatch(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    byte lhs_bits = lhs.getSide(lhs_orient).getNumericRepresentation();
    byte rhs_bits = rhs.getSide(rhs_orient).getNumericRepresentation();
    byte mask = 31;
    byte flip = (byte) ((~lhs_bits) & mask);
    return flip == rhs_bits;
  }
  
  public static boolean fullMatchReversed(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    byte lhs_bits = lhs.getSide(lhs_orient).getNumericRepresentation();
    byte rhs_bits = rhs.getSide(rhs_orient).getReversed().getNumericRepresentation();
    byte mask = 31;
    byte flip = (byte) ((~lhs_bits) & mask);
    return flip == rhs_bits;
  }
  
  // XXX: start
  public void exhaustiveSolve() {
    if (mCubes.size() != 6) {
      throw new IllegalStateException("Number of cubes is not equal to six! No solution.");
    }
    
    List<Integer> cube_ids = new ArrayList<>(6);
    for (Map.Entry<Integer, Cube> entry : mCubes.entrySet()) {
      cube_ids.add(entry.getKey());
    }
    
    List<List<Integer>> permutations = Util.allCombinations(cube_ids);
    int perm_i = 0;
    permutations_loop: for (List<Integer> permutation : permutations) {
      
     long start = System.currentTimeMillis();

      for (int mirror_i1 = 0; mirror_i1 < 2; ++mirror_i1) {
        for (int rotate_i1 = 0; rotate_i1 < 4; ++rotate_i1) {
          Cube cube_1 = new Cube(mCubes.get(permutation.get(0)));
          if (mirror_i1 == 1) {
            cube_1 = cube_1.getMirrored();
          }
          switch (rotate_i1) {
            case 0:
              break;
            case 1:
              cube_1 = cube_1.getRotated();
              break;
            case 2:
              cube_1 = cube_1.getRotated().getRotated();
              break;
            case 3:
              cube_1 = cube_1.getRotated().getRotated().getRotated();
              break;
          }
          // have cube 1
          
          for (int mirror_i2 = 0; mirror_i2 < 2; ++mirror_i2) {
            for (int rotate_i2 = 0; rotate_i2 < 4; ++rotate_i2) {
              Cube cube_2 = new Cube(mCubes.get(permutation.get(1)));
              if (mirror_i2 == 1) {
                cube_2 = cube_2.getMirrored();
              }
              switch (rotate_i2) {
                case 0:
                  break;
                case 1:
                  cube_2 = cube_2.getRotated();
                  break;
                case 2:
                  cube_2 = cube_2.getRotated().getRotated();
                  break;
                case 3:
                  cube_2 = cube_2.getRotated().getRotated().getRotated();
                  break;
              }
              // have cube 2
              
              for (int mirror_i3 = 0; mirror_i3 < 2; ++mirror_i3) {
                for (int rotate_i3 = 0; rotate_i3 < 4; ++rotate_i3) {
                  Cube cube_3 = new Cube(mCubes.get(permutation.get(2)));
                  if (mirror_i3 == 1) {
                    cube_3 = cube_3.getMirrored();
                  }
                  switch (rotate_i3) {
                    case 0:
                      break;
                    case 1:
                      cube_3 = cube_3.getRotated();
                      break;
                    case 2:
                      cube_3 = cube_3.getRotated().getRotated();
                      break;
                    case 3:
                      cube_3 = cube_3.getRotated().getRotated().getRotated();
                      break;
                  }
                  // have cube 3
                  
                  for (int mirror_i4 = 0; mirror_i4 < 2; ++mirror_i4) {
                    for (int rotate_i4 = 0; rotate_i4 < 4; ++rotate_i4) {
                      Cube cube_4 = new Cube(mCubes.get(permutation.get(3)));
                      if (mirror_i4 == 1) {
                        cube_4 = cube_4.getMirrored();
                      }
                      switch (rotate_i4) {
                        case 0:
                          break;
                        case 1:
                          cube_4 = cube_4.getRotated();
                          break;
                        case 2:
                          cube_4 = cube_4.getRotated().getRotated();
                          break;
                        case 3:
                          cube_4 = cube_4.getRotated().getRotated().getRotated();
                          break;
                      }
                      // have cube 4
                      
                      for (int mirror_i5 = 0; mirror_i5 < 2; ++mirror_i5) {
                        for (int rotate_i5 = 0; rotate_i5 < 4; ++rotate_i5) {
                          Cube cube_5 = new Cube(mCubes.get(permutation.get(4)));
                          if (mirror_i5 == 1) {
                            cube_5 = cube_5.getMirrored();
                          }
                          switch (rotate_i5) {
                            case 0:
                              break;
                            case 1:
                              cube_5 = cube_5.getRotated();
                              break;
                            case 2:
                              cube_5 = cube_5.getRotated().getRotated();
                              break;
                            case 3:
                              cube_5 = cube_5.getRotated().getRotated().getRotated();
                              break;
                          }
                          // have cube 5
                          
                          for (int mirror_i6 = 0; mirror_i6 < 2; ++mirror_i6) {
                            for (int rotate_i6 = 0; rotate_i6 < 4; ++rotate_i6) {
                              Cube cube_6 = new Cube(mCubes.get(permutation.get(5)));
                              if (mirror_i6 == 1) {
                                cube_6 = cube_6.getMirrored();
                              }
                              switch (rotate_i6) {
                                case 0:
                                  break;
                                case 1:
                                  cube_6 = cube_6.getRotated();
                                  break;
                                case 2:
                                  cube_6 = cube_6.getRotated().getRotated();
                                  break;
                                case 3:
                                  cube_6 = cube_6.getRotated().getRotated().getRotated();
                                  break;
                              }
                              // have cube 6
                              
                              // have got all cubes, make a folding
                              List<Cube> segment = new ArrayList<>();
                              segment.add(cube_1);
                              segment.add(cube_2);
                              segment.add(cube_3);
                              segment.add(cube_4);
                              segment.add(cube_5);
                              segment.add(cube_6);
                              Folding fT_upper = new Folding(segment, true, Folding.Form.T);
                              Folding fT_lower = new Folding(segment, false, Folding.Form.T);
                              Folding fX_upper = new Folding(segment, true, Folding.Form.X);
                              Folding fX_lower = new Folding(segment, false, Folding.Form.X);
                              
                              if (isUnfoldedTValid(fT_upper)) {
                                //foldings.add(fT_upper);
                                mUnfoldedT.add(fT_upper);
                              }
                              if (isUnfoldedTValid(fT_lower)) {
                                //foldings.add(fT_lower);
                                mUnfoldedT.add(fT_lower);
                              }
                              if (isUnfoldedXValid(fX_upper)) {
                                //foldings.add(fX_upper);
                                mUnfoldedX.add(fX_upper);
                              }
                              if (isUnfoldedXValid(fX_lower)) {
                                //foldings.add(fX_lower);
                                mUnfoldedX.add(fX_lower);
                              }
                               
                            }  // cube 6
                          }
                           
                        }  // cube 5
                      }
                       
                    }  // cube 4
                  }
                  
                }  // cube 3
              }
              
            }  // cube 2
          }
            
        }  // cube 1
      }
      System.gc();
      long elapsed = System.currentTimeMillis() - start;
      ++perm_i;
      System.out.println("Permutation [" + perm_i + " / 720] has been processed. Time elapsed: " + elapsed / 1000.0);
    }  // permutations_loop
    
    mUnfoldedT = removeDuplicates(mUnfoldedT);
    mUnfoldedX = removeDuplicates(mUnfoldedX);
    // XXX: end
  }  // end exhaustive solution
  
  /*
   *  Someday there was an old, obsolete, monstrous and wrong solution. I've removed that completely.
   *  By the way, it could be found in repository.
   */
  
  public String getSolution() {
    StringBuilder solution = new StringBuilder();
    for (Folding cubes : mUnfoldedT) {
      // print all solutions as T unfolded form
      solution.append(unfoldedTtoString(cubes));
    }
    
    // ------------------------------------------------------------------------
    for (Folding cubes : mUnfoldedX) {
      // print all solutions as X unfolded form
      solution.append(unfoldedXtoString(cubes));
    }
    return solution.toString();
  }
  
  public void printCubes() {
    for (Map.Entry<Integer, Cube> entry : mCubes.entrySet()) {
      System.out.println(entry.getValue());
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private boolean isSegmentARing(final LinkedList<Cube> segment) {
    return match(segment.get(0), Orientation.DOWN, segment.get(segment.size() - 1), Orientation.UP);
  }
  
  private boolean isSegmentValid(final LinkedList<Cube> segment) {
    for (int i = 0; i + 1 < segment.size(); ++i) {
      boolean result = match(segment.get(i), Orientation.UP, segment.get(i + 1), Orientation.DOWN);
      if (!result) {
        return false;
      }
    }
    return true;
  }
  
  private LinkedList<Cube> mirrorSegment(final LinkedList<Cube> segment) {
    LinkedList<Cube> mirrored = new LinkedList<Cube>();
    for (Cube cube : segment) {
      Cube mirror_cube = new Cube(cube);
      mirror_cube.mirror();
      mirrored.add(mirror_cube);
    }
    return mirrored;
  }
  
  private boolean isUnfoldedTValid(final Folding folding) {
    LinkedList<Cube> ring = new LinkedList<>();
    ring.addAll(folding.cubes.subList(0, 4));
    boolean segment_valid = isSegmentValid(ring) && isSegmentARing(ring);
    
    boolean left = false;
    boolean right = false;
    if (folding.isUpper) {
      boolean left_right = match(folding.cubes.get(4), Orientation.RIGHT, folding.cubes.get(3), Orientation.LEFT);
      boolean left_down = matchReversed(folding.cubes.get(4), Orientation.DOWN, folding.cubes.get(2), Orientation.LEFT);
      boolean left_up = match(folding.cubes.get(4), Orientation.UP, folding.cubes.get(0), Orientation.LEFT);
      boolean left_left = matchReversed(folding.cubes.get(4), Orientation.LEFT, folding.cubes.get(1), Orientation.LEFT);
      left = left_right && left_down && left_up && left_left;
      
      boolean right_left = match(folding.cubes.get(5), Orientation.LEFT, folding.cubes.get(3), Orientation.RIGHT);
      boolean right_down = match(folding.cubes.get(5), Orientation.DOWN, folding.cubes.get(2), Orientation.RIGHT);
      boolean right_up = matchReversed(folding.cubes.get(5), Orientation.UP, folding.cubes.get(0), Orientation.RIGHT);
      boolean right_right = matchReversed(folding.cubes.get(5), Orientation.RIGHT, folding.cubes.get(1), Orientation.RIGHT);
      right = right_left && right_down && right_up && right_right;
      
    } else {
      boolean left_right = match(folding.cubes.get(4), Orientation.RIGHT, folding.cubes.get(0), Orientation.LEFT);
      boolean left_down = matchReversed(folding.cubes.get(4), Orientation.DOWN, folding.cubes.get(3), Orientation.LEFT);
      boolean left_up = match(folding.cubes.get(4), Orientation.UP, folding.cubes.get(1), Orientation.LEFT);
      boolean left_left = matchReversed(folding.cubes.get(4), Orientation.LEFT, folding.cubes.get(2), Orientation.LEFT);
      left = left_right && left_down && left_up && left_left;
      
      boolean right_left = match(folding.cubes.get(5), Orientation.LEFT, folding.cubes.get(0), Orientation.RIGHT);
      boolean right_down = match(folding.cubes.get(5), Orientation.DOWN, folding.cubes.get(3), Orientation.RIGHT);
      boolean right_up = matchReversed(folding.cubes.get(5), Orientation.UP, folding.cubes.get(1), Orientation.RIGHT);
      boolean right_right = matchReversed(folding.cubes.get(5), Orientation.RIGHT, folding.cubes.get(2), Orientation.RIGHT);
      right = right_left && right_down && right_up && right_right;
    }
    
    return segment_valid && left && right;
  }
  
  private boolean isUnfoldedXValid(final Folding folding) {
    LinkedList<Cube> ring = new LinkedList<>();
    ring.addAll(folding.cubes.subList(0, 4));
    boolean segment_valid = isSegmentValid(ring) && isSegmentARing(ring);
    
    boolean left = false;
    boolean right = false;
    if (folding.isUpper) {
      boolean left_right = match(folding.cubes.get(4), Orientation.RIGHT, folding.cubes.get(2), Orientation.LEFT);
      boolean left_down = matchReversed(folding.cubes.get(4), Orientation.DOWN, folding.cubes.get(1), Orientation.LEFT);
      boolean left_up = match(folding.cubes.get(4), Orientation.UP, folding.cubes.get(3), Orientation.LEFT);
      boolean left_left = matchReversed(folding.cubes.get(4), Orientation.LEFT, folding.cubes.get(0), Orientation.LEFT);
      left = left_right && left_down && left_up && left_left;
      
      boolean right_left = match(folding.cubes.get(5), Orientation.LEFT, folding.cubes.get(2), Orientation.RIGHT);
      boolean right_down = match(folding.cubes.get(5), Orientation.DOWN, folding.cubes.get(1), Orientation.RIGHT);
      boolean right_up = matchReversed(folding.cubes.get(5), Orientation.UP, folding.cubes.get(3), Orientation.RIGHT);
      boolean right_right = matchReversed(folding.cubes.get(5), Orientation.RIGHT, folding.cubes.get(0), Orientation.RIGHT);
      right = right_left && right_down && right_up && right_right;
      
    } else {
      boolean left_right = match(folding.cubes.get(4), Orientation.RIGHT, folding.cubes.get(1), Orientation.LEFT);
      boolean left_down = matchReversed(folding.cubes.get(4), Orientation.DOWN, folding.cubes.get(0), Orientation.LEFT);
      boolean left_up = match(folding.cubes.get(4), Orientation.UP, folding.cubes.get(2), Orientation.LEFT);
      boolean left_left = matchReversed(folding.cubes.get(4), Orientation.LEFT, folding.cubes.get(3), Orientation.LEFT);
      left = left_right && left_down && left_up && left_left;
      
      boolean right_left = match(folding.cubes.get(5), Orientation.LEFT, folding.cubes.get(1), Orientation.RIGHT);
      boolean right_down = match(folding.cubes.get(5), Orientation.DOWN, folding.cubes.get(0), Orientation.RIGHT);
      boolean right_up = matchReversed(folding.cubes.get(5), Orientation.UP, folding.cubes.get(2), Orientation.RIGHT);
      boolean right_right = matchReversed(folding.cubes.get(5), Orientation.RIGHT, folding.cubes.get(3), Orientation.RIGHT);
      right = right_left && right_down && right_up && right_right;
    }
    
    return segment_valid && left && right;
  }
  
  private boolean equal(final List<Cube> lhs, final List<Cube> rhs) {
    if (lhs.size() != rhs.size()) {
      return false;
    }
    
    for (int i = 0; i < lhs.size(); ++i) {
      Cube lhs_cube = lhs.get(i);
      Cube rhs_cube = rhs.get(i);
      if (!Util.equal(lhs_cube, rhs_cube)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equal(final Folding lhs, final Folding rhs) {
    if (lhs.form != rhs.form) {
      return false;
    }
    
    if (lhs.isUpper != rhs.isUpper) {
      Folding rhs_flipped = flip(rhs);
      return cubesEquality(lhs, rhs_flipped);
    } else {
      boolean direct = cubesEquality(lhs, rhs);
      Folding rhs_mirrored = mirror(rhs);
      boolean mirrored = cubesEquality(lhs, rhs_mirrored);
      return direct || mirrored;
    }
  }
  
  private boolean cubesEquality(final Folding lhs, final Folding rhs) {
    for (int i = 0; i < 6; ++i) {
      Cube lhs_cube = lhs.cubes.get(i);
      Cube rhs_cube = rhs.cubes.get(i);
      if (!Util.equal(lhs_cube, rhs_cube)) {
        return false;
      }
    }
    return true;
  }
  
  private List<LinkedList<Cube>> removeDuplicateSegments(List<LinkedList<Cube>> list) {
    if (list.size() < 2) {
      return list;
    }
    List<LinkedList<Cube>> result = new ArrayList<>(list.size());
    
    Set<LinkedList<Cube>> unique_list = new TreeSet<>(
        new Comparator<LinkedList<Cube>>() {
          @Override
          public int compare(LinkedList<Cube> lhs, LinkedList<Cube> rhs) {
            if (equal(lhs, rhs)) {
              return 0;
            } else {
              return -1;
            }
          }});
    
    unique_list.addAll(list);
    
    for (LinkedList<Cube> item : unique_list) {
      result.add(item);
    }
    return result;
  }
  
  private List<Folding> removeDuplicates(List<Folding> list) {
    if (list.size() < 2) {
      return list;
    }
    List<Folding> result = new ArrayList<>(list.size());
    
    Set<Folding> unique_list = new TreeSet<>(
        new Comparator<Folding>() {
          @Override
          public int compare(Folding lhs, Folding rhs) {
            if (equal(lhs, rhs)) {
              return 0;
            } else {
              return 1;
            }
          }});
    
    unique_list.addAll(list);
    
    for (Folding item : unique_list) {
      result.add(item);
    }
    return result;
  }
  
  private Folding flip(final Folding folding) {
    boolean is_upper = !folding.isUpper;
    Folding.Form form = folding.form;
    List<Cube> cubes = new ArrayList<>();
    for (int i = 3; i >= 0; --i) {
      cubes.add(folding.cubes.get(i).getRotated().getRotated());
    }
    cubes.add(folding.cubes.get(5).getRotated().getRotated());
    cubes.add(folding.cubes.get(4).getRotated().getRotated());
    Folding flipped = new Folding(cubes, is_upper, form);
    return flipped;
  }
  
  public void testFlip() {
    List<Cube> cubes = new ArrayList<>();
    cubes.add(new Cube(mCubes.get(3)));
    cubes.add(new Cube(mCubes.get(1)));
    cubes.add(new Cube(mCubes.get(4).getMirrored().getRotated().getRotated()));
    cubes.add(new Cube(mCubes.get(5).getMirrored().getRotated().getRotated().getRotated()));
    cubes.add(new Cube(mCubes.get(0)));
    cubes.add(new Cube(mCubes.get(2).getRotated().getRotated().getRotated()));
    Folding folding = new Folding(cubes, true, Folding.Form.T);
    
    System.out.println(unfoldedTtoString(folding));
    System.out.println();
    Folding flipped = flip(folding);
    System.out.println(unfoldedTtoString(flipped));
    System.out.println("Equal: " + equal(folding, flipped));
  }
  
  private Folding mirror(final Folding folding) {
    List<Cube> cubes = new ArrayList<>();
    for (int i = 0; i <= 3; ++i) {
      Cube cube = folding.cubes.get(i);
      cubes.add(cube.getOriented(cube.getOrientation()).getMirrored());
    }
    Cube cube_5 = folding.cubes.get(5);
    Cube cube_4 = folding.cubes.get(4);
    cubes.add(cube_5.getOriented(cube_5.getOrientation()).getMirrored());
    cubes.add(cube_4.getOriented(cube_4.getOrientation()).getMirrored());
    Folding mirrored = new Folding(cubes, folding.isUpper, folding.form);
    return mirrored;
  }
  
  public void testMirror() {
    List<Cube> cubes = new ArrayList<>();
    cubes.add(new Cube(mCubes.get(3)));
    cubes.add(new Cube(mCubes.get(1)));
    cubes.add(new Cube(mCubes.get(4).getMirrored().getRotated().getRotated()));
    cubes.add(new Cube(mCubes.get(5).getMirrored().getRotated().getRotated().getRotated()));
    cubes.add(new Cube(mCubes.get(0)));
    cubes.add(new Cube(mCubes.get(2).getRotated().getRotated().getRotated()));
    Folding folding = new Folding(cubes, true, Folding.Form.T);
    
    System.out.println(unfoldedTtoString(folding));
    System.out.println();
    Folding mirrored = mirror(folding);
    System.out.println(unfoldedTtoString(mirrored));
    System.out.println("Equal: " + equal(folding, mirrored));
  }
  
  // --------------------------------------------------------------------------
  @SuppressWarnings("unused")
  private String ringToString(final List<Cube> ring) {
    for (Cube c : ring) {
      System.out.print(c.getID() + " ");
    }
    System.out.println();
    
    StringBuilder string = new StringBuilder();
    
    for (int i = ring.size() - 1; i >= 0; --i) {
      string.append(ring.get(i).getSide(Orientation.UP)).append("\n");
      
      for (int j = 1; j <= 3; ++j) {
        string.append(ring.get(i).getSide(Orientation.LEFT).cells[j].toChar()).append("ooo")
              .append(ring.get(i).getSide(Orientation.RIGHT).cells[j].toChar()).append("\n");
      }
      
      string.append(ring.get(i).getSide(Orientation.DOWN)).append("\n");
    }
    
    string.append("\n");
    return string.toString();
  }
  
  // --------------------------------------------------------------------------
  private String unfoldedTtoString(final Folding folding) {
    StringBuilder solution = new StringBuilder();
    
    if (folding.isUpper) {
      solution.append(folding.cubes.get(4).getSide(Orientation.UP))
              .append(folding.cubes.get(3).getSide(Orientation.UP))
              .append(folding.cubes.get(5).getSide(Orientation.UP))
              .append("\n");
      
      for (int i = 1; i <= 3; ++i) {
        solution.append(folding.cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(3).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(3).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
                .append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.DOWN))
              .append(folding.cubes.get(3).getSide(Orientation.DOWN))
              .append(folding.cubes.get(5).getSide(Orientation.DOWN))
              .append("\n");
      
      for (int i = 2; i >= 0; --i) {
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
      
        for (int j = 1; j <= 3; ++j) {
          solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                  .append("ooo").append(folding.cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                  .append("     ").append("\n");
        }
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
      }
    } else {
      for (int i = 3; i >= 1; --i) {
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
        
        for (int j = 1; j <= 3; ++j) {
          solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                  .append("ooo").append(folding.cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                  .append("     ").append("\n");
        }
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.UP))
              .append(folding.cubes.get(0).getSide(Orientation.UP))
              .append(folding.cubes.get(5).getSide(Orientation.UP))
              .append("\n");
        
      for (int i = 1; i <= 3; ++i) {
        solution.append(folding.cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(0).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(0).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
                .append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.DOWN))
              .append(folding.cubes.get(0).getSide(Orientation.DOWN))
              .append(folding.cubes.get(5).getSide(Orientation.DOWN))
              .append("\n");
    }
    
    solution.append("\n");
    return solution.toString();
  }
  
  // --------------------------------------------------------------------------
  private String unfoldedXtoString(final Folding folding) {
    StringBuilder solution = new StringBuilder();
    
    if (folding.isUpper) {
      solution.append("     ").append(folding.cubes.get(3).getSide(Orientation.UP)).append("     ").append("\n");
      
      for (int i = 1; i <= 3; ++i) {
        solution.append("     ").append(folding.cubes.get(3).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(3).getSide(Orientation.RIGHT).cells[i].toChar()).append("     ").append("\n");
      }
      solution.append("     ").append(folding.cubes.get(3).getSide(Orientation.DOWN)).append("     ").append("\n");
      
      solution.append(folding.cubes.get(4).getSide(Orientation.UP))
              .append(folding.cubes.get(2).getSide(Orientation.UP))
              .append(folding.cubes.get(5).getSide(Orientation.UP))
              .append("\n");
      
      for (int i = 1; i <= 3; ++i) {
        solution.append(folding.cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(2).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(2).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
                .append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.DOWN))
              .append(folding.cubes.get(2).getSide(Orientation.DOWN))
              .append(folding.cubes.get(5).getSide(Orientation.DOWN))
              .append("\n");
      
      for (int i = 1; i >= 0; --i) {
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
        
        for (int j = 1; j <= 3; ++j) {
          solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                  .append("ooo").append(folding.cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                  .append("     ").append("\n");
        }
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
      }
    } else {
      for (int i = 3; i >= 2; --i) {
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
        
        for (int j = 1; j <= 3; ++j) {
          solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                  .append("ooo").append(folding.cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                  .append("     ").append("\n");
        }
        solution.append("     ").append(folding.cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.UP))
              .append(folding.cubes.get(1).getSide(Orientation.UP))
              .append(folding.cubes.get(5).getSide(Orientation.UP))
              .append("\n");
        
      for (int i = 1; i <= 3; ++i) {
        solution.append(folding.cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(1).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(1).getSide(Orientation.RIGHT).cells[i].toChar())
                .append(folding.cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
                .append("\n");
      }
      
      solution.append(folding.cubes.get(4).getSide(Orientation.DOWN))
              .append(folding.cubes.get(1).getSide(Orientation.DOWN))
              .append(folding.cubes.get(5).getSide(Orientation.DOWN))
              .append("\n");
      
      solution.append("     ").append(folding.cubes.get(0).getSide(Orientation.UP)).append("     ").append("\n");
      
      for (int i = 1; i <= 3; ++i) {
        solution.append("     ").append(folding.cubes.get(0).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
                .append(folding.cubes.get(0).getSide(Orientation.RIGHT).cells[i].toChar()).append("     ").append("\n");
      }
      solution.append("     ").append(folding.cubes.get(0).getSide(Orientation.DOWN)).append("     ").append("\n");
    }
    
    solution.append("\n");
    return solution.toString();
  }
}
