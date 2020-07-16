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
import java.util.Arrays;
import java.util.Comparator; 

/**
 * Represents a merge sort object
 */
public final class MergeSort<T> {

  /**
   * Merges two subarrays into an ordered array
   * Time Complexity: O(n)
   * 
   * @param array the arraylist we will order 
   * @param comp the comparator that  
   * @param left the leftmost index of the sub array
   * @param middle the middle index of the subarray
   * @param right the right index of the subarray
   */
  public void merge(T[] array, Comparator<T> comp,
      int left, int middle, int right) {
    // prep by creating the two subarrays
    T[] sub1 = Arrays.copyOfRange(array, left, middle);
    T[] sub2 = Arrays.copyOfRange(array, middle + 1, right);

    int sub1Size = sub1.length;
    int sub2Size = sub2.length; 

    // merge the two subarrays
    int startIndex = left;
    int i = 0;
    int j = 0;
    while (i < sub1Size && j < sub2Size) {
      T first = sub1[i];
      T second = sub2[j];
      int order = comp.compare(first, second);
      if (order < 0) {
        i++;
        array[startIndex] = first;
      } else {
        j++;
        array[startIndex] = second;
      }
      startIndex++;
    }

    // merge the leftover
    while (i < sub1Size) {
      array[startIndex] = sub1[i];
      i++;
      startIndex++;
    }

    while (j < sub2Size) {
      array[startIndex] = sub2[j];
      j++;
      startIndex++;
    }
  }

  /**
   * Sort an array using merge sort
   * Time complexity: O(n * ln(n))
   *
   * @param array the array to order
   * @param comp the comparator to define the ordering
   * @param left the left index of the subarray
   * @param right the right index of the subarray
   */
  public void sortArray(T[] array, Comparator<T> comp, 
      int left, int right) {
    if (left < right) {
      int middle = left + (right - left) / 2; 
      sortArray(array, comp, left, middle);
      sortArray(array, comp, middle + 1, right);

      merge(array, comp, left, middle, right);
    }
  }

  /**
   * Sort an array using merge sort
   * Time complexity: O(n * ln(n))
   *
   * @param array the array to order
   * @param comp the comparator to define the ordering
   */
  public void sort(T[] array, Comparator<T> comp) {
    sortArray(array, comp, 0, array.length - 1);
  }
} 