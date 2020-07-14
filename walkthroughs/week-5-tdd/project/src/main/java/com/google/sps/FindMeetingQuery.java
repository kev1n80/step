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

package com.google.sps;

import java.util.Collection;

public final class FindMeetingQuery {
  /**
   * Returns all possible time periods throughout the day when everybody 
   * attending this meeting is available. 
   * 
   * @param events All events that are occurring  
   * @param request The meeting that the user wants to create and find time for 
   * @return an array of TimeRange objects
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    throw new UnsupportedOperationException("TODO: Implement this method.");

    // check if duration of meeting is longer than a day or a negative number
    int durationMeeting = request.getDuration();
    if (durationMeeting > 1440 || durationMeeting < 1) {
      // enter error message or throw Exception
    }
    
    // remove all events that do no have the attendees from the meeting request 
    Predicate<Event> isDisjoint = new IsDisjoint(request.getAttendees());
    events.RemoveIf(isDisjoint);

    // sort the events that are remaining
    Collections.sort(events, new SortByTime());

    // Find the time available for this meeting
    // go through the collection, store the beginning time and end time
    // skip through the other events with the same start time because of the 
    //  way that the collection is ordered
  }
}

/**
 * Represents an object that check if two Collection<String> are disjoint 
 */
public class IsDisjoint implements Predicate<Event> {
  private final Collection<String> attendees;

  public isDisjoint(Collection<String> attendees) {
    this.attendees = attendees;
  }

  /**
   * Checks if an event shares an attendee with this class's attendees
   *
   * @param other the other event we are comparing with
   * @return a boolean stating whether this event share an attendee with this 
   * class's collection of attendees
   */
  @Override
  public boolean isEqual(Event other) {
    Set<String> eventAttendees = other.getAttendees();
    int eventAttendeesSize = eventAttendees.size();
    int attendeesSize = attendees.size();
    boolean contains = false;
    if (eventAttendeesSize > attendeesSize) {
      contains = hasIntersection(eventAttendees, attendees, eventAttendeesSize);
    } else {
      contains = hasIntersection(attendees, eventAttendees, attendeesSize);
    }

    return contains;
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
    Collections.sort(first, String.CASE_INSENSITIVE_ORDER);
      
    Iterator secondIterator = second.iterator();
    String[] firstArray = first.toArray();
    for (String str : secondIterator) {
      // enter binary search
      int index = BinarySearch.binarySearchString(firstArray, 0, 
          firstSize - 1, str);
      if (index >= 0) {
        return true;
      }
    }

    return false;     
  }
}

/** 
  * Sorts events in ascending order based on their start time, and if they
  * the same start time the one with the longest duration is first.
  */
public class SortbyTime implements Comparator<Event> { 
  /** 
  * Used for sorting in ascending order of start time and for a tie breaker
  * in descending order of duration.
  * Time complexity: O(1)
  *
  * @param first the first event
  * @param second the second event
  * @return an int that states the ordering of the two events
  */
  @Override
  public int compare(Event first, Event second) { 
    TimeRange firstTime = first.getWhen();
    TimeRange secondTime = second.getWhen();
    int order = TimeRange.ORDER_BY_START(firstTime, secondTime);
    /** 
    * if they start at the same time, the one with the longest duration 
    * shows up first
    */
    if (order == 0) {
      return - Long.compare(firstTime.duration(), secondTime.duration());
    }
    return order;
  } 
} 

/**
 * Represents a binary search object
 */
class BinarySearch { 
    /**
     * Returns the index of the target in the specified array or return -1
     * Time complexity: O(ln(n))
     *
     * @param stringArr the array that we are searching through
     * @param left the leftmost index of the subarray we are currently in
     * @param right the rightmost index of the subarray we are currently in
     * @param target the strign we are looking for
     * @return the index of the target or -1
     */
    public static int binarySearchString(String[] stringArr, int left, 
        int right, String target) { 
        if (r >= left) { 
            int mid = left + (right - left) / 2; 
  
            // If the element is present at the 
            // middle itself 
            if (stringArr[mid] == target) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (stringArr[mid] > target) 
                return binarySearch(stringArr, left, mid - 1, target); 
  
            // Else the element can only be present 
            // in right subarray 
            return binarySearch(stringArr, mid + 1, right, target); 
        } 
  
        // We reach here when element is not present 
        // in array 
        return -1; 
    } 
} 
