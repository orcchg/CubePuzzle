package com.orcchg.javatask.cubes.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.orcchg.javatask.cubes.util.Util;

public class Solver {
  private Map<Integer, Cube> mCubes;
  private static int internalCounter = 0;
  
  public Solver() {
    mCubes = new HashMap<Integer, Cube>();
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
  
  // match two cubes side (of lhs one) by side (of rhs one)
  public boolean match(final Cube lhs, Orientation lhs_orient, final Cube rhs, Orientation rhs_orient) {
    byte lhs_bits = lhs.getSide(lhs_orient).getNumericRepresentation();
    byte rhs_bits = rhs.getSide(rhs_orient).getNumericRepresentation();
    byte mask = 31;
    byte flip = (byte) ((~lhs_bits) & mask);
    //System.out.println("LHS: " + lhs_bits + ", FLIP: " + flip + ", RHS: " + rhs_bits);
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
    
    List<LinkedList<Cube>> collect_rings = new ArrayList<>();
    
    // attempt to build the ring of 4 adjacent puzzles
    List<List<Integer>> combinations = Util.allConjunctions(cube_ids, 4);
    puzzles_4: for (List<Integer> combination : combinations) {
      List<Integer> backup = Util.cloneArrayList(combination);
      
      // all combinations from 4 of 2
      List<List<Integer>> smallcomb = Util.allConjunctions(combination, 2);
      // ring segment
      LinkedList<Cube> ring_segment = new LinkedList<>();
      
      puzzles_2: while (!smallcomb.isEmpty()) {
        // try two puzzles in all possible orientations and get any
        int pair_index = smallcomb.size() - 1;
        List<Integer> pair = smallcomb.get(pair_index);  // peek last
        
        // get all possible combinations between two pieces
        List<Orientation[]> orientation_pairs = new ArrayList<>();
        
        for (Orientation lhs : Orientation.entries) {
          for (Orientation rhs : Orientation.entries) {
            Cube lhs_cube = mCubes.get(pair.get(0));
            Cube rhs_cube = mCubes.get(pair.get(1));
            boolean result = match(lhs_cube, lhs, rhs_cube, rhs);
            if (result) {
              Orientation[] orientation_pair = new Orientation[]{lhs, rhs};
              orientation_pairs.add(orientation_pair);
            }
          }
        }
        
        orientations_loop: for (Orientation[] orientation_pair : orientation_pairs) {
          Cube lhs_cube = mCubes.get(pair.get(0));
          Cube rhs_cube = mCubes.get(pair.get(1));
          ring_segment.add(lhs_cube.getOriented(orientation_pair[0]));
          ring_segment.add(rhs_cube.getOriented(orientation_pair[1]));
          combination.remove((Object) lhs_cube.getID());
          combination.remove((Object) rhs_cube.getID());
          
          // ------------------------------------------------------------------
          // try to attach third puzzle and get a straight line
          rest_two_puzzles: for (int rest_cube_id : combination) {
            int not_matched_counter = 0;
            for (Orientation orientation : Orientation.entries) {
              Cube rest_cube = mCubes.get(rest_cube_id);
              boolean result = match(ring_segment.get(1), Orientation.UP, rest_cube, orientation);
              if (result) {
                ring_segment.add(rest_cube.getOriented(orientation));
                combination.remove((Object) rest_cube_id);
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
                if (result) {
                  ring_segment.addFirst(rest_cube.getOriented(orientation));
                  combination.remove((Object) rest_cube_id);
                  break rest_two_puzzles;
                }
              }
            }
          }  // rest_two_puzzles loop
          // ------------------------------------------------------------------
          
          // ------------------------------------------------------------------
          if (combination.size() == 2) {
            // initial pair of puzzles is wrong or its pieces are in invalid orientation
            // retry with new orientation
            combination = Util.cloneArrayList(backup);
            ring_segment.clear();
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
            if (result) {
              ring_segment.add(last_cube.getOriented(orientation));
              combination.remove((Object) last_puzzle_id);
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
              if (result) {
                ring_segment.addFirst(last_cube.getOriented(orientation));
                combination.remove((Object) last_puzzle_id);
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
            combination = Util.cloneArrayList(backup);
            ring_segment.clear();
            continue orientations_loop;
          }
        }  // orientations_loop
        
//        orientation_lhs_loop: for (Orientation lhs : Orientation.entries) {
//          orientation_rhs_loop: for (Orientation rhs : Orientation.entries) {
//            Cube lhs_cube = mCubes.get(pair.get(0));
//            Cube rhs_cube = mCubes.get(pair.get(1));
//            boolean result = match(lhs_cube, lhs, rhs_cube, rhs);
//            if (result) {
//              ring_segment.add(lhs_cube.getOriented(lhs));
//              ring_segment.add(rhs_cube.getOriented(rhs));
//              combination.remove((Object) lhs_cube.getID());
//              combination.remove((Object) rhs_cube.getID());
//              break orientation_lhs_loop;
//            } else {
//              continue orientation_rhs_loop;
//            }
//          }  // orientation_rhs_loop
//        }  // orientation_lhs_loop
        

      }  // puzzles_2 while loop
      
      if (ring_segment.size() == 4) {
        // we have got a ring! one ring to rule them all...
        collect_rings.add(ring_segment);
        continue puzzles_4;
      } else {
        // current combination of 4 pieces is wrong
        continue puzzles_4;
      }
    }  // puzzles_4 loop
    
    
  }
  
  public void printCubes() {
    for (Map.Entry<Integer, Cube> entry : mCubes.entrySet()) {
      System.out.println(entry.getValue());
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
}
