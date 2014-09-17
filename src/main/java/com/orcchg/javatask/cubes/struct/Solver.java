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
    permutations_loop: for (List<Integer> permutation : permutations) {
      
     // List<Folding> foldings = new ArrayList<>();

      Cube cube_1 = new Cube(mCubes.get(permutation.get(0)));
      for (int mirror_i1 = 0; mirror_i1 < 2; ++mirror_i1) {
        for (int rotate_i1 = 0; rotate_i1 < 4; ++rotate_i1) {
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
            
            Cube cube_2 = new Cube(mCubes.get(permutation.get(1)));
            for (int mirror_i2 = 0; mirror_i2 < 2; ++mirror_i2) {
              for (int rotate_i2 = 0; rotate_i2 < 4; ++rotate_i2) {
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
                
                Cube cube_3 = new Cube(mCubes.get(permutation.get(2)));
                for (int mirror_i3 = 0; mirror_i3 < 2; ++mirror_i3) {
                  for (int rotate_i3 = 0; rotate_i3 < 4; ++rotate_i3) {
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
                    
                    Cube cube_4 = new Cube(mCubes.get(permutation.get(3)));
                    for (int mirror_i4 = 0; mirror_i4 < 2; ++mirror_i4) {
                      for (int rotate_i4 = 0; rotate_i4 < 4; ++rotate_i4) {
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
                        
                        Cube cube_5 = new Cube(mCubes.get(permutation.get(4)));
                        for (int mirror_i5 = 0; mirror_i5 < 2; ++mirror_i5) {
                          for (int rotate_i5 = 0; rotate_i5 < 4; ++rotate_i5) {
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
                            
                            Cube cube_6 = new Cube(mCubes.get(permutation.get(5)));
                            for (int mirror_i6 = 0; mirror_i6 < 2; ++mirror_i6) {
                              for (int rotate_i6 = 0; rotate_i6 < 4; ++rotate_i6) {
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
    
    }  // permutations_loop
    
    //mUnfoldedT = removeDuplicates(mUnfoldedT);
    //mUnfoldedX = removeDuplicates(mUnfoldedX);
    // XXX: end
  }  // end exhaustive solution
  
  
  // ------------------------------------------------------------------------------------------------------------------
  /**
   * Old, obsolete and monstrous solution. Do not try to understand this!
   * @brief Sorry for code duplication - I was in a hurry during development :(
   */
  public void solve() {
    if (mCubes.size() != 6) {
      throw new IllegalStateException("Number of cubes is not equal to six! No solution.");
    }
    
    List<Integer> cube_ids = new ArrayList<>(6);
    for (Map.Entry<Integer, Cube> entry : mCubes.entrySet()) {
      cube_ids.add(entry.getKey());
    }

    // ------------------------------------------------------------------------
    // attempt to build the ring of 4 adjacent puzzles
    List<List<Integer>> combinations = Util.allConjunctions(cube_ids, 4);
    
    combinations_4: for (List<Integer> combination : combinations) {
      // all possible rings from given 4 pieces
      List<LinkedList<Cube>> collect_rings = new ArrayList<>();
      
      // all combinations from 4 of 2
      List<List<Integer>> smallcomb = Util.allConjunctions(combination, 2);
      
      // ----------------------------------------------------------------------
      // try two puzzles in all possible orientations and get any
      smallcomb_2: for (List<Integer> pair : smallcomb) {
        // get all possible combinations between two pieces
        List<Orientation.Feature[]> orientation_pairs = new ArrayList<>();

        top_mirror_loop: for (int top_mirror_i = 0; top_mirror_i < 4; ++top_mirror_i) {
          for (Orientation lhs : Orientation.entries) {
            for (Orientation rhs : Orientation.entries) {
              Cube lhs_cube = new Cube(mCubes.get(pair.get(0)));
              if (top_mirror_i == 2 || top_mirror_i == 3) {
                lhs_cube.mirror();
              }
              
              Cube rhs_cube = new Cube(mCubes.get(pair.get(1)));
              if (top_mirror_i == 1 || top_mirror_i == 3) {
                rhs_cube.mirror();
              }
              
              boolean direct = match(lhs_cube, lhs, rhs_cube, rhs);
              boolean reversed = matchReversed(lhs_cube, lhs, rhs_cube, rhs);
              boolean mirrored = Orientation.makeMirroredVertical(lhs, rhs, direct, reversed);
              
              if (direct) {
                Orientation.Feature[] orientation_pair = new Orientation.Feature[2];
                Orientation.Feature lhs_feature = new Orientation.Feature.Builder().setOrientation(lhs).build();
                Orientation.Feature rhs_feature = new Orientation.Feature.Builder().setOrientation(rhs).setMirrored(mirrored).build();
                orientation_pair[0] = lhs_feature;
                orientation_pair[1] = rhs_feature;
                orientation_pairs.add(orientation_pair);
              }
              if (reversed) {
                Orientation.Feature[] orientation_pair = new Orientation.Feature[2];
                Orientation.Feature lhs_feature = new Orientation.Feature.Builder().setOrientation(lhs).build();
                Orientation.Feature rhs_feature = new Orientation.Feature.Builder().setOrientation(rhs).setReversed(true).setMirrored(mirrored).build();
                orientation_pair[0] = lhs_feature;
                orientation_pair[1] = rhs_feature;
                orientation_pairs.add(orientation_pair);
              }
            }
          }
  
          orientations_loop: for (Orientation.Feature[] orientation_pair : orientation_pairs) {
            // get two last pieces ids
            Set<Integer> set_combination_init = new HashSet<>(combination);
            Set<Integer> set_pair_init = new HashSet<>(pair);
            set_combination_init.removeAll(set_pair_init);
            List<Integer> two_last_pieces = new ArrayList<>(set_combination_init);
            
            // ring segments allocation
            Map<Integer, List<LinkedList<Cube>>> ring_segments = new HashMap<>();  // vertical straight line of 3 adjacent pieces
            Map<Integer, List<LinkedList<Cube>>> full_ring_segments = new HashMap<>();  // vertical straight line of 4 adjacent pieces
            for (int last_piece_id : two_last_pieces) {
              ring_segments.put(last_piece_id, new ArrayList<LinkedList<Cube>>());
              full_ring_segments.put(last_piece_id, new ArrayList<LinkedList<Cube>>());
            }
            LinkedList<Cube> common_ring_segment = new LinkedList<>();  // common part of 2 pieces, vertically directed
            
            Cube lhs_cube = new Cube(mCubes.get(pair.get(0)));
            Cube rhs_cube = new Cube(mCubes.get(pair.get(1)));
            Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(orientation_pair[0].getOrientation(), orientation_pair[1].getOrientation());   
            
            Cube valid_lhs_cube = new Cube(lhs_cube);
            if (orientation_pair[0].isMirrored()) {
              valid_lhs_cube.mirror();
              valid_lhs_cube.setOrientation(Orientation.mirror(valid_orientation[0].getOrientation()));
            } else {
              valid_lhs_cube.setOrientation(valid_orientation[0].getOrientation());
            }
            
            Cube valid_rhs_cube = new Cube(rhs_cube);
            if (orientation_pair[1].isMirrored()) {
              valid_rhs_cube.mirror();
              valid_rhs_cube.setOrientation(Orientation.mirror(valid_orientation[1].getOrientation()));
            } else {
              valid_rhs_cube.setOrientation(valid_orientation[1].getOrientation());
            }
            
            common_ring_segment.add(valid_lhs_cube);
            common_ring_segment.add(valid_rhs_cube);
            
            // store pieces to be removed - we do not modify collections we iterating through
            List<Integer> combination_to_remove = new ArrayList<>();
            combination_to_remove.add(lhs_cube.getID());
            combination_to_remove.add(rhs_cube.getID());
            
            if (!isSegmentValid(common_ring_segment)) {
              // wrong segment
              continue orientations_loop;
            }
            
            List<Integer> rest_combination = Util.cloneArrayList(combination);
            rest_combination.removeAll(combination_to_remove);
  
            // ------------------------------------------------------------------
            // try to attach third puzzle and get a straight line
            mirror_loop: for (int mirror_i = 0; mirror_i < 2; ++mirror_i) {
              rest_two_puzzles: for (int restcomb_i = 0; restcomb_i < rest_combination.size(); ++restcomb_i) {
                int rest_cube_id = rest_combination.get(restcomb_i);
                
                for (Orientation orientation : Orientation.entries) {
                  LinkedList<Cube> ring_segment = new LinkedList<>();
                  ring_segment.addAll(common_ring_segment);
                  
                  Cube rest_cube = new Cube(mCubes.get(rest_cube_id));
                  if (mirror_i == 1) {
                    rest_cube.mirror();
                  }
                  
                  boolean direct = match(common_ring_segment.get(1), Orientation.UP, rest_cube, orientation);
                  boolean reversed = matchReversed(common_ring_segment.get(1), Orientation.UP, rest_cube, orientation);
                  boolean mirrored = Orientation.makeMirroredVertical(Orientation.UP, orientation, direct, reversed);
                  Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
                  valid_orientation_local[1].setMirrored(mirrored);
                  
                  if (direct || reversed) {
                    Cube valid_rest_cube = new Cube(rest_cube);
                    if (mirrored) {
                      valid_rest_cube.mirror();
                      valid_rest_cube.setOrientation(Orientation.mirror(valid_orientation_local[1].getOrientation()));
                    } else {
                      valid_rest_cube.setOrientation(valid_orientation_local[1].getOrientation());
                    }
                    
                    ring_segment.add(valid_rest_cube);
                    int mark_last_cube = restcomb_i == 0 ? 1 : 0;
                    ring_segments.get(rest_combination.get(mark_last_cube)).add(ring_segment);
                  }
                }
                
                // try another side
                for (Orientation orientation : Orientation.entries) {
                  LinkedList<Cube> ring_segment = new LinkedList<>();
                  ring_segment.addAll(common_ring_segment);
                  
                  Cube rest_cube = new Cube(mCubes.get(rest_cube_id));
                  if (mirror_i == 1) {
                    rest_cube.mirror();
                  }
                  
                  boolean direct = match(common_ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                  boolean reversed = matchReversed(common_ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                  boolean mirrored = Orientation.makeMirroredVertical(Orientation.DOWN, orientation, direct, reversed);
                  
                  Orientation.Feature valid_orientation_local = null;
                  switch (orientation) {
                    case UP:
                      valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                      break;
                    case DOWN:
                      valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                      break;
                    case RIGHT:
                      valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).build();
                      break;
                    case LEFT:
                      valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();
                      break;
                  }
                  
                  if (direct || reversed) {
                    Cube valid_rest_cube = new Cube(rest_cube);
                    if (mirrored) {
                      valid_rest_cube.mirror();
                      valid_rest_cube.setOrientation(Orientation.mirror(valid_orientation_local.getOrientation()));
                    } else {
                      valid_rest_cube.setOrientation(valid_orientation_local.getOrientation());
                    }
                    
                    ring_segment.addFirst(valid_rest_cube);
                    int mark_last_cube = restcomb_i == 0 ? 1 : 0;
                    ring_segments.get(rest_combination.get(mark_last_cube)).add(ring_segment);
                  }
                }
                continue rest_two_puzzles;
              }  // rest_two_puzzles loop
              continue mirror_loop;
            }  // mirror_loop
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            if (Util.mapContentIsEmpty(ring_segments)) {
              // initial pair of puzzles is wrong or its pieces are in invalid orientation
              // retry with new orientation
              continue orientations_loop;
            }
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            last_mirror_loop: for (int mirror_i = 0; mirror_i < 2; ++mirror_i) {
              ring_segments_map_loop: for (Map.Entry<Integer, List<LinkedList<Cube>>> entry : ring_segments.entrySet()) {
                // try to attach last puzzle and get a ring
                int last_puzzle_id = entry.getKey();
                
                ring_segment_loop: for (LinkedList<Cube> ring_segment : entry.getValue()) {
                  for (Orientation orientation : Orientation.entries) {
                    Cube last_cube = new Cube(mCubes.get(last_puzzle_id));
                    if (mirror_i == 1) {
                      last_cube.mirror();
                    }
                    
                    boolean direct = match(ring_segment.get(2), Orientation.UP, last_cube, orientation);
                    boolean reversed = matchReversed(ring_segment.get(2), Orientation.UP, last_cube, orientation);
                    boolean mirrored = Orientation.makeMirroredVertical(Orientation.UP, orientation, direct, reversed);
                    Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
                    valid_orientation_local[1].setMirrored(mirrored);
                    
                    if (direct || reversed) {
                      Cube valid_last_cube = new Cube(last_cube);
                      if (mirrored) {
                        valid_last_cube.mirror();
                        valid_last_cube.setOrientation(Orientation.mirror(valid_orientation_local[1].getOrientation()));
                      } else {
                        valid_last_cube.setOrientation(valid_orientation_local[1].getOrientation());
                      }
                      
                      LinkedList<Cube> full_ring_segment = new LinkedList<>();
                      full_ring_segment.addAll(ring_segment);
                      full_ring_segment.add(valid_last_cube);
                      full_ring_segments.get(entry.getKey()).add(full_ring_segment);
                    }
                  }
                  
                  // try another side
                  for (Orientation orientation : Orientation.entries) {
                    Cube last_cube = new Cube(mCubes.get(last_puzzle_id));
                    if (mirror_i == 1) {
                      last_cube.mirror();
                    }
                    
                    boolean direct = match(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
                    boolean reversed = matchReversed(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
                    boolean mirrored = Orientation.makeMirroredVertical(Orientation.DOWN, orientation, direct, reversed);
                    
                    Orientation.Feature valid_orientation_local = null;
                    switch (orientation) {
                      case UP:
                        valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                        break;
                      case DOWN:
                        valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                        break;
                      case RIGHT:
                        valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).build();
                        break;
                      case LEFT:
                        valid_orientation_local = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).setReversed(true).build();
                        break;
                    }
      
                    if (direct || reversed) {
                      Cube valid_last_cube = new Cube(last_cube);
                      if (mirrored) {
                        valid_last_cube.mirror();
                        valid_last_cube.setOrientation(Orientation.mirror(valid_orientation_local.getOrientation()));
                      } else {
                        valid_last_cube.setOrientation(valid_orientation_local.getOrientation());
                      }
                      
                      LinkedList<Cube> full_ring_segment = new LinkedList<>();
                      full_ring_segment.addAll(ring_segment);
                      full_ring_segment.addFirst(valid_last_cube);
                      full_ring_segments.get(entry.getKey()).add(full_ring_segment);
                    }
                  }
                  continue ring_segment_loop;
                }  // ring_segment_loop
                continue ring_segments_map_loop;
              }  // ring_segments_map_loop
              continue last_mirror_loop;
            }  // last_mirror_loop
            // ------------------------------------------------------------------
            
            if (Util.mapContentIsEmpty(full_ring_segments)) {
              // initial pair of puzzles is wrong or its pieces are in invalid orientation
              // retry with new orientation
              continue orientations_loop;
            } else {
              // last piece has been found
              for (Map.Entry<Integer, List<LinkedList<Cube>>> entry : full_ring_segments.entrySet()) {
                for (LinkedList<Cube> ring_segment : entry.getValue()) {
                  if (ring_segment.size() == 4) {
                    if (isSegmentARing(ring_segment) && isSegmentValid(ring_segment)) {
                      // we have got a ring! one ring to rule them all...
                      collect_rings.add(ring_segment);
                    }
                    LinkedList<Cube> mirrored_ring_segment = mirrorSegment(ring_segment);
                    if (isSegmentARing(mirrored_ring_segment) && isSegmentValid(mirrored_ring_segment)) {
                      // we have got a ring! one ring to rule them all...
                      collect_rings.add(mirrored_ring_segment);
                    }
                  } else {
                    throw new RuntimeException("Magic error!");
                  }
                }
              }
            }
          }  // orientations_loop
          
          continue top_mirror_loop;
        } // top_mirror_loop
        continue smallcomb_2;
      }  // smallcomb_2 loop
      // ----------------------------------------------------------------------
      collect_rings = removeDuplicateSegments(collect_rings);
      System.out.println(collect_rings.size());
      if (collect_rings.size() < 20) {
        for (LinkedList<Cube> ring : collect_rings) {
          System.out.println(ringToString(ring));
        }
        return;
      }
      
      // --------------------------------------------------------------------------------------------------------------
      
      // ----------------------------------------------------------------------
      // leave two last pieces
      Set<Integer> set_cube_ids = new HashSet<>(cube_ids);
      Set<Integer> set_combination = new HashSet<>(combination);
      set_cube_ids.removeAll(set_combination);
      List<Integer> two_last_pieces = new ArrayList<>(set_cube_ids);
    
      // XXX: trying to add last two pieces
      folding_mirror_loop: for (int folding_mirror_i = 0; folding_mirror_i < 4; ++folding_mirror_i) {
        Cube cube = new Cube(mCubes.get(two_last_pieces.get(0)));
        if (folding_mirror_i == 2 || folding_mirror_i == 3) {
          cube.mirror();
        }
        
        collect_rings_loop: for (LinkedList<Cube> ring : collect_rings) {
          // try to attach two rest pieces in T unfolded form
          List<Integer> ring_head_and_tail = new ArrayList<>(2);
          ring_head_and_tail.add(3);  // head
          ring_head_and_tail.add(0);  // tail
          
          ring_head_and_tail_loop: for (int id : ring_head_and_tail) {
            // ------------------------------------------------------------------
            // try left side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean direct = match(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean reversed = matchReversed(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean mirrored = Orientation.makeMirroredLeft(orientation, direct, reversed);
              
              Orientation.Feature valid_orientation = null;
              switch (orientation) {
                case UP:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                  break;
                case DOWN:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                  break;
                case RIGHT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                  break;
                case LEFT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                  break;
              }
              
              if (direct || reversed) {
                Cube candidate_cube = new Cube(cube);
                if (mirrored) {
                  candidate_cube.mirror();
                  candidate_cube.setOrientation(Orientation.mirror(valid_orientation.getOrientation()));
                } else {
                  candidate_cube.setOrientation(valid_orientation.getOrientation());
                }
                
                boolean try_one   = matchReversed(ring.get(id == 3 ? 2 : 3), Orientation.LEFT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 3 ? 0 : 1), Orientation.LEFT, candidate_cube, Orientation.UP);
                boolean try_three = matchReversed(ring.get(id == 3 ? 1 : 2), Orientation.LEFT, candidate_cube, Orientation.LEFT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (folding_mirror_i == 1 || folding_mirror_i == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredRight(last_orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (last_orientation) {
                      case UP:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                        break;
                      case DOWN:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                        break;
                      case RIGHT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                        break;
                      case LEFT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                        break;
                    }
                    
                    if (another_direct || another_reversed) {
                      Cube last_candidate_cube = new Cube(last_cube);
                      if (last_mirrored) {
                        last_candidate_cube.mirror();
                        last_candidate_cube.setOrientation(Orientation.mirror(local_valid_orientation.getOrientation()));
                      } else {
                        last_candidate_cube.setOrientation(local_valid_orientation.getOrientation());
                      }
                      
                      boolean success_two   = match(ring.get(id == 3 ? 2 : 3), Orientation.RIGHT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = matchReversed(ring.get(id == 3 ? 0 : 1), Orientation.RIGHT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = matchReversed(ring.get(id == 3 ? 1 : 2), Orientation.RIGHT, last_candidate_cube, Orientation.RIGHT);
                      boolean success = success_two && success_three && success_four;
                      
                      if (success) {
                        // we have got an answer - T unfolded form
                        List<Cube> answerT = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerT.add(cube_from_ring);
                        }
                        answerT.add(candidate_cube);
                        answerT.add(last_candidate_cube);
                        Folding folding = new Folding(answerT, id == 3 ? true : false, Folding.Form.T);
                        if (isUnfoldedTValid(folding)) {  // XXX: Record unfolded T, first from left
                          mUnfoldedT.add(folding);
                        }
                      }
                    } else {
                      ++subcounter;
                      continue last_orientation_loop;
                    }
                  }  // last_orientation_loop
                  
                  if (subcounter == Orientation.size) {
                    // last piece has not matched - the whole ring is wrong or orientation of 1st piece is wrong
                    // retry orientation of 1st piece
                    subcounter = 0;
                    continue orientation_loop;
                  }
                  
                } else {
                  // this piece does not match
                }  // accumulate (if)
                
              } else {
                // this piece does not match in such orientation
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            // try another side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean direct = match(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean reversed = matchReversed(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean mirrored = Orientation.makeMirroredRight(orientation, direct, reversed);
              
              Orientation.Feature valid_orientation = null;
              switch (orientation) {
                case UP:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                  break;
                case DOWN:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                  break;
                case RIGHT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                  break;
                case LEFT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                  break;
              }
              
              if (direct || reversed) {
                Cube candidate_cube = new Cube(cube);
                if (mirrored) {
                  candidate_cube.mirror();
                  candidate_cube.setOrientation(Orientation.mirror(valid_orientation.getOrientation()));
                } else {
                  candidate_cube.setOrientation(valid_orientation.getOrientation());
                }
  
                boolean try_one   = match(ring.get(id == 3 ? 2 : 3), Orientation.RIGHT, candidate_cube, Orientation.DOWN);
                boolean try_two   = matchReversed(ring.get(id == 3 ? 0 : 1), Orientation.RIGHT, candidate_cube, Orientation.UP);
                boolean try_three = matchReversed(ring.get(id == 3 ? 1 : 2), Orientation.RIGHT, candidate_cube, Orientation.RIGHT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (folding_mirror_i == 1 || folding_mirror_i == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredLeft(last_orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (last_orientation) {
                      case UP:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                        break;
                      case DOWN:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                        break;
                      case RIGHT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                        break;
                      case LEFT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                        break;
                    }
                    
                    if (another_direct || another_reversed) {
                      Cube last_candidate_cube = new Cube(last_cube);
                      if (last_mirrored) {
                        last_candidate_cube.mirror();
                        last_candidate_cube.setOrientation(Orientation.mirror(local_valid_orientation.getOrientation()));
                      } else {
                        last_candidate_cube.setOrientation(local_valid_orientation.getOrientation());
                      }
  
                      boolean success_two   = matchReversed(ring.get(id == 3 ? 2 : 3), Orientation.LEFT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 3 ? 0 : 1), Orientation.LEFT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = matchReversed(ring.get(id == 3 ? 1 : 2), Orientation.LEFT, last_candidate_cube, Orientation.LEFT);
                      boolean success = success_two && success_three && success_four;
                      
                      if (success) {
                        // we have got an answer - T unfolded form
                        List<Cube> answerT = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerT.add(cube_from_ring);
                        }
                        answerT.add(last_candidate_cube);
                        answerT.add(candidate_cube);
                        Folding folding = new Folding(answerT, id == 3 ? true : false, Folding.Form.T);
                        if (isUnfoldedTValid(folding)) {  // XXX: Record unfolded T, first from right
                          mUnfoldedT.add(folding);
                        }
                      }
                    } else {
                      ++subcounter;
                      continue last_orientation_loop;
                    }
                  }  // last_orientation_loop
                  
                  if (subcounter == Orientation.size) {
                    // last piece has not matched - the whole ring is wrong or orientation of 1st piece is wrong
                    // retry orientation of 1st piece
                    subcounter = 0;
                    continue orientation_loop;
                  }
                  
                } else {
                  // this piece does not match
                }  // accumulate (if)
                
              } else {
                // this piece does not match in such orientation
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            continue ring_head_and_tail_loop;
          }  // ring_head_and_tail_loop
          
          // --------------------------------------------------------------------
          // try to attach two rest pieces in X unfolded form
          List<Integer> ring_middle_band = new ArrayList<>(2);
          ring_middle_band.add(2);  // head
          ring_middle_band.add(1);  // tail
          
          ring_middle_band_loop: for (int id : ring_middle_band) {
            // ------------------------------------------------------------------
            // try left side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean direct = match(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean reversed = matchReversed(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean mirrored = Orientation.makeMirroredLeft(orientation, direct, reversed);
              
              Orientation.Feature valid_orientation = null;
              switch (orientation) {
                case UP:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                  break;
                case DOWN:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                  break;
                case RIGHT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                  break;
                case LEFT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                  break;
              }
              
              if (direct || reversed) {
                Cube candidate_cube = new Cube(cube);
                if (mirrored) {
                  candidate_cube.mirror();
                  candidate_cube.setOrientation(Orientation.mirror(valid_orientation.getOrientation()));
                } else {
                  candidate_cube.setOrientation(valid_orientation.getOrientation());
                }
                
                boolean try_one   = matchReversed(ring.get(id == 2 ? 1 : 0), Orientation.LEFT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 2 ? 3 : 2), Orientation.LEFT, candidate_cube, Orientation.UP);
                boolean try_three = matchReversed(ring.get(id == 2 ? 0 : 3), Orientation.LEFT, candidate_cube, Orientation.LEFT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (folding_mirror_i == 1 || folding_mirror_i == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredRight(last_orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (last_orientation) {
                      case UP:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                        break;
                      case DOWN:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                        break;
                      case RIGHT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                        break;
                      case LEFT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                        break;
                    }
                    
                    if (another_direct || another_reversed) {
                      Cube last_candidate_cube = new Cube(last_cube);
                      if (last_mirrored) {
                        last_candidate_cube.mirror();
                        last_candidate_cube.setOrientation(Orientation.mirror(local_valid_orientation.getOrientation()));
                      } else {
                        last_candidate_cube.setOrientation(local_valid_orientation.getOrientation());
                      }
                      
                      boolean success_two   = match(ring.get(id == 2 ? 1 : 0), Orientation.RIGHT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = matchReversed(ring.get(id == 2 ? 3 : 2), Orientation.RIGHT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = matchReversed(ring.get(id == 2 ? 0 : 3), Orientation.RIGHT, last_candidate_cube, Orientation.RIGHT);
                      boolean success = success_two && success_three && success_four;
                      
                      if (success) {
                        // we have got an answer - X unfolded form
                        List<Cube> answerX = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerX.add(cube_from_ring);
                        }
                        answerX.add(candidate_cube);
                        answerX.add(last_candidate_cube);
                        Folding folding = new Folding(answerX, id == 2 ? true : false, Folding.Form.X);
                        if (isUnfoldedXValid(folding)) {  // XXX: Record unfolded X, first from left
                          mUnfoldedX.add(folding);
                        }
                      }
                    } else {
                      ++subcounter;
                      continue last_orientation_loop;
                    }
                  }  // last_orientation_loop
                  
                  if (subcounter == Orientation.size) {
                    // last piece has not matched - the whole ring is wrong or orientation of 1st piece is wrong
                    // retry orientation of 1st piece
                    subcounter = 0;
                    continue orientation_loop;
                  }
                  
                } else {
                  // this piece does not match
                }  // accumulate (if)
                
              } else {
                // this piece does not match in such orientation
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            // try another side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean direct = match(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean reversed = matchReversed(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean mirrored = Orientation.makeMirroredRight(orientation, direct, reversed);
              
              Orientation.Feature valid_orientation = null;
              switch (orientation) {
                case UP:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                  break;
                case DOWN:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                  break;
                case RIGHT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                  break;
                case LEFT:
                  valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                  break;
              }
              
              if (direct || reversed) {
                Cube candidate_cube = new Cube(cube);
                if (mirrored) {
                  candidate_cube.mirror();
                  candidate_cube.setOrientation(Orientation.mirror(valid_orientation.getOrientation()));
                } else {
                  candidate_cube.setOrientation(valid_orientation.getOrientation());
                }
                
                boolean try_one   = match(ring.get(id == 2 ? 1 : 0), Orientation.RIGHT, candidate_cube, Orientation.DOWN);
                boolean try_two   = matchReversed(ring.get(id == 2 ? 3 : 2), Orientation.RIGHT, candidate_cube, Orientation.UP);
                boolean try_three = matchReversed(ring.get(id == 2 ? 0 : 3), Orientation.RIGHT, candidate_cube, Orientation.RIGHT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (folding_mirror_i == 1 || folding_mirror_i == 3) {
                    last_cube.mirror();
                  }
  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredLeft(last_orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (last_orientation) {
                      case UP:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.RIGHT).build();
                        break;
                      case DOWN:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.LEFT).setReversed(true).build();
                        break;
                      case RIGHT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.UP).build();
                        break;
                      case LEFT:
                        local_valid_orientation = new Orientation.Feature.Builder().setOrientation(Orientation.DOWN).setReversed(true).build();
                        break;
                    }
                    
                    if (another_direct || another_reversed) {
                      Cube last_candidate_cube = new Cube(last_cube);
                      if (last_mirrored) {
                        last_candidate_cube.mirror();
                        last_candidate_cube.setOrientation(Orientation.mirror(local_valid_orientation.getOrientation()));
                      } else {
                        last_candidate_cube.setOrientation(local_valid_orientation.getOrientation());
                      }
                      
                      boolean success_two   = matchReversed(ring.get(id == 2 ? 1 : 0), Orientation.LEFT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 2 ? 3 : 2), Orientation.LEFT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = matchReversed(ring.get(id == 2 ? 0 : 3), Orientation.LEFT, last_candidate_cube, Orientation.LEFT);
                      boolean success = success_two && success_three && success_four;
                      
                      if (success) {
                        // we have got an answer - X unfolded form
                        List<Cube> answerX = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerX.add(cube_from_ring);
                        }
                        answerX.add(last_candidate_cube);
                        answerX.add(candidate_cube);
                        Folding folding = new Folding(answerX, id == 2 ? true : false, Folding.Form.X);
                        if (isUnfoldedXValid(folding)) {  // XXX: Record unfolded X, first from right
                          mUnfoldedX.add(folding);
                        }
                      }
                    } else {
                      ++subcounter;
                      continue last_orientation_loop;
                    }
                  }  // last_orientation_loop
                  
                  if (subcounter == Orientation.size) {
                    // last piece has not matched - the whole ring is wrong or orientation of 1st piece is wrong
                    // retry orientation of 1st piece
                    subcounter = 0;
                    continue orientation_loop;
                  }
                  
                } else {
                  // this piece does not match
                }  // accumulate (if)
                
              } else {
                // this piece does not match in such orientation
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            continue ring_middle_band_loop;
          }  // ring_middle_band_loop
          
          // ------------------------------------------------------------------
  
          continue collect_rings_loop;
        }  // collect_rings_loop
        
        continue folding_mirror_loop;
      }  // folding_mirror_i
      
      continue combinations_4;
    }  // combinations_4 loop
    // ------------------------------------------------------------------------
    
    mUnfoldedT = removeDuplicates(mUnfoldedT);
    mUnfoldedX = removeDuplicates(mUnfoldedX);
  }  // XXX: finish solution
  
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
    
    Folding rhs_temp = rhs;
    if (lhs.isUpper != rhs_temp.isUpper) {
      rhs_temp = flip(rhs);
    }
    for (int i = 0; i < 6; ++i) {
      Cube lhs_cube = lhs.cubes.get(i);
      Cube rhs_cube = rhs_temp.cubes.get(i);
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
              return -1;
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
      cubes.add(folding.cubes.get(i).getOriented(Orientation.DOWN));
    }
    cubes.add(folding.cubes.get(5).getOriented(Orientation.DOWN));
    cubes.add(folding.cubes.get(4).getOriented(Orientation.DOWN));
    Folding flipped = new Folding(cubes, is_upper, form);
    return flipped;
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
