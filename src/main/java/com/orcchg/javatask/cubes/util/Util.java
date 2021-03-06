package com.orcchg.javatask.cubes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orcchg.javatask.cubes.struct.Cube;
import com.orcchg.javatask.cubes.struct.Orientation;

public class Util {
  public static List<List<Integer>> allCombinations(final List<Integer> list) {
    return permutation(list);
  }
  
  public static List<List<Integer>> allConjunctions(final List<Integer> list, int length) {
    if (list.size() < length) {
      throw new IllegalArgumentException("List is shorter than required size of conjunctions.");
    }
    
    int[] array = new int[list.size()];
    for (int i = 0; i < array.length; ++i) {
      array[i] = list.get(i);
    }
    List<List<Integer>> combinations = processSubsets(array, length);
    return combinations;
  }
  
  public static <T> void printList(final List<T> list) {
    for (T item : list) {
      System.out.print(item + " ");
    }
    System.out.println("");
  }
  
  public static <T> void printListOfLists(final List<List<T>> list) {
    for (List<T> items : list) {
      for (T item : items) {
        System.out.print(item + " ");
      }
      System.out.println("");
    }
  }
  
  public static List<Integer> cloneArrayList(List<Integer> list) {
    List<Integer> clone = new ArrayList<>(list.size());
    for(Integer item: list) {
      clone.add(item.intValue());
    }
    return clone;
  }
  
  public static List<List<Integer>> cloneListOfArrayLists(List<List<Integer>> list) {
    List<List<Integer>> clone = new ArrayList<>(list.size());
    for (List<Integer> item : list) {
      clone.add(cloneArrayList(item));
    }
    return clone;
  }
  
  public static boolean xor(boolean A, boolean B) {
    return ((A && !B) || (!A && B));
  }
  
  public static boolean sameIds(int[] ids, List<Cube> ring) {
    int i = 0;
    for (Cube cube : ring) {
      if (ids[i] != cube.getID()) {
        return false;
      }
      ++i;
    }
    return true;
  }
  
  public static boolean mapContentIsEmpty(Map<Integer, List<LinkedList<Cube>>> map) {
    int total = 0;
    for (Map.Entry<Integer, List<LinkedList<Cube>>> entry : map.entrySet()) {
      total += entry.getValue().size();
    }
    return (total == 0);
  }
  
  public static String printIDs(List<Cube> cubes) {
    StringBuilder string = new StringBuilder();
    for (Cube cube : cubes) {
      string.append(cube.getID()).append(" ");
    }
    string.append("\n");
    return string.toString();
  }
  
  public static boolean equal(final Cube lhs, final Cube rhs) {
    for (Orientation side : Orientation.entries) {
      if (!lhs.getSide(side).equals(rhs.getSide(side))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean equalSegments(final List<Cube> lhs, final List<Cube> rhs) {
    if (lhs.size() != rhs.size()) {
      return false;
    }
    
    for (int i = 0; i < lhs.size(); ++i) {
      Cube lhs_cube = lhs.get(i);
      Cube rhs_cube = rhs.get(i);
      if (!equal(lhs_cube, rhs_cube)) {
        return false;
      }
    }
    return true;
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private static List<List<Integer>> processSubsets(int[] set, int k) {
    int[] subset = new int[k];
    List<List<Integer>> combinations = new ArrayList<>();
    processLargerSubsets(set, subset, 0, 0, combinations);
    return combinations;
  }
  
  private static void processLargerSubsets(int[] set, int[] subset, int subsetSize, int nextIndex, final List<List<Integer>> combinations) {
    if (subsetSize == subset.length) {
      List<Integer> sublist = new ArrayList<>();
      for (int i : subset) {
        sublist.add(i);
      }
      combinations.add(sublist);
    } else {
      for (int j = nextIndex; j < set.length; j++) {
        subset[subsetSize] = set[j];
        processLargerSubsets(set, subset, subsetSize + 1, j + 1, combinations);
      }
    }
  }

  private static List<List<Integer>> permutation(List<Integer> str) {
    List<List<Integer>> answer = new ArrayList<>();
    permutation(new ArrayList<Integer>(), str, answer);
    return answer;
  }

  private static void permutation(List<Integer> prefix, List<Integer> str, List<List<Integer>> answer) {
    int n = str.size();
    if (n == 0) {
      answer.add(prefix);
    } else {
      for (int i = 0; i < n; i++) {
        List<Integer> sublist = new ArrayList<>();
        sublist.addAll(str.subList(0, i));
        sublist.addAll(str.subList(i + 1, n));
        List<Integer> prefixlist = new ArrayList<>();
        prefixlist.addAll(prefix);
        prefixlist.add(str.get(i));
        permutation(prefixlist, sublist, answer);
      }
    }
  }
}
