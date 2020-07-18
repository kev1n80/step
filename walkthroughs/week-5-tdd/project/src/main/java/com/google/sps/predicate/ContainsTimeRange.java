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

package com.google.sps.predicate;

import com.google.sps.Event;
import com.google.sps.TimeRange;
import com.google.sps.algorithms.BinarySearch;
import com.google.sps.algorithms.MergeSort;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.Set;

/**
 * Represents an object that check if two Collection<String> are disjoint 
 */
public final class ContainsTimeRange implements Predicate<TimeRange> {
  private final TimeRange[] timeRanges;

  /**
   * A constructor that stores the attendees string array and orders it
   * Time Complexity: O(n*ln(n))
   *
   * @param timeRanges the arraylist of ordered TimeRanges that we will compare
   *    with another TimeRange
   */ 
  public ContainsTimeRange(ArrayList<TimeRange> timeRanges) {
    TimeRange[] timeRangesArray = new TimeRange[timeRanges.size()];
    timeRangesArray = timeRanges.toArray(timeRangesArray);

    this.timeRanges = timeRangesArray;
  }

  /**
   * Checks if an event shares an attendee with this class's attendees
   * Time Complexity: O(ln(n))
   *
   * @param other the other event we are comparing with
   * @return a boolean stating whether this TimeRange share an attendee with this 
   *    class's collection of attendees
   */
  @Override
  public boolean test(TimeRange otherTimeRange) {
    int timeRangesSize = this.timeRanges.length;
    boolean contains;

    int index = BinarySearch.binarySearchTimeRange(this.timeRanges, 0, 
        timeRangesSize - 1, otherTimeRange);
    if (index >= 0) {
      return true;
    }

    return false;      
  }
}
