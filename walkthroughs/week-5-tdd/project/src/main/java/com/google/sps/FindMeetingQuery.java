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

import com.google.sps.algorithms.BinarySearch;
import com.google.sps.algorithms.MergeSort;
import com.google.sps.comparator.SortEventsByTime;
import com.google.sps.predicate.IsDisjoint;
import com.google.sps.TimeRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class FindMeetingQuery {
  /**
   * Turns an ordered Collection of events (by time) into an ArrayList of an 
   *    array that contains the start and end time.
   * Also, it will remove all of the events with the same start time but will 
   *    keep the event with the greatest duration (or latest end time) of those 
   *    with the same start 
   *    time.
   * Time Complexity: O(n)
   *
   * @param events the Collection of events that will turn into its start and 
   *     end time
   * @return An ArrayList that contains an array of start and end times, and 
   *     will not have any events with the same start time.
   */
  public static ArrayList<int[]> eventToTime(Collection<Event> events) 
      throws Exception {
    Event[] eventsArray = (Event[]) events.toArray();
    ArrayList<int[]> eventTimes = new ArrayList<>();
    TimeRange previousEventTR = TimeRange.fromStartDuration(0, 0);

    for (int i = 0; i < eventsArray.length; i ++) {
      Event event = eventsArray[i];

      TimeRange eventTR = event.getWhen();
      int eventTRStart = eventTR.start();
      int previousEventTRStart = previousEventTR.start();
      if (eventTRStart > previousEventTRStart) {
        int[] eventTime = new int[] {eventTR.start(), eventTR.end()};
        eventTimes.add(eventTime);
      } else if (eventTRStart == previousEventTRStart) {
        // if they are the same, keep the one with the longer duration
        if (eventTR.duration() > previousEventTR.duration()) {
          int lastIndex = eventTimes.size() - 1;
          int[] eventTime = new int[] {eventTR.start(), eventTR.end()};
          eventTimes.add(lastIndex, eventTime);
        }
      } else {
        // This means that this event started before the previous event.
        // This means that the Collection of events is not ordered
        throw new Exception("The input of Collection is not in order");
      }
    }

    return eventTimes;
  }

  /**
   * Returns the times available to have the meeting of a certain duration
   * Time Complexity: O(n)
   *
   * @param times the times when 
   */
  public static Collection<TimeRange> timeAvailable(ArrayList<int[]> times, 
      int duration) {
    int timesSize = times.size();
    int endTime = 0;
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (int i = 0; i < timesSize; i++) {
      int[] time = times.get(i);
      int start = time[0];
      int end = time[1];

      if (start > endTime) {
        int availableDuration = start - endTime;
        if (availableDuration >= duration) {
          TimeRange availableTime = TimeRange.fromStartDuration(endTime, 
              availableDuration);
          availableTimes.add(availableTime);
        }
        endTime = end;
      }
    }

    return availableTimes;
  }

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
    int durationMeeting = Math.toIntExact(request.getDuration());
    if (durationMeeting > 1440 || durationMeeting < 1) {
      // enter error message or throw Exception
    }
    
    // remove all events that do no have the attendees from the meeting request 
    Predicate<Event> isDisjoint = new IsDisjoint(request.getAttendees());
    events.removeIf(isDisjoint);

    // sort the events that are remaining
    MergeSort.sort(events, new SortEventsByTime());

    // Get the times of the event and remove events with the same times
    // (but keep the longest duration)
    ArrayList<int[]> times = eventToTime(events);

    // Find the time available for this meeting
    Collection<TimeRange> availableTimes = timeAvailable(times, durationMeeting);
    return availableTimes;
  }
}
