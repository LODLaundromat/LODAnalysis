import java.lang.Exception;
import java.lang.StringBuilder;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Scanner;

import data.Triple;

public class BkChain extends Object {

  private static long threshold     = 1000000;
  private static long tripleCounter = 0;
  private static Triple triple = null;
  private static Scanner input = null;

  private static Set<String> classSet    = new HashSet<String>();
  private static Set<String> propertySet = new HashSet<String>();
  private static Map<String, Set<String>> sameAsSubjectSet   = new HashMap<String, Set<String>>();
  private static Map<String, Set<String>> sameAsObjectSet   = new HashMap<String, Set<String>>();

  public static void main (String[] argv) {
    long tripleCounter = 0;
    String inputString = null;
    Triple inputTriple = null;
    String inputFile;

    if (argv.length < 1) {
      System.err.println ("give the input file!");
      System.exit(1);
    }

    inputFile = argv[0];

    try {
      BufferedReader br = new BufferedReader(new FileReader(inputFile));
      try {
        while ((inputString = br.readLine()) != null) {
          inputTriple = new Triple(inputString);
          filterTriple(inputTriple);
          updateTripleCounter();
        }
      } finally {
        br.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    checkoutSameAsChain();
  }
  /* ----------------------------------- */
  private static void filterTriple (Triple triple) {
        filterClasses    (triple);
        filterProperties (triple);
        filterSameAs     (triple);
        //filterDomain     (triple);
        //filterRange      (triple);
  }
  /* ----------------------------------- */
  private static void checkoutSameAsChain() {

    boolean foundReplacement;
    List<String> temp = new ArrayList<String>();

    while (true) {
      foundReplacement = false;
      /* ----------------------------------- */
      System.out.println ("-------------------------------");
      for (String c : classSet) {
        System.out.println ("Subject class=" + c);
        if (sameAsSubjectSet.get(c) != null) {
          for (String s : sameAsSubjectSet.get(c)) {
            if (!classSet.contains(s)){
              temp.add(s);
              foundReplacement = true;
            }
          }
        }
      }
      if (foundReplacement) {
        for (String s : temp) {
          System.out.println(s);
          classSet.add(s);
        }
        temp.clear();
      }
      /* ----------------------------------- */
      System.out.println ("-------------------------------");
      for (String c : classSet) {
        System.out.println ("Object class=" + c);
        if (sameAsObjectSet.get(c) != null) {
          for (String s : sameAsObjectSet.get(c)) {
            if (!classSet.contains(s)){
              temp.add(s);
              foundReplacement = true;
            }
          }
        }
      }
      if (foundReplacement) {
        for (String s : temp) {
          System.out.println(s);
          classSet.add(s);
        }
        temp.clear();
      }
      /* ----------------------------------- */
      if (foundReplacement == false) {
        break;
      }
    }
  }
  /* ----------------------------------- */
  private static void filterClasses (Triple triple) {
    if (triple.getObject().matches(".*#Class>")) {
          classSet.add(triple.getSubject());
    }
  }
  /* ----------------------------------- */
  private static void filterProperties (Triple triple) {
    if (triple.getObject().matches(".*#Property>")) {
          propertySet.add(triple.getSubject());
    }
  }
  /* ----------------------------------- */
  private static void filterSameAs (Triple triple) {
    Set<String> objectSet = null;
    Set<String> subjectSet = null;

    if (triple.getPredicate().matches(".*owl#sameAs>")) {

      if (sameAsSubjectSet.get(triple.getSubject()) == null) {
        objectSet = new HashSet<String>();
        objectSet.add(triple.getObject());
        sameAsSubjectSet.put(triple.getSubject(), objectSet);
      } else {
        sameAsSubjectSet.get(triple.getSubject()).add(triple.getObject());
      }

      if (sameAsObjectSet.get(triple.getObject()) == null) {
        subjectSet = new HashSet<String>();
        subjectSet.add(triple.getSubject());
        sameAsObjectSet.put(triple.getObject(), subjectSet);
      } else {
        sameAsObjectSet.get(triple.getObject()).add(triple.getSubject());
      }
    }
  }
  /* ----------------------------------- */
  private static void printSets() {
    for (Map.Entry<String, Set<String>> entry : sameAsSubjectSet.entrySet()) {
      System.out.println ("key = " + entry.getKey());
      System.out.println ("Value = " + entry.getValue());
    }
  }
  /* ----------------------------------- */
  private static void updateTripleCounter() {
    if (tripleCounter % threshold == 0) {
      System.out.println (tripleCounter);
    }
    tripleCounter++;
  }
}