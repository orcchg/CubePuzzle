package com.orcchg.javatask.cubes.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orcchg.javatask.cubes.MainSolution;
import com.orcchg.javatask.cubes.util.Util;

public class TestCubeUtils {
  private static MainSolution main;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
//    main = new MainSolution();
//    
//    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//    main.readCubes(classloader.getResource("sample.txt").getFile());
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCubeEquals() {
    List<List<Integer>> a = Util.allCombinations(Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5}));
    for (List<Integer> l : a) {
      System.out.println(l);
    }
  }

}
