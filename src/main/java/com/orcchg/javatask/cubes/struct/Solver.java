package com.orcchg.javatask.cubes.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  public boolean match(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    Side lhs_side = lhs.getSide(lhs_orient);
    Side rhs_side = rhs.getSide(lhs_orient);
    boolean result = true;
    for (int i = 0; i < 5; ++i) {
      result = result && ((lhs_side.cells[i].toInt() + rhs_side.cells[i].toInt()) < 2);
    }
    return result;
  }
  
  public boolean matchReversed(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    Side lhs_side = lhs.getSide(lhs_orient);
    Side rhs_side = rhs.getSide(lhs_orient).getReversed();
    boolean result = true;
    for (int i = 0; i < 5; ++i) {
      result = result && ((lhs_side.cells[i].toInt() + rhs_side.cells[i].toInt()) < 2);
    }
    return result;
  }
  
  public boolean fullMatch(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    byte lhs_bits = lhs.getSide(lhs_orient).getNumericRepresentation();
    byte rhs_bits = rhs.getSide(rhs_orient).getNumericRepresentation();
    byte mask = 31;
    byte flip = (byte) ((~lhs_bits) & mask);
    return flip == rhs_bits;
  }
  
  public boolean fullMatchReversed(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
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
        List<Orientation[]> orientation_pairs = new ArrayList<>();

        for (Orientation lhs : Orientation.entries) {
          for (Orientation rhs : Orientation.entries) {
            Cube lhs_cube = mCubes.get(pair.get(0));
            Cube rhs_cube = mCubes.get(pair.get(1));
            boolean result = match(lhs_cube, lhs, rhs_cube, rhs);
            boolean another_result = matchReversed(lhs_cube, lhs, rhs_cube, rhs);
            if (result || another_result) {
              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
              orientation_pairs.add(orientation_pair);
            }
            
            Cube rhs_cube_mirrored = mCubes.get(pair.get(1)).getMirrored();
            boolean result_om = match(lhs_cube, lhs, rhs_cube_mirrored, rhs);
            boolean another_result_om = matchReversed(lhs_cube, lhs, rhs_cube_mirrored, rhs);
            rhs.mirror = true;
            if (result_om || another_result_om) {
              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
              orientation_pairs.add(orientation_pair);
            }
            rhs.mirror = false;
            
            Cube lhs_cube_mirrored = mCubes.get(pair.get(0)).getMirrored();
            boolean result_mo = match(lhs_cube_mirrored, lhs, rhs_cube, rhs);
            boolean another_result_mo = matchReversed(lhs_cube_mirrored, lhs, rhs_cube, rhs);
            lhs.mirror = true;
            if (result_mo || another_result_mo) {
              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
              orientation_pairs.add(orientation_pair);
            }
            
            boolean result_mm = match(lhs_cube_mirrored, lhs, rhs_cube_mirrored, rhs);
            boolean another_result_mm = matchReversed(lhs_cube_mirrored, lhs, rhs_cube_mirrored, rhs);
            rhs.mirror = true;
            if (result_mm || another_result_mm) {
              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
              orientation_pairs.add(orientation_pair);
            }
          }
        }

        orientations_loop: for (Orientation[] orientation_pair : orientation_pairs) {
          // ring segment
          LinkedList<Cube> ring_segment = new LinkedList<>();  // vertical straight line of 4 adjacent pieces

          Cube lhs_cube = mCubes.get(pair.get(0));
          Cube rhs_cube = mCubes.get(pair.get(1));
          Orientation[] valid_orientation = Orientation.getValidOrientation(orientation_pair[0], orientation_pair[1]);
          
          Cube valid_lhs_cube = new Cube(lhs_cube);
          if (valid_orientation[0].mirror) {
            valid_lhs_cube.mirror();
          }
          valid_lhs_cube.setOrientation(valid_orientation[0]);
          
          Cube valid_rhs_cube = new Cube(rhs_cube);
          if (valid_orientation[1].mirror) {
            valid_rhs_cube.mirror();
          }
          valid_rhs_cube.setOrientation(valid_orientation[1]);
          
          ring_segment.add(valid_lhs_cube);
          ring_segment.add(valid_rhs_cube);
          //combination.remove((Object) lhs_cube.getID());
          combination_to_remove.add(lhs_cube.getID());
          //combination.remove((Object) rhs_cube.getID());
          combination_to_remove.add(rhs_cube.getID());

          // ------------------------------------------------------------------
          // try to attach third puzzle and get a straight line
          rest_two_puzzles: for (int rest_cube_id : combination) {
            int not_matched_counter = 0;
            for (Orientation orientation : Orientation.entries) {
              Cube rest_cube = mCubes.get(rest_cube_id);
              boolean result = match(ring_segment.get(1), Orientation.UP, rest_cube, orientation);
              boolean another_result = matchReversed(ring_segment.get(1), Orientation.UP, rest_cube, orientation);
              Orientation[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
              
              if (result || another_result) {
                Cube valid_rest_cube = new Cube(rest_cube);
                if (valid_orientation_local[1].mirror) {
                  valid_rest_cube.mirror();
                }
                valid_rest_cube.setOrientation(valid_orientation_local[1]);
                
                ring_segment.add(valid_rest_cube);
                //combination.remove((Object) rest_cube_id);
                combination_to_remove.add(rest_cube_id);
                break rest_two_puzzles;
              } else {
                ++not_matched_counter;
              }
            }
            
            if (not_matched_counter == Orientation.size) {
              // try another side
              for (Orientation orientation : Orientation.entries) {
                Cube rest_cube = mCubes.get(rest_cube_id);
                boolean result = match(ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                boolean another_result = matchReversed(ring_segment.get(0), Orientation.DOWN, rest_cube, orientation);
                Orientation[] valid_orientation_local = Orientation.getValidOrientation(Orientation.DOWN, orientation);
                
                if (result || another_result) {
                  Cube valid_rest_cube = new Cube(rest_cube);
                  if (valid_orientation_local[1].mirror) {
                    valid_rest_cube.mirror();
                  }
                  valid_rest_cube.setOrientation(valid_orientation_local[1]);
                  
                  ring_segment.addFirst(valid_rest_cube);
                  //combination.remove((Object) rest_cube_id);
                  combination_to_remove.add(rest_cube_id);
                  break rest_two_puzzles;
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
          
          // ------------------------------------------------------------------
          // try to attach last puzzle and get a ring
          int last_puzzle_id = combination.get(0);
          int not_matched_counter = 0;
          for (Orientation orientation : Orientation.entries) {
            Cube last_cube = mCubes.get(last_puzzle_id);
            boolean result = match(ring_segment.get(2), Orientation.UP, last_cube, orientation);
            boolean another_result = matchReversed(ring_segment.get(2), Orientation.UP, last_cube, orientation);
            Orientation[] valid_orientation_local = Orientation.getValidOrientation(Orientation.UP, orientation);
            
            if (result || another_result) {
              Cube valid_last_cube = new Cube(last_cube);
              if (valid_orientation_local[1].mirror) {
                valid_last_cube.mirror();
              }
              valid_last_cube.setOrientation(valid_orientation_local[1]);
              
              ring_segment.add(valid_last_cube);
              //combination.remove((Object) last_puzzle_id);
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
              Cube last_cube = mCubes.get(last_puzzle_id);
              boolean result = match(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
              boolean another_result = matchReversed(ring_segment.get(0), Orientation.DOWN, last_cube, orientation);
              Orientation[] valid_orientation_local = Orientation.getValidOrientation(Orientation.DOWN, orientation);
              
              if (result || another_result) {
                Cube valid_last_cube = new Cube(last_cube);
                if (valid_orientation_local[1].mirror) {
                  valid_last_cube.mirror();
                }
                valid_last_cube.setOrientation(valid_orientation_local[1]);
                
                ring_segment.add(valid_last_cube);
                //combination.remove((Object) last_puzzle_id);
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
      
      // ----------------------------------------------------------------------
      // leave two last pieces
      Set<Integer> set_cube_ids = new HashSet<>(cube_ids);
      Set<Integer> set_combination = new HashSet<>(combination);
      set_cube_ids.removeAll(set_combination);
      List<Integer> two_last_pieces = new ArrayList<>(set_cube_ids);
      
      mirror_loop: for (int mirror = 0; mirror < 4; ++mirror) {
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
              Orientation[] valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1]);
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
                    Orientation[] local_valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1]);
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
              Orientation[] valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1]);
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
                    Orientation[] local_valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1]);
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
              Orientation[] valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1]);
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
                    Orientation[] local_valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1]);
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
              Orientation[] valid_orientation = Orientation.getValidOrientation(Orientation.RIGHT, orientation);
              
              if (result || another_result) {
                Cube candidate_cube = cube.getOriented(valid_orientation[1]);
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
                    Orientation[] local_valid_orientation = Orientation.getValidOrientation(Orientation.LEFT, last_orientation);
                    
                    if (success_one || another_success_one) {
                      Cube last_candidate_cube = last_cube.getOriented(local_valid_orientation[1]);
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
  }
  
  public String getSolution() {
    StringBuilder solution = new StringBuilder();
    for (List<Cube> cubes : mUnfoldedT) {
      // print all solutions as T unfolded form
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
      solution.append("\n\n");
    }
    
    // ------------------------------------------------------------------------
    for (List<Cube> cubes : mUnfoldedX) {
      // print all solutions as X unfolded form
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
}
