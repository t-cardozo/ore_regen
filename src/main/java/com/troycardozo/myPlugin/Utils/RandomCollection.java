package com.troycardozo.myPlugin.Utils;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/******
 * 
 * Random Weight selector from stackoverflow
 * https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 */
public class RandomCollection<E> {
  private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
  private double total = 0;

  public void add(double weight, E result) {
    if (weight <= 0 || map.containsValue(result))
      return;
    total += weight;
    map.put(total, result);
  }

  public void clear() {
    total = 0;
    map.clear();
  }

  public E next() {
    double value = ThreadLocalRandom.current().nextDouble() * total;
    return map.ceilingEntry(value).getValue();
  }
}
