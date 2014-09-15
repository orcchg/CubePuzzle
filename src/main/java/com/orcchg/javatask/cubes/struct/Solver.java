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
  
  private List<List<Cube>> mUnfoldedT;
  private List<List<Cube>> mUnfoldedX;
  
  public Solver() {
    mCubes = new HashMap<Integer, Cube>();
    mUnfoldedT = new ArrayList<List<Cube>>();
    mUnfoldedX = new ArrayList<List<Cube>>();
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

      // store pieces to be removed - we do not modify collections we iterating through
      List<Integer> combination_to_remove = new ArrayList<>();
      
      // all combinations from 4 of 2
      List<List<Integer>> smallcomb = Util.allConjunctions(combination, 2);

      // ----------------------------------------------------------------------
      // try two puzzles in all possible orientations and get any
      smallcomb_2: for (List<Integer> pair : smallcomb) {
        // get all possible combinations between two pieces
        List<Orientation.Feature[]> orientation_pairs = new ArrayList<>();

        for (Orientation lhs : Orientation.entries) {
          for (Orientation rhs : Orientation.entries) {
            Cube lhs_cube = new Cube(mCubes.get(pair.get(0)));
            Cube rhs_cube = new Cube(mCubes.get(pair.get(1)));
            boolean direct = match(lhs_cube, lhs, rhs_cube, rhs);
            boolean reversed = matchReversed(lhs_cube, lhs, rhs_cube, rhs);
            
            boolean mirrored = Orientation.makeMirrored(lhs, rhs, direct, reversed);
            
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
            
//            Cube rhs_cube_mirrored = mCubes.get(pair.get(1)).getMirrored();
//            boolean result_om = match(lhs_cube, lhs, rhs_cube_mirrored, rhs);
//            boolean another_result_om = matchReversed(lhs_cube, lhs, rhs_cube_mirrored, rhs);
//            rhs.mirror = true;
//            if (result_om || another_result_om) {
//              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
//              orientation_pairs.add(orientation_pair);
//            }
//            rhs.mirror = false;
//            
//            Cube lhs_cube_mirrored = mCubes.get(pair.get(0)).getMirrored();
//            boolean result_mo = match(lhs_cube_mirrored, lhs, rhs_cube, rhs);
//            boolean another_result_mo = matchReversed(lhs_cube_mirrored, lhs, rhs_cube, rhs);
//            lhs.mirror = true;
//            if (result_mo || another_result_mo) {
//              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
//              orientation_pairs.add(orientation_pair);
//            }
//            
//            boolean result_mm = match(lhs_cube_mirrored, lhs, rhs_cube_mirrored, rhs);
//            boolean another_result_mm = matchReversed(lhs_cube_mirrored, lhs, rhs_cube_mirrored, rhs);
//            rhs.mirror = true;
//            if (result_mm || another_result_mm) {
//              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
//              orientation_pairs.add(orientation_pair);
//            }
          }
        }

        orientations_loop: for (Orientation.Feature[] orientation_pair : orientation_pairs) {
          // ring segment
          LinkedList<Cube> ring_segment = new LinkedList<>();  // vertical straight line of 4 adjacent pieces

          Cube lhs_cube = new Cube(mCubes.get(pair.get(0)));
          Cube rhs_cube = new Cube(mCubes.get(pair.get(1)));
          Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(orientation_pair[0].getOrientation(), orientation_pair[1].getOrientation());
          
          if (lhs_cube.getID() == 1 && rhs_cube.getID() == 5) {
          System.out.println("-------------------------------------------");
          System.out.println(lhs_cube);
          System.out.println(rhs_cube);
          }
          
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
            if (lhs_cube.getID() == 1 && rhs_cube.getID() == 5) {
            System.out.println("mir");
            System.out.println(valid_rhs_cube);
            }
            valid_rhs_cube.setOrientation(Orientation.mirror(valid_orientation[1].getOrientation()));
            if (lhs_cube.getID() == 1 && rhs_cube.getID() == 5) {
            System.out.println(valid_rhs_cube);
            System.out.println("rot " + valid_rhs_cube.getOrientation());
            }
          } else {
            valid_rhs_cube.setOrientation(valid_orientation[1].getOrientation());
          }
          
          if (lhs_cube.getID() == 1 && rhs_cube.getID() == 5) {
          System.out.print("IDS: [" + lhs_cube.getID() + "|" + rhs_cube.getID() +
              "]\nORIENT [" + orientation_pair[0] + "|" + orientation_pair[1] +
              "]\nVALID [" + valid_orientation[0] + "|" + valid_orientation[1] + "]\n" +
              "ACTUAL [" + valid_lhs_cube.getOrientation() + "__" + valid_rhs_cube.getOrientation() + "]\n");
          System.out.println("+++++");
          
          System.out.println(valid_lhs_cube);
          System.out.println(valid_rhs_cube);
          System.out.println("###############");
          }
//          boolean check = false;
//          if (valid_orientation[0].isReversed() && !valid_orientation[1].isReversed()) {
//            check = matchReversed(valid_rhs_cube, valid_orientation[1].getOrientation(), valid_lhs_cube, valid_orientation[0].getOrientation());
//          } else if (!valid_orientation[0].isReversed() && valid_orientation[1].isReversed()) {
//            check = matchReversed(valid_lhs_cube, valid_orientation[0].getOrientation(), valid_rhs_cube, valid_orientation[1].getOrientation());
//          } else if (!valid_orientation[0].isReversed() && !valid_orientation[1].isReversed()) {
//            check = match(valid_lhs_cube, valid_orientation[0].getOrientation(), valid_rhs_cube, valid_orientation[1].getOrientation());
//          } else {
//            throw new RuntimeException("Magic error: more than one orientations are reversed!");
//          }
//          if (!check) {
//            // wrong actual matching - continue with other orientation
//            continue orientations_loop;
//          }
          
          ring_segment.add(valid_lhs_cube);
          ring_segment.add(valid_rhs_cube);
          combination_to_remove.add(lhs_cube.getID());
          combination_to_remove.add(rhs_cube.getID());
          if (lhs_cube.getID() == 1 && rhs_cube.getID() == 5) {
          System.out.println(ringToString(ring_segment));
          }
          
          List<Integer> rest_combination = Util.cloneArrayList(combination);
          rest_combination.removeAll(combination_to_remove);

          // ------------------------------------------------------------------
          // try to attach third puzzle and get a straight line
          rest_two_puzzles: for (int rest_cube_id : rest_combination) {
            int not_matched_counter = 0;
            for (Orientation orientation : Orientation.entries) {
              Cube rest_cube = new Cube(mCubes.get(rest_cube_id));
              boolean result = match(ring_segment.get(1), Orientation.UP, rest_cube, orientation);
              boolean another_result = matchReversed(ring_segment.get(1), Orientation.UP, rest_cube, orientation);
              Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
              
              if (result || another_result) {
                Cube valid_rest_cube = new Cube(rest_cube);
                if (valid_orientation_local[1].isMirrored()) {
                  valid_rest_cube.mirror();
                }
                valid_rest_cube.setOrientation(valid_orientation_local[1].getOrientation());
                
                boolean check_three = false;
                if (valid_orientation_local[1].isReversed()) {
                  check_three = matchReversed(ring_segment.get(1), Orientation.UP, valid_rest_cube, valid_orientation_local[1].getOrientation());
                } else {
                  check_three = match(ring_segment.get(1), Orientation.UP, valid_rest_cube, valid_orientation_local[1].getOrientation());
                }
                if (!check_three) {
                  // wrong actual matching - continue with other orientation
                  ++not_matched_counter;
                  continue;
                }
                
                ring_segment.add(valid_rest_cube);
                combination_to_remove.add(rest_cube_id);
                break rest_two_puzzles;
              } else {
                ++not_matched_counter;
              }
            }
            
            if (not_matched_counter == Orientation.size) {
              // try another side
              for (Orientation orientation : Orientation.entries) {
                Cube rest_cube = new Cube(mCubes.get(rest_cube_id));
                boolean result = match(ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                boolean another_result = matchReversed(ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.DOWN, orientation);
                
                if (result || another_result) {
                  Cube valid_rest_cube = new Cube(rest_cube);
                  if (valid_orientation_local[1].isMirrored()) {
                    valid_rest_cube.mirror();
                  }
                  valid_rest_cube.setOrientation(valid_orientation_local[1].getOrientation());
                  
                  boolean check_three = false;
                  if (valid_orientation_local[1].isReversed()) {
                    check_three = matchReversed(ring_segment.get(0), Orientation.DOWN, valid_rest_cube, valid_orientation_local[1].getOrientation());
                  } else {
                    check_three = match(ring_segment.get(0), Orientation.DOWN, valid_rest_cube, valid_orientation_local[1].getOrientation());
                  }
                  if (!check_three) {
                    // wrong actual matching - continue with other orientation
                    ++not_matched_counter;
                    continue;
                  }
                  
                  ring_segment.addFirst(valid_rest_cube);
                  combination_to_remove.add(rest_cube_id);
                  break rest_two_puzzles;
                } else {
                  // no-op
                }
              }
            }
          }  // rest_two_puzzles loop
          // ------------------------------------------------------------------
          
          // ------------------------------------------------------------------
          if (combination_to_remove.size() == 2) {
            // initial pair of puzzles is wrong or its pieces are in invalid orientation
            // retry with new orientation
            combination_to_remove.clear();
            continue orientations_loop;
          }
          // ------------------------------------------------------------------
          
          List<Integer> last_combination = Util.cloneArrayList(combination);
          last_combination.removeAll(combination_to_remove);
          
          // ------------------------------------------------------------------
          // try to attach last puzzle and get a ring
          int last_puzzle_id = last_combination.get(0);
          int not_matched_counter = 0;
          for (Orientation orientation : Orientation.entries) {
            Cube last_cube = new Cube(mCubes.get(last_puzzle_id));
            boolean result = match(ring_segment.get(2), Orientation.UP, last_cube, orientation);
            boolean another_result = matchReversed(ring_segment.get(2), Orientation.UP, last_cube, orientation);
            Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
            
            if (result || another_result) {
              Cube valid_last_cube = new Cube(last_cube);
              if (valid_orientation_local[1].isMirrored()) {
                valid_last_cube.mirror();
              }
              valid_last_cube.setOrientation(valid_orientation_local[1].getOrientation());
              
              boolean check_last = false;
              if (valid_orientation_local[1].isReversed()) {
                check_last = matchReversed(ring_segment.get(2), Orientation.UP, valid_last_cube, valid_orientation_local[1].getOrientation());
              } else {
                check_last = match(ring_segment.get(2), Orientation.UP, valid_last_cube, valid_orientation_local[1].getOrientation());
              }
              if (!check_last) {
                // wrong actual matching - continue with other orientation
                ++not_matched_counter;
                continue;
              }
              
              ring_segment.add(valid_last_cube);
              combination_to_remove.add(last_puzzle_id);
              break;
            } else {
              ++not_matched_counter;
            }
          }
          
          if (not_matched_counter == Orientation.size) {
            not_matched_counter = 0;
            // try another side
            for (Orientation orientation : Orientation.entries) {
              Cube last_cube = new Cube(mCubes.get(last_puzzle_id));
              boolean result = match(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
              boolean another_result = matchReversed(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
              Orientation.Feature[] valid_orientation_local = Orientation.getValidOrientation(Orientation.DOWN, orientation);
              
              if (result || another_result) {
                Cube valid_last_cube = new Cube(last_cube);
                if (valid_orientation_local[1].isMirrored()) {
                  valid_last_cube.mirror();
                }
                valid_last_cube.setOrientation(valid_orientation_local[1].getOrientation());
                
                boolean check_last = false;
                if (valid_orientation_local[1].isReversed()) {
                  check_last = matchReversed(ring_segment.get(0), Orientation.DOWN, valid_last_cube, valid_orientation_local[1].getOrientation());
                } else {
                  check_last = match(ring_segment.get(0), Orientation.DOWN, valid_last_cube, valid_orientation_local[1].getOrientation());
                }
                if (!check_last) {
                  // wrong actual matching - continue with other orientation
                  ++not_matched_counter;
                  continue;
                }
                
                ring_segment.addFirst(valid_last_cube);
                combination_to_remove.add(last_puzzle_id);
                break;
              } else {
                ++not_matched_counter;
              }
            }
          }
          // ------------------------------------------------------------------
          
          if (not_matched_counter == Orientation.size) {
            // initial pair of puzzles is wrong or its pieces are in invalid orientation
            // retry with new orientation
            combination_to_remove.clear();
            continue orientations_loop;
          } else {
            // last piece has been found
            if (ring_segment.size() == 4 && combination_to_remove.size() == 4) {
              if (isSegmentARing(ring_segment)) {
                // we have got a ring! one ring to rule them all...
                collect_rings.add(ring_segment);
              }
              combination_to_remove.clear();
            } else {
              throw new RuntimeException("Magic error!");
            }
          }
        }  // orientations_loop
        
        continue smallcomb_2;
      }  // smallcomb_2 loop
      // ----------------------------------------------------------------------
      
      // --------------------------------------------------------------------------------------------------------------
      
      // ----------------------------------------------------------------------
      // leave two last pieces
      Set<Integer> set_cube_ids = new HashSet<>(cube_ids);
      Set<Integer> set_combination = new HashSet<>(combination);
      set_cube_ids.removeAll(set_combination);
      List<Integer> two_last_pieces = new ArrayList<>(set_cube_ids);
      
      mirror_loop: for (int mirror = 0; mirror < 1; ++mirror) {  // TODO
        Cube cube = new Cube(mCubes.get(two_last_pieces.get(0)));
        if (mirror > 1) {
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
              boolean result = match(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean another_result = matchReversed(ring.get(id), Orientation.LEFT, cube, orientation);
              Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1].getOrientation());
                
//                boolean check = match(ring.get(id), Orientation.LEFT, candidate_cube, valid_orientation[1]);
//                if (!check) {
//                  // wrong actual matching - continue with other orientation
//                  continue orientation_loop;
//                }
                
                boolean try_one   = match(ring.get(id == 3 ? 2 : 3), Orientation.LEFT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 3 ? 0 : 1), Orientation.LEFT, candidate_cube, Orientation.UP);
                boolean try_three = match(ring.get(id == 3 ? 1 : 2), Orientation.LEFT, candidate_cube, Orientation.LEFT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean success_one = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_success_one = matchReversed(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    Orientation.Feature[] local_valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1].getOrientation());
                      
//                      boolean last_check = match(ring.get(id), Orientation.RIGHT, last_candidate_cube, local_valid_orientation[1]);
//                      if (!last_check) {
//                         // wrong actual matching - continue with other orientation
//                        ++subcounter;
//                        continue last_orientation_loop;
//                      }
                      
                      boolean success_two   = match(ring.get(id == 3 ? 2 : 3), Orientation.RIGHT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 3 ? 0 : 1), Orientation.RIGHT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = match(ring.get(id == 3 ? 1 : 2), Orientation.RIGHT, last_candidate_cube, Orientation.RIGHT);
                      boolean success = success_two && success_three && success_four;
                      if (success) {
                        // we have got an answer - T unfolded form
                        List<Cube> answerT = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerT.add(cube_from_ring);
                        }
                        answerT.add(candidate_cube);
                        answerT.add(last_candidate_cube);
                        mUnfoldedT.add(answerT);
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
                // this piece does not match
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            // try another side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean result = match(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean another_result = matchReversed(ring.get(id), Orientation.RIGHT, cube, orientation);
              Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1].getOrientation());
                
//                boolean check = match(ring.get(id), Orientation.RIGHT, candidate_cube, valid_orientation[1]);
//                if (!check) {
//                  // wrong actual matching - continue with other orientation
//                  continue orientation_loop;
//                }
                
                boolean try_one   = match(ring.get(id == 3 ? 2 : 3), Orientation.RIGHT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 3 ? 0 : 1), Orientation.RIGHT, candidate_cube, Orientation.UP);
                boolean try_three = match(ring.get(id == 3 ? 1 : 2), Orientation.RIGHT, candidate_cube, Orientation.RIGHT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean success_one = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_success_one = matchReversed(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    Orientation.Feature[] local_valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1].getOrientation());
                      
//                      boolean last_check = match(ring.get(id), Orientation.LEFT, last_candidate_cube, local_valid_orientation[1]); 
//                      if (!last_check) {
//                        // wrong actual matching - continue with other orientation
//                        ++subcounter;
//                        continue last_orientation_loop;
//                      }
                      
                      boolean success_two   = match(ring.get(id == 3 ? 2 : 3), Orientation.LEFT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 3 ? 0 : 1), Orientation.LEFT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = match(ring.get(id == 3 ? 1 : 2), Orientation.LEFT, last_candidate_cube, Orientation.LEFT);
                      boolean success = success_two && success_three && success_four;
                      if (success) {
                        // we have got an answer - T unfolded form
                        List<Cube> answerT = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerT.add(cube_from_ring);
                        }
                        answerT.add(last_candidate_cube);
                        answerT.add(candidate_cube);
                        mUnfoldedT.add(answerT);
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
                // this piece does not match
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
              boolean result = match(ring.get(id), Orientation.LEFT, cube, orientation);
              boolean another_result = match(ring.get(id), Orientation.LEFT, cube, orientation);
              Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1].getOrientation());
                
//                boolean check = match(ring.get(id), Orientation.LEFT, candidate_cube, valid_orientation[1]);
//                if (!check) {
//                  // wrong actual matching - continue with other orientation
//                  continue orientation_loop;
//                }
                
                boolean try_one   = match(ring.get(id == 2 ? 1 : 0), Orientation.LEFT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 2 ? 3 : 2), Orientation.LEFT, candidate_cube, Orientation.UP);
                boolean try_three = match(ring.get(id == 2 ? 0 : 3), Orientation.LEFT, candidate_cube, Orientation.LEFT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean success_one = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_success_one = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    Orientation.Feature[] local_valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1].getOrientation());
                      
//                      boolean last_check = match(ring.get(id), Orientation.RIGHT, last_candidate_cube, local_valid_orientation[1]);
//                      if (!last_check) {
//                        // wrong actual matching - continue with other orientation
//                        ++subcounter;
//                        continue last_orientation_loop;
//                      }
                      
                      boolean success_two   = match(ring.get(id == 2 ? 1 : 0), Orientation.RIGHT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 2 ? 3 : 2), Orientation.RIGHT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = match(ring.get(id == 2 ? 0 : 3), Orientation.RIGHT, last_candidate_cube, Orientation.RIGHT);
                      boolean success = success_two && success_three && success_four;
                      if (success) {
                        // we have got an answer - X unfolded form
                        List<Cube> answerX = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerX.add(cube_from_ring);
                        }
                        answerX.add(candidate_cube);
                        answerX.add(last_candidate_cube);
                        mUnfoldedX.add(answerX);
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
                // this piece does not match
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            // ------------------------------------------------------------------
            // try another side
            orientation_loop: for (Orientation orientation : Orientation.entries) {
              boolean result = match(ring.get(id), Orientation.RIGHT, cube, orientation);
              boolean another_result = matchReversed(ring.get(id), Orientation.RIGHT, cube, orientation);
              Orientation.Feature[] valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1].getOrientation());
                
//                boolean check = match(ring.get(id), Orientation.RIGHT, candidate_cube, valid_orientation[1]);
//                if (!check) {
//                  // wrong actual matching - continue with other orientation
//                  continue orientation_loop;
//                }
                
                boolean try_one   = match(ring.get(id == 2 ? 1 : 0), Orientation.RIGHT, candidate_cube, Orientation.DOWN);
                boolean try_two   = match(ring.get(id == 2 ? 3 : 2), Orientation.RIGHT, candidate_cube, Orientation.UP);
                boolean try_three = match(ring.get(id == 2 ? 0 : 3), Orientation.RIGHT, candidate_cube, Orientation.RIGHT);
                boolean accumulate = try_one && try_two && try_three;
                
                if (accumulate) {
                  // try last piece
                  Cube last_cube = new Cube(mCubes.get(two_last_pieces.get(1)));
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean success_one = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_success_one = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    Orientation.Feature[] local_valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1].getOrientation());
                      
//                      boolean last_check = match(ring.get(id), Orientation.LEFT, last_candidate_cube, local_valid_orientation[1]);
//                      if (!last_check) {
//                        // wrong actual matching - continue with other orientation
//                        ++subcounter;
//                        continue last_orientation_loop;
//                      }
                      
                      boolean success_two   = match(ring.get(id == 2 ? 1 : 0), Orientation.LEFT, last_candidate_cube, Orientation.DOWN);
                      boolean success_three = match(ring.get(id == 2 ? 3 : 2), Orientation.LEFT, last_candidate_cube, Orientation.UP);
                      boolean success_four  = match(ring.get(id == 2 ? 0 : 3), Orientation.LEFT, last_candidate_cube, Orientation.LEFT);
                      boolean success = success_two && success_three && success_four;
                      if (success) {
                        // we have got an answer - X unfolded form
                        List<Cube> answerX = new ArrayList<>(6);
                        for (Cube cube_from_ring : ring) {
                          answerX.add(cube_from_ring);
                        }
                        answerX.add(last_candidate_cube);
                        answerX.add(candidate_cube);
                        mUnfoldedX.add(answerX);
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
                // this piece does not match
              }  // result (if)
            }  // orientations_loop
            // ------------------------------------------------------------------
            
            continue ring_middle_band_loop;
          }  // ring_middle_band_loop
          
          // ------------------------------------------------------------------
  
          continue collect_rings_loop;
        }  // collect_rings_loop
        
        continue mirror_loop;
      }  // mirror_loop
      
      continue combinations_4;
    }  // combinations_4 loop
    // ------------------------------------------------------------------------
    
    mUnfoldedT = removeDuplicates(mUnfoldedT);
    mUnfoldedX = removeDuplicates(mUnfoldedX);
  }
  
  public String getSolution() {
    StringBuilder solution = new StringBuilder();
    for (List<Cube> cubes : mUnfoldedT) {
      // print all solutions as T unfolded form
      solution.append(unfoldedTtoString(cubes));
    }
    
    // ------------------------------------------------------------------------
    for (List<Cube> cubes : mUnfoldedX) {
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
  
  private List<List<Cube>> removeDuplicates(List<List<Cube>> list) {
    Set<List<Cube>> unique_list = new TreeSet<>(
        new Comparator<List<Cube>>() {
          @Override
          public int compare(List<Cube> lhs, List<Cube> rhs) {
            int equal_counter = 0;
            int ids_counter = 0;
            
            for (int i = 0; i < lhs.size(); ++i) {
              Cube lhs_cube = lhs.get(i);
              Cube rhs_cube = rhs.get(i);
              boolean equal_ids = lhs_cube.getID() == rhs_cube.getID();
              boolean equal_orientations = lhs_cube.getOrientation().equals(rhs_cube.getOrientation()) ||
                                           (equal_ids && lhs_cube.isSymmetric());
              
              if (equal_ids && equal_orientations) {
                ++equal_counter;
              } else if (equal_ids) {
                ++ids_counter;
              }
            }
            
            if (equal_counter == lhs.size()) {
              return 0;
            } else if (ids_counter == lhs.size()) {
              return 1;
            } else {
              return -1;
            }
          }});
    
    unique_list.addAll(list);
    
    List<List<Cube>> result = new ArrayList<>();
    for (List<Cube> item : unique_list) {
      result.add(item);
    }
    return result;
  }
  
  // --------------------------------------------------------------------------
  private String ringToString(final List<Cube> ring) {
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
  private String unfoldedTtoString(final List<Cube> cubes) {
    StringBuilder solution = new StringBuilder();
    
    solution.append(cubes.get(4).getSide(Orientation.UP))
            .append(cubes.get(3).getSide(Orientation.UP))
            .append(cubes.get(5).getSide(Orientation.UP))
            .append("\n");
    
    for (int i = 1; i <= 3; ++i) {
      solution.append(cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
              .append(cubes.get(3).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(3).getSide(Orientation.RIGHT).cells[i].toChar())
              .append(cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
              .append("\n");
    }
    
    solution.append(cubes.get(4).getSide(Orientation.DOWN))
            .append(cubes.get(3).getSide(Orientation.DOWN))
            .append(cubes.get(5).getSide(Orientation.DOWN))
            .append("\n");
    
    for (int i = 2; i >= 0; --i) {
      solution.append("     ").append(cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
    
      for (int j = 1; j <= 3; ++j) {
        solution.append("     ").append(cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                .append("ooo").append(cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                .append("     ").append("\n");
      }
      solution.append("     ").append(cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
    }
    
    solution.append("\n");
    return solution.toString();
  }
  
  // --------------------------------------------------------------------------
  private String unfoldedXtoString(final List<Cube> cubes) {
    StringBuilder solution = new StringBuilder();
    
    solution.append("     ").append(cubes.get(3).getSide(Orientation.UP)).append("     ").append("\n");
    
    for (int i = 1; i <= 3; ++i) {
      solution.append("     ").append(cubes.get(3).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(3).getSide(Orientation.RIGHT).cells[i].toChar()).append("     ").append("\n");
    }
    solution.append("     ").append(cubes.get(3).getSide(Orientation.DOWN)).append("     ").append("\n");
    
    solution.append(cubes.get(4).getSide(Orientation.UP))
            .append(cubes.get(2).getSide(Orientation.UP))
            .append(cubes.get(5).getSide(Orientation.UP))
            .append("\n");
    
    for (int i = 1; i <= 3; ++i) {
      solution.append(cubes.get(4).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(4).getSide(Orientation.RIGHT).cells[i].toChar())
              .append(cubes.get(2).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(2).getSide(Orientation.RIGHT).cells[i].toChar())
              .append(cubes.get(5).getSide(Orientation.LEFT).cells[i].toChar()).append("ooo")
              .append(cubes.get(5).getSide(Orientation.RIGHT).cells[i].toChar())
              .append("\n");
    }
    
    solution.append(cubes.get(4).getSide(Orientation.DOWN))
            .append(cubes.get(2).getSide(Orientation.DOWN))
            .append(cubes.get(5).getSide(Orientation.DOWN))
            .append("\n");
    
    for (int i = 1; i >= 0; --i) {
      solution.append("     ").append(cubes.get(i).getSide(Orientation.UP)).append("     ").append("\n");
      
      for (int j = 1; j <= 3; ++j) {
        solution.append("     ").append(cubes.get(i).getSide(Orientation.LEFT).cells[j].toChar())
                .append("ooo").append(cubes.get(i).getSide(Orientation.RIGHT).cells[j].toChar())
                .append("     ").append("\n");
      }
      solution.append("     ").append(cubes.get(i).getSide(Orientation.DOWN)).append("     ").append("\n");
    }
    
    solution.append("\n");
    return solution.toString();
  }
}
