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
   * @param events the Collection of events that will turn into its start and 
   *     end time
   * @return An ArrayList that contains an array of start and end times, and 
   *     will not have any events with the same start time.
   */
  public static ArrayList<int[]> eventToTime(Event[] eventsArray) 
      throws Exception {
    ArrayList<int[]> eventTimes = new ArrayList<>();
    TimeRange previousEventTR = TimeRange.fromStartDuration(0, 0);

    for (Event event : eventsArray) {
      TimeRange eventTR = event.getWhen();
      int eventTRStart = eventTR.start();
      int previousEventTRStart = previousEventTR.start();
      // check if this event starts after the previous event
      if (eventTRStart > previousEventTRStart) {
        // if the previous event contains this event, don't add this event
        if (!previousEventTR.contains(eventTR)) {
          int[] eventTime = new int[] {eventTR.start(), eventTR.end()};
          eventTimes.add(eventTime);
          previousEventTR = eventTR;
        }
      } else if (eventTRStart == previousEventTRStart) {
        // if they are the same, keep the one with the longer duration
        if (eventTR.duration() > previousEventTR.duration()) {
          int lastIndex = eventTimes.size() - 1;
          if (eventTimes.size() == 0) {
            lastIndex = 0;
          }
          int[] eventTime = new int[] {eventTRStart, eventTR.end()};
          eventTimes.add(lastIndex, eventTime);
          previousEventTR = eventTR; 
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
        System.err.println("Add Available Time");
        System.err.println("endTime: " + prevEnd);
        System.err.println("startTime: " + start);
        System.err.println("availableDuration: " + availableDuration);
        System.err.println("duration: " + duration);
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
  public static Collection<TimeRange> timeAvailable(ArrayList<int[]> times, 
      int duration) {
    System.err.println("TO TIME t: " + times.size());
    int endTime = TimeRange.START_OF_DAY;
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (int[] time : times) {
      int start = time[0];
      int end = time[1];
      
      addAvailableTime(start, endTime, duration, availableTimes);

      endTime = end;
      System.err.println("NEW endTime: " + endTime);
    }

    int endOfDay = TimeRange.END_OF_DAY + 1;
    System.err.println("endTime: " + endTime);
    System.err.println("endOfDay: " + endOfDay);
    addAvailableTime(endOfDay, endTime, duration, availableTimes);

    System.err.println("Available Times size: " + availableTimes.size());
    return availableTimes;
  }
  
  /**
   * Returns an array with all of the events that meet the requirements set in 
   *    the predicate.
   *
   * @param events the array of events that we are going to filter through
   * @param pred the predicate that will decide whether or not to keep an event
   * @retunr an array of events that meet the requirements set in the predicate
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
    filteredEventsArray = filteredEvents.toArray(filteredEventsArray);
    return filteredEventsArray;
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
    // throw new UnsupportedOperationException("TODO: Implement this method.");

    // check if duration of meeting is longer than a day or a negative number
    int durationMeeting = Math.toIntExact(request.getDuration());
    System.err.println("DURATION: " + durationMeeting);
    // long durationMeeting = request.getDuration();
    if (durationMeeting > 1440 || durationMeeting < 0) {
      System.err.println("EDGE: duration meeting out of scope");
      return new ArrayList<TimeRange>();
    } 

    if (durationMeeting == 0) {
      System.err.println("EDGE: duration meeting is 0");
      TimeRange wholeDay = TimeRange.WHOLE_DAY;
      return new ArrayList<TimeRange>(Arrays.asList(wholeDay));
    }
    
    // Filter events input
    Event[] eventsArray = new Event[events.size()];
    eventsArray = events.toArray(eventsArray);
    System.err.println("INITIAL e: " + eventsArray.length);

    // remove all events that do not have the attendees from the meeting request 
    Predicate<Event> isIntersection = new IsIntersection(request.getAttendees());
    eventsArray = includeEventIf(eventsArray, isIntersection);
    System.err.println("PRED e: " + eventsArray.length);
    // DELETE afterward
    for (Event event : eventsArray) {
      TimeRange tr = event.getWhen();
      System.err.print("- (" + tr.start() + ", ");
      System.err.print(tr.end() + ") \n");
    }

    // sort the events that are remaining
    MergeSort<Event> merge = new MergeSort<Event>();
    merge.sort(eventsArray, new SortEventsByTime());
    System.err.println("SORTED e: " + eventsArray.length);
    // DELETE afterward
    for (Event event : eventsArray) {
      TimeRange tr = event.getWhen();
      System.err.print("- (" + tr.start() + ", ");
      System.err.print(tr.end() + ") \n");
    }
    // Get the times of the event and remove events with the same times
    // (but keep the longest duration)
    ArrayList<int[]> times = new ArrayList<int[]>();
    try {
      times = eventToTime(eventsArray);
      System.err.println("TO TIME e: " + eventsArray.length);
      System.err.println("TO TIME t: " + times.size());
      
      for (int[] time : times) {
        System.err.print("- (" + time[0] + ", ");
        System.err.print(time[1] + ") \n");
      }
    } catch (Exception e) {
      String errorMessage = "Error: " + e.getMessage();
      System.err.println(errorMessage);
    }

    // Compare filtered events input to meeting request
    // Find the time available for this meeting
    Collection<TimeRange> availableTimes = timeAvailable(times, durationMeeting);
    System.err.println("AVAILABLE: " + availableTimes.size());
    System.err.println();
    return availableTimes;
  }
}
