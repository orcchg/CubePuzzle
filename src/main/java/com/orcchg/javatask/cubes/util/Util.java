package com.orcchg.javatask.cubes.util;

import java.util.ArrayList;
import java.util.List;

public class Util {
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
  
  /* Private methods */
  // --------------------------------------------------------------------------
  //private List<List<Integer>> combinations = new ArrayList<>();
  
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
}
