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
import com.google.sps.predicate.IsIntersection;
import com.google.sps.predicate.ContainsTimeRange;
import com.google.sps.TimeRange;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
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
   *    with the same start time.
   * Also, it will remove an event that is contained within another event.
   * Time Complexity: O(n)
   *
   * @param eventsArray the Collection of events that will turn into its start and 
   *     end time
   * @return An ArrayList that contains an array of start and end times, and 
   *     will not have any events with the same start time.
   */
  public static ArrayList<int[]> eventToTime(Event[] eventsArray) 
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
  public static void addAvailableTime(int start, int prevEnd, int duration,
      ArrayList<TimeRange> availableTimes) {
    if (start > prevEnd) {
      int availableDuration = start - prevEnd;
      if (availableDuration >= duration) {
        TimeRange availableTime = TimeRange.fromStartDuration(prevEnd, 
            availableDuration);
        availableTimes.add(availableTime);
      }
    }
  }

  /**
   * Returns the times available to have the meeting of a certain duration
   * Time Complexity: O(n)
   *
   * @param times Contains an arraylist of an array of start and end times
   *    this array should be ordered and contain no duplicate start times
   * @param duration the duration of time the meeting request lasts
   * @return an arraylist of times available whose duration are >= duration
   */
  public static ArrayList<TimeRange> timeAvailable(ArrayList<int[]> times, 
      int duration) {
    int endTime = TimeRange.START_OF_DAY;
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (int[] time : times) {
      int start = time[0];
      int end = time[1];
      
      addAvailableTime(start, endTime, duration, availableTimes);

      endTime = end;
    }

    int endOfDay = TimeRange.END_OF_DAY + 1;
    addAvailableTime(endOfDay, endTime, duration, availableTimes);

    return availableTimes;
  }
  
  /**
   * Returns an array with all of the events that meet the requirements set in 
   *    the predicate.
   * Time Complexity: O(n^2 * ln(n))
   *
   * @param events the array of events that we are going to filter through
   * @param pred the predicate that will decide whether or not to keep an event
   * @return an array of events that meet the requirements set in the predicate
   */
  public static Event[] includeEventIf(Event[] events, Predicate<Event> pred) {
      ArrayList<Event> filteredEvents = new ArrayList<Event>();
    for (Event event : events) {
      // if the events share an attenddee then add it to the array
      if (pred.test(event)) {
        filteredEvents.add(event);
      }
    }
    Event[] filteredEventsArray = new Event[filteredEvents.size()];
    return filteredEvents.toArray(filteredEventsArray);
  }

  public ArrayList<int[]> filterAndSortEvent(Event[] eventsArray, 
      Collection<String> attendees) throws Exception {
    // remove all events that do not have the attendees from the meeting request 
    Predicate<Event> isIntersection = new IsIntersection(attendees);
    eventsArray = includeEventIf(eventsArray, isIntersection);      
    // sort the events that are remaining
    MergeSort<Event> merge = new MergeSort<Event>();
    merge.sort(eventsArray, new SortEventsByTime());

    // Get the times of the event, how many optional attendees are attending  
    //    the event, and remove events with the same times
    //    (but keep the longest duration) and the events that are contained in 
    //    another event
    ArrayList<int[]> times = new ArrayList<int[]>();
    try {
      times = eventToTime(eventsArray);  
    } catch (Exception e) {
      throw e;
    }

    return times;
  }

  public ArrayList<int[]> filterAndSortTimes(int[] timesArray, 
      Collection<String> attendees) throws Exception {
    // remove all events that do not have the attendees from the meeting request 
    Predicate<Event> isIntersection = new IsIntersection(attendees);
    eventsArray = includeEventIf(eventsArray, isIntersection);      
    // sort the events that are remaining
    MergeSort<Event> merge = new MergeSort<Event>();
    merge.sort(eventsArray, new SortEventsByTime());

    // Get the times of the event, how many optional attendees are attending  
    //    the event, and remove events with the same times
    //    (but keep the longest duration) and the events that are contained in 
    //    another event
    ArrayList<int[]> times = new ArrayList<int[]>();
    try {
      times = eventToTime(eventsArray);  
    } catch (Exception e) {
      throw e;
    }

    return times;
  }  

  /**
   * Returns an array with all of the timeRanges that meet the requirements set
   *     in the predicate.
   * Time Complexity: O(n^2 * ln(n))
   *
   * @param timeRanges the array of timeRanges that we are going to filter 
   *     through
   * @param pred the predicate that will decide whether or not to keep an event
   * @return an array of timeRanges that meet the requirements set in the 
   *     predicate
   */
  public static ArrayList<TimeRange> includeTimeRangeIf(
      ArrayList<TimeRange> timeRanges, Predicate<TimeRange> pred) {
    ArrayList<TimeRange> filteredTimeRanges = new ArrayList<TimeRange>();
    for (TimeRange timeRange : timeRanges) {
      // if the events share an attenddee then add it to the array
      if (pred.test(timeRange)) {
        filteredTimeRanges.add(timeRange);
        System.err.println(timeRange.toString + "was added");
      }
    }
    return filteredTimeRanges;
  }

  public static ArrayList<TimeRange> getIntersection(
      ArrayList<TimeRange> timeRanges, Predicate<TimeRange> pred) {
    
    return filteredTimeRanges;
  }

  public ArrayList<TimeRange> mandatoryAvailableTimes(Event[] eventsArray,  
      MeetingRequest request, int durationMeetingMinutes) throws Exception{
    ArrayList<int[]> times = new ArrayList<int[]>();
    try {
      times = filterAndSortEvent(eventsArray, request.getAttendees());  
    } catch (Exception e) {
      throw e;
    }

    return times;
  }

  public ArrayList<TimeRange> optionalAvailableTimes(Event[] eventsArray,  
      MeetingRequest request, ArrayList<int[]> mandatoryTimes, 
      int durationMeetingMinutes) throws Exception {
    ArrayList<int[]> times = new ArrayList<int[]>();
    try {
      times = filterAndSortEvent(eventsArray, request.getOptionalAttendees());  
    } catch (Exception e) {
      throw e;
    }

    ArrayList<int[]> allTimes = times.addAll(mandatoryTimes);
    allTimes = filterAndSortTimes();

    // Compare filtered events input to meeting request
    //    Find the time available for this meeting
    ArrayList<TimeRange> availableTimes = timeAvailable(allTimes, 
        durationMeetingMinutes); 
      
    // while (times.size() > 0 && availableTimes.size() == 0) {
    //   // remove time with smallest amount of attendees from times then make 
    //   //     new allTimes
    //   ArrayList<TimeRange> availableTimes = timeAvailable(allTimes, 
    //     durationMeetingMinutes);
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
    
    // Filter events input
    Event[] eventsArray = new Event[events.size()];
    eventsArray = events.toArray(eventsArray);

    ArrayList<int[]> availableMandatoryTimes;
    try {
      availableMandatoryTimes = mandatoryAvailableTimes(eventsArray, 
          request, durationMeetingMinutes);
    } catch (Exception e) {
      throw e;
    }

    ArrayList<TimeRange> availableOptionalTimes;
    try {
      availableOptionalTimes = optionalAvailableTimes(eventsArray,  
      request, availableMandatoryTimes, durationMeetingMinutes);
    } catch (Exception e) {
      throw e;
    }

    if (availableOptionalTimes.size() == 0) {
      return timeAvailable(times, durationMeetingMinutes);
    }

    return availableOptionalTimes;
  }
}
