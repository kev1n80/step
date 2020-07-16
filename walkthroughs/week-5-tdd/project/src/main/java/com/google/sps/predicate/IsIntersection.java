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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.Set;

/**
 * Represents an object that check if two Collection<String> are disjoint 
 */
public final class IsIntersection implements Predicate<Event> {
  private final Collection<String> attendees;

  public IsIntersection(Collection<String> attendees) {
    this.attendees = attendees;
  }

  /**
   * Checks if there is an intersection between the two collections.
   * Returns true when there is an element that both collections have
   * in common.
   * Time Complexity: O(n*ln(n))
   * 
   * @param first the bigger collection of the two
   * @param second the smaller collection of the two
   * @return a boolean that says wether there is an intersection or not
   */ 
  public boolean hasIntersection(Collection<String> first, 
      Collection<String> second, int firstSize) {
    // Sort the first Array
    String[] firstArray = new String[first.size()];
    firstArray = first.toArray(firstArray);

    MergeSort<String> merge = new MergeSort<String>();
    merge.sort(firstArray, String.CASE_INSENSITIVE_ORDER);
    
    // iterate through the second array
    String[] secondArray = new String[second.size()];
    secondArray = second.toArray(secondArray);
    
    for (String str : secondArray) {
      // enter binary search
      int index = BinarySearch.binarySearchString(firstArray, 0, 
          firstSize - 1, str);
      if (index >= 0) {
        return true;
      }
    }

    return false;     
  }

  /**
   * Checks if an event shares an attendee with this class's attendees
   * Time Complexity: O()
   *
   * @param other the other event we are comparing with
   * @return a boolean stating whether this event share an attendee with this 
   * class's collection of attendees
   */
  @Override
  public boolean test(Event other) {
    Set<String> eventAttendees = other.getAttendees();
    int eventAttendeesSize = eventAttendees.size();
    int attendeesSize = attendees.size();
    boolean contains;
    if (eventAttendeesSize > attendeesSize) {
      contains = hasIntersection(eventAttendees, attendees, eventAttendeesSize);
    } else {
      contains = hasIntersection(attendees, eventAttendees, attendeesSize);
    }
    System.err.println("Contains: " + contains);
    return contains;
  }
}
