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
import java.util.Comparator; 

/**
 * Represents a merge sort object
 */
public final class MergeSort<T> {

  public void merge(T[] events, Comparator<T> eventComp,
      int left, int middle, int right) {
    // prep by creating the two subarrays
    int sub1Size = left - middle + 1;
    int sub2Size = right - middle; 

    T[] sub1 = new T[sub1Size];
    T[] sub2 = new T[sub2Size];

    for (int i = 0; i < sub1Size; i++) {
      sub1[i] = events[left + i];
    }
    for (int i = 0; i < sub2Size; i++) {
      sub2[i] = events[middle + 1 + i];
    }

    // merge the two subarrays
    int startIndex = left;
    int i = 0;
    int j = 0;
    while (i < sub1Size && j < sub2Size) {
      T first = sub1[i];
      T second = sub2[j];
      int order = eventComp.compare(first, second);
      if (order < 0) {
        i++;
        events[startIndex] = first;
      } else {
        j++;
        events[startIndex] = second;
      }
      startIndex++;
    }

    // merge the leftover
    while (i < sub1Size) {
      events[startIndex] = sub1[i];
      i++;
      startIndex++;
    }

    while (j < sub2Size) {
      events[startIndex] = sub2[j];
      j++;
      startIndex++;
    }
  }

  public static void sortArray(T[] events, Comparator<T> eventComp, 
      int left, int right) {
    if (left < right) {
      int middle = left + (right - left) / 2; 
      sort(events, eventComp, left, middle);
      sort(events, eventComp, middle + 1, right);

      merge(events, eventComp, left, middle, right);
    }
  }

  public static void sort(T[] events, Comparator<T> eventComp) {
    sortArray(events, eventComp, 0, events.length - 1);
  }
} 