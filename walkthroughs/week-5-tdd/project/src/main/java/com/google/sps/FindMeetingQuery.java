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

    // remove all events that do no have the attendees from the meeting request 

    // sort the events that are remaining

    // Find the time available for this meeting
  }
}
