// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.algorithms;

import com.google.sps.Event;
import java.util.ArrayList;
import java.util.Comparator; 
import java.util.List;

/**
 * Represents a merge sort object
 */
public final class MergeSort<T> {

  /**
   * Merges two subarrays into an ordered array
   * Time Complexity: O(n)
   * 
   * @param objs the arraylist we will order 
   * @param comp the comparator that  
   * @param left the leftmost index of the sub array
   * @param middle the middle index of the subarray
   * @param right the right index of the subarray
   */
  public void merge(ArrayList<T> objs, Comparator<T> comp,
      int left, int middle, int right) {
    // prep by creating a view of the two subarrays on the clone of that 
    //     arraylist
    ArrayList<T> objsClone = (ArrayList<T>) objs.clone();
    List<T> sub1 = objsClone.subList(left, middle);
    List<T> sub2 = objsClone.subList(middle, right);

    int sub1Size = sub1.size();
    int sub2Size = sub2.size(); 

    // merge the two subarrays
    int startIndex = left;
    int i = 0;
    int j = 0;

    while (i < sub1Size && j < sub2Size) {
      T first = sub1.get(i);
      T second = sub2.get(j);
      int order = comp.compare(first, second);
      if (order < 0) {
        i++;
        objs.set(startIndex, first);
      } else {
        j++;
        objs.set(startIndex, second);
      }
      startIndex++;
    }

    // merge the leftover
    while (i < sub1Size) {
      objs.set(startIndex, sub1.get(i));
      i++;
      startIndex++;
    }

    while (j < sub2Size) {
      objs.set(startIndex, sub2.get(j));
      j++;
      startIndex++;
    }
  }

  /**
   * Sort an arrayList using merge sort
   * Time complexity: O(n * ln(n))
   *
   * @param objs the arrayList to order
   * @param comp the comparator to define the ordering
   * @param left the left index of the subarray
   * @param right the right index of the subarray
   */
  public void sortarrayList(ArrayList<T> objs, Comparator<T> comp, 
      int left, int right) {
    if (left < right) {
      int middle = left + (right - left) / 2; 
      sortarrayList(objs, comp, left, middle);
      sortarrayList(objs, comp, middle + 1, right);

      merge(objs, comp, left, middle, right);
    }
  }

  /**
   * Sort an arrayList using merge sort
   * Time complexity: O(n * ln(n))
   *
   * @param objs the arrayList to order
   * @param comp the comparator to define the ordering
   */
  public void sort(ArrayList<T> objs, Comparator<T> comp) {
    sortarrayList(objs, comp, 0, objs.size());
  }
} 