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
public final class IsInTimeRange implements Predicate<Event> {
  private final ArrayList<TimeRange> timeRanges;

  /**
   * A constructor that stores the attendees string array and orders it
   * Time Complexity: O(n*ln(n))
   *
   * @param timeRanges the collection of Strings that we will compare with 
   *    another collection of Strings
   */ 
  public IsInTimeRange(ArrayList<TimeRange> timeRanges) {
    this.timeRanges = timeRanges;
  }

  /**
   * Checks if an event shares an attendee with this class's attendees
   * Time Complexity: O(n*ln(n))
   *
   * @param other the other event we are comparing with
   * @return a boolean stating whether this event share an attendee with this 
   *    class's collection of attendees
   */
  @Override
  public boolean test(Event other) {
    TimeRange eventTimeRange = other.getWhen();
    boolean contains;

    for (TimeRange timeRange : this.timeRanges) {
      if (timeRange.contains(eventTimeRange)) {
        return true;
      }
    }

    return false;      
  }
}
