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
public final class IsIntersection implements Predicate<Event> {
  private final String[] attendees;

  /**
   * A constructor that stores the attendees string array and orders it
   * Time Complexity: O(n*ln(n))
   *
   * @param attendees the collection of Strings that we will compare with 
   *    another collection of Strings
   */ 
  public IsIntersection(Collection<String> attendees) {
    ArrayList<String> attendeesList = Collections.list(Collections.enumeration
        (attendees));

    // order the array
    MergeSort<String> merge = new MergeSort<String>();
    merge.sort(attendeesList, String.CASE_INSENSITIVE_ORDER);

    String[] attendeesArray = new String[attendeesList.size()];
    attendeesArray = attendeesList.toArray(attendeesArray);

    this.attendees = attendeesArray;
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
    Set<String> eventAttendees = other.getAttendees();
    boolean contains;

    for (String eventAttendee : eventAttendees) {
      // enter binary search
      int index = BinarySearch.binarySearchString(this.attendees, 0, 
          attendees.length - 1, eventAttendee);
      if (index >= 0) {
        return true;
      }
    }

    return false;      
  }

/**
 * Returns the number of attendees that are attending this event
 * Time Complexity: O(n*ln(n)) 
 *
 * @param other the other event we are comparing with
 * @return the number of attendees that are attending this event
 */
  public int numAttendeesAttendingEvent(Event other) {
    Set<String> eventAttendees = other.getAttendees();
    int numAttendees = 0;

    for (String eventAttendee : eventAttendees) {
      // enter binary search
      int index = BinarySearch.binarySearchString(attendees, 0, 
          attendees.length - 1, eventAttendee);
      if (index >= 0) {
        numAttendees ++;
      }
    }

    return numAttendees;     
  }
}
