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
import com.google.sps.comparator.SortTimesByNumOptionalAttendees;
import com.google.sps.comparator.SortTimesAscending;
import com.google.sps.filterAndSort.FilterAndSort;
import com.google.sps.predicate.EventInTimeRange;
import com.google.sps.predicate.IncludeIf;
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
   *     array that contains the start and end time.
   * Also, it will remove all of the events with the same start time but will 
   *     keep the event with the greatest duration (or latest end time) of  
   *     those with the same start time.
   * Also, it will remove an event that is contained within another event.
   * Time Complexity: O(n)
   *
   * @param eventsArray the Collection of events that will turn into its start 
   *     and end time
   * @return An ArrayList that contains an array of start and end times, and 
   *     will not have any events with the same start time.
   */
  public static ArrayList<int[]> eventToFilteredTime(ArrayList<Event> 
      eventsArray) throws Exception {
    ArrayList<int[]> eventTimes = new ArrayList<int[]>();
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
   * Turns an ordered Collection of events (by time) into an ArrayList of an 
   *     array that contains the start, end time, duration, and the number of 
   *     optional attendees attending.
   * Time Complexity: O(n)
   *
   * @param eventsArray the Collection of events that will turn into its start, 
   *     end time, duration, and the number of optional attendees attending.
   * @param optionalAttendees the list of optional attendees 
   * @return An ArrayList that contains an array of start, end time, duration, 
   *     and the number of optional attendees attending.
   */
  public static ArrayList<int[]> eventToTimeWithAttendeesList (
      ArrayList<Event> eventsArray, Collection<String> optionalAttendees) {
    ArrayList<int[]> eventTimes = new ArrayList<int[]>();
    SortEventsByNumAttendees getNumAttendees = new SortEventsByNumAttendees
        (optionalAttendees);
    for (Event event : eventsArray) {
      TimeRange eventTimeRange = event.getWhen();
      int start = eventTimeRange.start();
      int end = eventTimeRange.end();
      int duration = end - start;
      int numOptionalAttendees = getNumAttendees.numAttendees(event);
      eventTimes.add(new int[] {start, end, duration, numOptionalAttendees});
    }

    return eventTimes;
  } 

  /**
   * Turns an ordered Collection of events (by time) into an ArrayList of an 
   *     array that contains the start and end time.
   * Time Complexity: O(n)
   *
   * @param eventsArray the Collection of events that will turn into its start 
   *     and end time
   * @return An ArrayList that contains an array of start and end times
   */
  public static ArrayList<int[]> eventToTime(ArrayList<Event> eventsArray) {
    ArrayList<int[]> eventTimes = new ArrayList<int[]>();

    for (Event event : eventsArray) {
      TimeRange eventTimeRange = event.getWhen();
      eventTimes.add(new int[] {eventTimeRange.start(), eventTimeRange.end()});
    }

    return eventTimes;
  }  

   /**
   * Turns an ordered Collection of int[] (by time) into an ArrayList of 
   *     TimeRanges
   * Time Complexity: O(n)
   *
   * @param times the ArrayList of int[] that will turn into a TimeRange
   * @return An ArrayList that contains an TimeRanges
   */
  public static ArrayList<TimeRange> timeToTimeRange(ArrayList<int[]> times) 
      throws Exception {
    ArrayList<TimeRange> timeRanges = new ArrayList<TimeRange>();

    for (int[] time : times) {
      timeRanges.add(TimeRange.fromStartEnd(time[0], time[1], false));
    }

    return timeRanges;
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

  /**
   * Returns the times when the optional attendees and mandatory attendees are 
   *     available.
   */
  public ArrayList<TimeRange> optionalAvailableTimes(
      ArrayList<int[]> optionalTimes, ArrayList<int[]> mandatoryTimes, 
      int durationMeetingMinutes) throws Exception {
    ArrayList<int[]> allTimes = (ArrayList<int[]>) optionalTimes.clone();
    
    MergeSort<int[]> merge = new MergeSort<int[]>();
    if (mandatoryTimes.size() > 0) {
      allTimes.addAll(mandatoryTimes);
      merge.sort(allTimes, new SortTimesAscending());      
    }

    // Compare filtered events input to meeting request
    //    Find the time available for this meeting
    ArrayList<TimeRange> availableTimes = timeAvailable(allTimes, 
        durationMeetingMinutes); 
      
    return availableTimes;
  }  
  
  /**
   * Group times in order to satisfy the meetinh request in that their 
   *     duration satisfies the meeting request duration.
   * Context: 
   * For the arrays the index is as follows:
   * [0] = start, [1] = end, [2] = duration, [3] = numOptionalAttendees   
   * ASSUMPTIONS: 
   * - int[] in times will always be contained in a TimeRange in 
   *     availableMandatoryTimes.
   * - first will only be called on after accumulation gains a duration
   * - times is ordered and does not contain an int[] that contains another int[]
   *
   * @param times A filtered arraylist of int[] that represents events. They 
   *     are all within on of the availableMandatoryTimes. 
   * @param availableMandatoryTimes The times when mandatory attendees are 
   *     available.
   * @param meetingRequestDuration The duration of the meeting request.
   * @return An ArrayList of int[] or times whose duration meeting the 
   *     requirement for the meeting request.
   */
  private ArrayList<int[]> groupTimesToSatisfyMeetingRequest(
      ArrayList<int[]> times, ArrayList<TimeRange> availableMandatoryTimes, 
      int meetingRequestDuration) {
    ArrayList<int[]> newTimes = new ArrayList<int[]>();

    TimeRange currentAvailableTimeRange = TimeRange.WHOLE_DAY;
    if (availableMandatoryTimes.size() > 0) {
      currentAvailableTimeRange = availableMandatoryTimes.get(0);
    }
    int timeRangeIndex = 1;

    int[] first;
    int firstDurationBefore;
    int[] prev = new int[]{0, 0, 0, 0};
    int[] accumulation = new int[]{0, 0, 0, 0};

    // Iterate until the second to last element
    for (int i = 0; i < times.size(); i++) {
      int[] time = times.get(i);

      int timeAvailableBefore;
      if (prev[1] >= currentAvailableTimeRange.start()) {
        timeAvailableBefore = prev[1];
      } else {
        timeAvailableBefore = currentAvailableTimeRange.start();
      }
      
      int timeAvailableAfter;
      if (i < (times.size() - 1)) {
        int[] timeAfter = times.get(i + 1);
        
        if (timeAfter[0] < currentAvailableTimeRange.end()) {
          timeAvailableAfter = timeAfter[0];
        } else {
          // check context to know assumptions for this function
          timeAvailableAfter = currentAvailableTimeRange.end();
          currentAvailableTimeRange = availableMandatoryTimes.get(timeRangeIndex);
          timeRangeIndex ++;
        }
      } else {
        timeAvailableAfter = currentAvailableTimeRange.end();
      }

      // check if time duration meets meetingRequest requirements
      int timeDurationBefore = time[0] - timeAvailableBefore;
      int timeDurationAfter = timeAvailableAfter - time[1];
      int timeDuration = timeDurationBefore + time[2] + timeDurationAfter;
      if (timeDuration >= meetingRequestDuration) {
        if (accumulation[2] >= meetingRequestDuration) {
          newTimes.add(accumulation);  
        } 

        accumulation = new int[]{0, 0, 0, 0};
        newTimes.add(time);
      } else {
        int start = 0;
        int end = 0;
        int duration = 0;
        int numOptionalAttendees = 0;
        // If there is no accumulation
        if (accumulation[2] == 0) {
          first = time;
          firstDurationBefore = timeDurationBefore;

          start = timeAvailableBefore;
          end = timeAvailableAfter;
          duration = timeDuration;
          numOptionalAttendees = time[3];

        // The accumulation is long enough to meet the MeetingRequest duration
        } else if (accumulation[2] >= meetingRequestDuration) {
          // Keep the one with the least amount of optional attendees.
          // If the same keep the current, so you can compare the new first 
          //     with the potential next.
          int order = first[3] - time[3];
          if (order < 0) {
            newTimes.add(accumulation);
          } else {
            start = first[1];
            end = timeAvailableAfter;
            duration = accumulation[2] - (firstDurationBefore + first[2]) + 
                (time[2] + timeDurationAfter);
            numOptionalAttendees = accumulation[3] - first[3] + time[3];
          }

        // The accumulation is not long enough to meet the MeetingRequest 
        //     duration
        } else {
          start = accumulation[0];
          end = timeAvailableAfter;
          duration = time[2] + timeDurationAfter;
          numOptionalAttendees = accumulation[3] + time[3];
        }

        accumulation = new int[]{start, end, duration, numOptionalAttendees};
      }

      prev = time;
    }

    return newTimes;
  }

  /**
   * Returns an ArrayList of TimeRanges that include the Times that allow 
   * mandatory attendees and the greatest possible number of optional attendees 
   * to attend.
   */
  public ArrayList<TimeRange> availableTimeWithMostOptionalAttendees (
      ArrayList<Event> filteredOptionalEvents, 
      ArrayList<TimeRange> availableMandatoryTimes, 
      Collection<String> optionalAttendees, int durationMeetingMinutes) {
    // Filter the events to only include event occuring during the available 
    //     mandatory times.
    Predicate<Event> eventInTimeRange = new EventInTimeRange(
        availableMandatoryTimes);
    IncludeIf<Event> includeIf = new IncludeIf<Event>();
    ArrayList<Event> filteredEvents = includeIf.includeIf(
        filteredOptionalEvents, eventInTimeRange);
    
    // Turn arraylist of events to an arraylist of int[] with information 
    ArrayList<int[]> eventTimes = eventToTimeWithAttendeesList(
        filteredOptionalEvents, optionalAttendees);

    // Group times in order to satisfy the meeting request in that their 
    //     duration satisfies the meeting request duration.
    ArrayList<int[]> availableTimes = groupTimesToSatisfyMeetingRequest(
        eventTimes, availableMandatoryTimes, durationMeetingMinutes);

    // Sort the int[] ascending based on the number of optional attendees
    MergeSort<int[]> merge = new MergeSort<int[]>();
    merge.sort(availableTimes, new SortTimesByNumOptionalAttendees());

    // Pick the one(s) with the smallest number of optional attendees
    ArrayList<int[]> availableTimesEfficient = new ArrayList<int[]>();
    if (availableTimes.size() > 0) {
      int numOptionalAttendees = availableTimes.get(0)[3];
      for (int[] time : availableTimes) {
        int timeNumOptionalAttendees = time[3];
        if(timeNumOptionalAttendees != numOptionalAttendees) {
          break;
        }
        availableTimesEfficient.add(time);
      }      
    }

    return timeToTimeRange(availableTimesEfficient);
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

    IncludeIf<Event> includeIf = new IncludeIf<Event>();
    MergeSort<Event> merge = new MergeSort<Event>();

    // filter and sort the events that mandatory attendees are attending
    Predicate<Event> isMandatoryIntersection = new IsIntersection
        (request.getAttendees());
    
    // Filter
    ArrayList<Event> filteredMandatoryEvents = includeIf.includeIf
        (eventsArray, isMandatoryIntersection);

    // Sort
    merge.sort(filteredMandatoryEvents, new SortEventsByTime());

    // Turn events into int[]
    ArrayList<int[]> mandatoryTimes = new ArrayList<int[]>();
    try {
      mandatoryTimes = eventToFilteredTime(filteredMandatoryEvents);  
    } catch (Exception e) {
      throw e;
    }
    
    // filter and sort the events that optional attendees are attending  
    Predicate<Event> isOptionalIntersection = new IsIntersection
        (request.getOptionalAttendees());   

    // Filter
    ArrayList<Event> filteredOptionalEvents = includeIf.includeIf
        (eventsArray, isOptionalIntersection);

    // Sort
    merge.sort(filteredOptionalEvents, new SortEventsByTime()); 
     
    // Turn events into int[]
    ArrayList<int[]> optionalTimes = new ArrayList<int[]>();
    try {
      optionalTimes = eventToFilteredTime(filteredOptionalEvents);  
    } catch (Exception e) {
      throw e;
    }    

    ArrayList<TimeRange> availableOptionalTimes;
    try {
      availableOptionalTimes = optionalAvailableTimes(optionalTimes, 
          mandatoryTimes, durationMeetingMinutes);
    } catch (Exception e) {
      throw e;
    }

    // If there are no available times for all mandatory and optional attendees
    //     then return all of the available times for mandatory attendees
    if (availableOptionalTimes.size() == 0 && mandatoryTimes.size() > 0) {
      ArrayList<TimeRange> availableMandatoryTimes = timeAvailable
          (mandatoryTimes, durationMeetingMinutes);
      ArrayList<TimeRange> efficientAvailableMeetings =   
          availableTimeWithMostOptionalAttendees(filteredOptionalEvents,  
          availableMandatoryTimes, request.getOptionalAttendees(), 
          durationMeetingMinutes);
      if (availableMandatoryTimes.size() > 0 && 
          efficientAvailableMeetings.size() == 0) {
        return availableMandatoryTimes;
      } else {
        return efficientAvailableMeetings;
      }
    }

    return availableOptionalTimes;
  }
}
