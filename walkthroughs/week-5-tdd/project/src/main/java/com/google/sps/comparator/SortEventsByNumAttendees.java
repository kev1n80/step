//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.comparator;

import com.google.sps.Event;
import com.google.sps.TimeRange;
import com.google.sps.algorithms.BinarySearch;
import com.google.sps.algorithms.MergeSort;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator; 
import java.util.Set;

/** 
   * Used for sorting in descending order of the number optional attendees and 
   *     for a tie breaker in descending order of duration.
  */
public class SortEventsByNumAttendees implements Comparator<Event> { 
  private final String[] attendees;

  public SortEventsByNumAttendees(Collection<String> attendees) {
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
   * Return the number of attendees attending this event
   * Time complexity: O(n*ln(n))
   *
   * @param event an Eventx
   * @return an int that states the number of attendees attending the event
   */
  public int numAttendees(Event event) {
    boolean contains;

    Set<String> eventAttendees = event.getAttendees();

    int numAttendeesAtEvent = 0;
    for (String eventAttendee : eventAttendees) {
      if (numAttendeesAtEvent == attendees.length) {
        // event contains all attendees
        return numAttendeesAtEvent;
      } else {
        int index = BinarySearch.binarySearchString(attendees, 0, 
            attendees.length - 1, eventAttendee);
        if (index >= 0) {
          numAttendeesAtEvent ++;
        }      
      }        
    }

    return numAttendeesAtEvent;   
  }

  /** 
   * Used for sorting in descending order of the number optional attendees and 
   *     for a tie breaker in descending order of duration.
   * Time complexity: O(1)
   *
   * @param first the first Event
   * @param second the second Event
   * @return an int that states the ordering of the two events
   */
  @Override
  public int compare(Event first, Event second) { 
    /** 
     * if they start at the same time, the one with the longest duration 
     *     shows up first
     */
    int firstNumAttendees = numAttendees(first);
    int secondNumAttendees = numAttendees(second);
    int order = Long.compare(firstNumAttendees, secondNumAttendees);

    if (order == 0) {
      return (-1 * Long.compare(firstNumAttendees, secondNumAttendees));
    }
    return order;
  } 
} 
