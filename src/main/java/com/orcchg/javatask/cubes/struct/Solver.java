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
  
  /**
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
          combination_to_remove.add(lhs_cube.getID());
          combination_to_remove.add(rhs_cube.getID());
          
          List<Integer> rest_combination = Util.cloneArrayList(combination);
          rest_combination.removeAll(combination_to_remove);

          // ------------------------------------------------------------------
          // try to attach third puzzle and get a straight line
          rest_two_puzzles: for (int restcomb_i = 0; restcomb_i < rest_combination.size(); ++restcomb_i) {
            int rest_cube_id = rest_combination.get(restcomb_i);
            
            for (Orientation orientation : Orientation.entries) {
              LinkedList<Cube> ring_segment = new LinkedList<>();
              ring_segment.addAll(common_ring_segment);
              
              Cube rest_cube = new Cube(mCubes.get(rest_cube_id));
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
          // ------------------------------------------------------------------
          
          // ------------------------------------------------------------------
          if (Util.mapContentIsEmpty(ring_segments)) {
            // initial pair of puzzles is wrong or its pieces are in invalid orientation
            // retry with new orientation
            combination_to_remove.clear();
            continue orientations_loop;
          }
          // ------------------------------------------------------------------
          
          // ------------------------------------------------------------------
          ring_segments_map_loop: for (Map.Entry<Integer, List<LinkedList<Cube>>> entry : ring_segments.entrySet()) {
            // try to attach last puzzle and get a ring
            int last_puzzle_id = entry.getKey();
            
            ring_segment_loop: for (LinkedList<Cube> ring_segment : entry.getValue()) {
              for (Orientation orientation : Orientation.entries) {
                Cube last_cube = new Cube(mCubes.get(last_puzzle_id));
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
          // ------------------------------------------------------------------
          
          if (Util.mapContentIsEmpty(full_ring_segments)) {
            // initial pair of puzzles is wrong or its pieces are in invalid orientation
            // retry with new orientation
            combination_to_remove.clear();
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
                  combination_to_remove.clear();
                } else {
                  throw new RuntimeException("Magic error!");
                }
              }
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
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredRight(orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (orientation) {
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
                        //if (isUnfoldedTValid(answerT)) {  //XXX
                          mUnfoldedT.add(answerT);
                        //}
                        
                        if (candidate_cube.getID() == 0 && last_candidate_cube.getID() == 2) {
                          System.out.println(unfoldedTtoString(answerT));
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
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_reversed = matchReversed(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredLeft(orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (orientation) {
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
                        //if (isUnfoldedTValid(answerT)) {  //XXX
                          //mUnfoldedT.add(answerT);
                        //}
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
              boolean reversed = match(ring.get(id), Orientation.LEFT, cube, orientation);
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
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean another_reversed = match(ring.get(id), Orientation.RIGHT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredRight(orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (orientation) {
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
                        //if (isUnfoldedXValid(answerX)) {  //XXX
                          //mUnfoldedX.add(answerX);
                        //}
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
                  if (mirror == 1 || mirror == 3) {
                    last_cube.mirror();
                  }
                  
                  int subcounter = 0;
                  last_orientation_loop: for (Orientation last_orientation : Orientation.entries) {
                    boolean another_direct = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean another_reversed = match(ring.get(id), Orientation.LEFT, last_cube, last_orientation);
                    boolean last_mirrored = Orientation.makeMirroredLeft(orientation, another_direct, another_reversed);
                    
                    Orientation.Feature local_valid_orientation = null;
                    switch (orientation) {
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
                        //if (isUnfoldedXValid(answerX)) {  //XXX
                          //mUnfoldedX.add(answerX);
                        //}
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
  
  private boolean isSegmentValid(final LinkedList<Cube> segment) {
    for (int i = 0; i + 1 < segment.size(); ++i) {
      boolean result = match(segment.get(i), Orientation.UP, segment.get(i + 1), Orientation.DOWN);
      if (!result) {
        return false;
      }
    }
    return true;
  }
  
  private boolean isUnfoldedTValid(final List<Cube> cubes) {
    LinkedList<Cube> ring = new LinkedList<>();
    ring.addAll(cubes.subList(0, 4));
    boolean segment_valid = isSegmentValid(ring) && isSegmentARing(ring);
    
    boolean left_right = match(cubes.get(4), Orientation.RIGHT, cubes.get(3), Orientation.LEFT);
    boolean left_down = matchReversed(cubes.get(4), Orientation.DOWN, cubes.get(2), Orientation.LEFT);
    boolean left_up = match(cubes.get(4), Orientation.UP, cubes.get(0), Orientation.LEFT);
    boolean left_left = matchReversed(cubes.get(4), Orientation.LEFT, cubes.get(1), Orientation.LEFT);
    boolean left = left_right && left_down && left_up && left_left;
    
    boolean right_left = match(cubes.get(5), Orientation.LEFT, cubes.get(3), Orientation.RIGHT);
    boolean right_down = match(cubes.get(5), Orientation.DOWN, cubes.get(2), Orientation.RIGHT);
    boolean right_up = matchReversed(cubes.get(5), Orientation.UP, cubes.get(0), Orientation.RIGHT);
    boolean right_right = matchReversed(cubes.get(5), Orientation.RIGHT, cubes.get(1), Orientation.RIGHT);
    boolean right = right_left && right_down && right_up && right_right;
    
    return segment_valid && left && right;
  }
  
  private boolean isUnfoldedXValid(final List<Cube> cubes) {
    LinkedList<Cube> ring = new LinkedList<>();
    ring.addAll(cubes.subList(0, 4));
    boolean segment_valid = isSegmentValid(ring) && isSegmentARing(ring);
    
    boolean left_right = match(cubes.get(4), Orientation.RIGHT, cubes.get(2), Orientation.LEFT);
    boolean left_down = match(cubes.get(4), Orientation.DOWN, cubes.get(1), Orientation.LEFT);
    boolean left_up = matchReversed(cubes.get(4), Orientation.UP, cubes.get(3), Orientation.LEFT);
    boolean left_left = matchReversed(cubes.get(4), Orientation.LEFT, cubes.get(0), Orientation.LEFT);
    boolean left = left_right && left_down && left_up && left_left;
    
    boolean right_left = match(cubes.get(5), Orientation.LEFT, cubes.get(2), Orientation.RIGHT);
    boolean right_down = match(cubes.get(5), Orientation.DOWN, cubes.get(1), Orientation.RIGHT);
    boolean right_up = matchReversed(cubes.get(5), Orientation.UP, cubes.get(3), Orientation.RIGHT);
    boolean right_right = matchReversed(cubes.get(5), Orientation.RIGHT, cubes.get(0), Orientation.RIGHT);
    boolean right = right_left && right_down && right_up && right_right;
    
    return segment_valid && left && right;
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
  private String unfoldedTtoString(final List<Cube> cubes) {
    StringBuilder solution = new StringBuilder();
    solution.append(Util.printIDs(cubes)).append("------------------\n  VALID: " + isUnfoldedTValid(cubes) + "\n");
    
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
    solution.append(Util.printIDs(cubes)).append("------------------\n  VALID: " + isUnfoldedXValid(cubes) + "\n");
    
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
