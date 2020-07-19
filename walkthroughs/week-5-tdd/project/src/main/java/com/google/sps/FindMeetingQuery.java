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
import com.google.sps.comparator.SortEventsByNumAttendees;
import com.google.sps.comparator.SortTimesAscending;
import com.google.sps.filterAndSort.FilterAndSort;
import com.google.sps.predicate.IncludeEventIf;
import com.google.sps.predicate.IsIntersection;
import com.google.sps.TimeRange;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator; 
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class FindMeetingQuery {
  /**
   * Turns an ordered Collection of events (by time) into an ArrayList of an 
   *    array that contains the start and end time.
   * Also, it will remove all of the events with the same start time but will 
   *    keep the event with the greatest duration (or latest end time) of those 
   *    with the same start time.
   * Also, it will remove an event that is contained within another event.
   * Time Complexity: O(n)
   *
   * @param eventsArray the Collection of events that will turn into its start and 
   *     end time
   * @return An ArrayList that contains an array of start and end times, and 
   *     will not have any events with the same start time.
   */
  public static ArrayList<int[]> eventToTime(ArrayList<Event> eventsArray) 
      throws Exception {
    ArrayList<int[]> eventTimes = new ArrayList<>();
    TimeRange previousEventTimeRange = TimeRange.fromStartDuration(0, 0);

    for (Event event : eventsArray) {
      TimeRange eventTimeRange = event.getWhen();
      int eventTimeRangeStart = eventTimeRange.start();
      int previousEventTimeRangeStart = previousEventTimeRange.start();
      // check if this event starts after the previous event
      if (eventTimeRangeStart > previousEventTimeRangeStart) {
        // if the previous event contains this event, don't add this event
        if (!previousEventTimeRange.contains(eventTimeRange)) {
          int[] eventTime = new int[] {eventTimeRange.start(), eventTimeRange.end()};
          eventTimes.add(eventTime);
          previousEventTimeRange = eventTimeRange;
        }
      } else if (eventTimeRangeStart == previousEventTimeRangeStart) {
        // if they are the same, keep the one with the longer duration
        if (eventTimeRange.duration() > previousEventTimeRange.duration()) {
          int lastIndex = Math.max(eventTimes.size() - 1, 0);
          int[] eventTime = new int[] {eventTimeRangeStart, eventTimeRange.end()};
          eventTimes.add(lastIndex, eventTime);
          previousEventTimeRange = eventTimeRange; 
        }
      } else {
        // This means that the Collection of events is not ordered
        throw new Exception("The input of Collection is not in order");
      }
    }

    return eventTimes;
  }

  /**
   * Adds a time to the arraylist if there is enough time between when the 
   *    previous meeting ended to the time when the current meeting starts for 
   *    the meeting request.
   * Time Complexity: O(1)
   *
   * @param start the start time of the current event
   * @param prevEnd the start time of the previous event
   * @param duration the duration of the meeting request
   * @param availableTimes the arraylist that we can add a time to
   */
  public static boolean addAvailableTime(int start, int prevEnd, int duration,
      ArrayList<TimeRange> availableTimes) {
    if (start > prevEnd) {
      int availableDuration = start - prevEnd;
      if (availableDuration >= duration) {
        TimeRange availableTime = TimeRange.fromStartDuration(prevEnd, 
            availableDuration);
        availableTimes.add(availableTime);
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the times available to have the meeting of a certain duration
   * Time Complexity: O(n)
   *
   * @param times Contains an ordered arraylist of an array of start and end 
   *     times this array should be ordered and contain no duplicate start times
   * @param duration the duration of time the meeting request lasts
   * @return an arraylist of times available whose duration are >= duration
   */
  public ArrayList<TimeRange> timeAvailable(ArrayList<int[]> times, 
      int duration) {
    int startTime = TimeRange.START_OF_DAY;
    int prevEndTime = TimeRange.START_OF_DAY;
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();

    for (int[] time : times) {
      int start = time[0];
      int end = time[1];
      
      // Prevents the case when a time range contains another time range.
      // The first time range should be the one with the longest duration since 
      //     it is ordered
      boolean startsAfterPrev = start > startTime;
      boolean endsAfterPrev = end > prevEndTime;
      boolean isStartOfDay = start == TimeRange.START_OF_DAY;
      boolean prevNotContainsCurrent = (startsAfterPrev || isStartOfDay) && 
          endsAfterPrev;
      if (prevNotContainsCurrent) {
        if (addAvailableTime(start, prevEndTime, duration, availableTimes)) {
          startTime = start;
        }

        prevEndTime = end;
      }
    }

    int endOfDay = TimeRange.END_OF_DAY + 1;
    addAvailableTime(endOfDay, prevEndTime, duration, availableTimes);

    return availableTimes;
  }

  public ArrayList<TimeRange> optionalAvailableTimes(
      ArrayList<Event> optionalEvents, ArrayList<int[]> optionalTimes, 
      ArrayList<int[]> mandatoryTimes, int durationMeetingMinutes) 
      throws Exception {
    ArrayList<int[]> allTimes = (ArrayList<int[]>) optionalTimes.clone();

    if (mandatoryTimes.size() > 0) {
      allTimes.addAll(mandatoryTimes);

      MergeSort<int[]> merge = new MergeSort<int[]>();
      merge.sort(allTimes, new SortTimesAscending());      
    }
    
    // Compare filtered events input to meeting request
    //    Find the time available for this meeting
    ArrayList<TimeRange> availableTimes = timeAvailable(allTimes, 
        durationMeetingMinutes); 
      
    // if (availableTimes.size() == 0) {
      // Comparator<Event> sortByNumAttendees = new SortEventsByNumAttendees
      //     (request.getOptionalAttendees());   

      // while (times.size() > 0 && availableTimes.size() == 0) {
      //   // remove time with smallest amount of attendees (which should be last) from times then make 
      //   //     new allTimes
      //   ArrayList<TimeRange> availableTimes = timeAvailable(allTimes, 
      //     durationMeetingMinutes);
      // }
    // }
      
    return availableTimes;
  }  

  /**
   * Returns all possible time periods throughout the day when everybody 
   *    attending this meeting is available. 
   * Time Complexity: O(n^2 * ln(n))
   * 
   * @param events All events that are occurring  
   * @param request The meeting that the user wants to create and find time for 
   * @return an array of TimeRange objects
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) throws Exception {
    // check if duration of meeting is longer than a day or a negative number
    int durationMeetingMinutes = Math.toIntExact(request.getDuration());
    if (durationMeetingMinutes > 1440 || durationMeetingMinutes < 0) {
      System.err.println("EDGE: duration meeting out of scope");
      return new ArrayList<TimeRange>();
    } 

    if (durationMeetingMinutes == 0) {
      System.err.println("EDGE: duration meeting is 0");
      TimeRange wholeDay = TimeRange.WHOLE_DAY;
      return new ArrayList<TimeRange>(Arrays.asList(wholeDay));
    }
    
    Event[] eventsArray = new Event[events.size()];
    eventsArray = events.toArray(eventsArray);

    IncludeEventIf<Event> includeEventIf = new IncludeEventIf<Event>();
    MergeSort<Event> merge = new MergeSort<Event>();

    // filter and sort the events that mandatory attendees are attending
    Predicate<Event> isMandatoryIntersection = new IsIntersection
        (request.getAttendees());
    
    // Filter
    ArrayList<Event> filteredMandatoryEvents = includeEventIf.includeEventIf
        (eventsArray, isMandatoryIntersection);

    // Sort
    merge.sort(filteredMandatoryEvents, new SortEventsByTime());

    // Turn events into int[]
    ArrayList<int[]> mandatoryTimes = new ArrayList<int[]>();
    try {
      mandatoryTimes = eventToTime(filteredMandatoryEvents);  
    } catch (Exception e) {
      throw e;
    }
    
    // filter and sort the events that optional attendees are attending  
    Predicate<Event> isOptionalIntersection = new IsIntersection
        (request.getOptionalAttendees());   

    // Filter
    ArrayList<Event> filteredOptionalEvents = includeEventIf.includeEventIf
        (eventsArray, isOptionalIntersection);

    // Sort
    merge.sort(filteredOptionalEvents, new SortEventsByTime()); 
     
    // Turn events into int[]
    ArrayList<int[]> optionalTimes = new ArrayList<int[]>();
    try {
      optionalTimes = eventToTime(filteredOptionalEvents);  
    } catch (Exception e) {
      throw e;
    }    

    ArrayList<TimeRange> availableOptionalTimes;
    try {
      availableOptionalTimes = optionalAvailableTimes(filteredOptionalEvents, 
          optionalTimes, mandatoryTimes, durationMeetingMinutes);
    } catch (Exception e) {
      throw e;
    }

    if (availableOptionalTimes.size() == 0 && mandatoryTimes.size() > 0) {
      ArrayList<TimeRange> availableMandatoryTimes = timeAvailable
          (mandatoryTimes, durationMeetingMinutes);
      if (availableMandatoryTimes.size() > 0) {
        return availableMandatoryTimes;
      }
    }

    return availableOptionalTimes;
  }
}
