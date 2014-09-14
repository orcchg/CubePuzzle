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
    processSubsets(array, length);
    return combinations;
  }
  
  public static void printCombinations() {
    for (List<Integer> items : combinations) {
      for (int item : items) {
        System.out.print(item + " ");
      }
      System.out.println("");
    }
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
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private static List<List<Integer>> combinations = new ArrayList<>();
  
  private static void processSubsets(int[] set, int k) {
    int[] subset = new int[k];
    combinations.clear();
    processLargerSubsets(set, subset, 0, 0);
  }
  
  private static void processLargerSubsets(int[] set, int[] subset, int subsetSize, int nextIndex) {
    if (subsetSize == subset.length) {
      List<Integer> sublist = new ArrayList<>();
      for (int i : subset) {
        sublist.add(i);
      }
      combinations.add(sublist);
    } else {
      for (int j = nextIndex; j < set.length; j++) {
        subset[subsetSize] = set[j];
        processLargerSubsets(set, subset, subsetSize + 1, j + 1);
      }
    }
  }
}
