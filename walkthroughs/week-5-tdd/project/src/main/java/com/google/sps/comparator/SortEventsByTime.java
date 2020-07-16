//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.comparator;

import com.google.sps.Event;
import com.google.sps.TimeRange;
import java.util.Comparator; 

/** 
  * Sorts events in ascending order based on their start time, and if they
  * the same start time the one with the longest duration is first.
  */
public class SortEventsByTime implements Comparator<Event> { 
  /** 
  * Used for sorting in ascending order of start time and for a tie breaker
  * in descending order of duration.
  * Time complexity: O(1)
  *
  * @param first the first Event
  * @param second the second Event
  * @return an int that states the ordering of the two events
  */
  @Override
  public int compare(Event first, Event second) { 
    TimeRange firstTime = first.getWhen();
    TimeRange secondTime = second.getWhen();
    int order = TimeRange.ORDER_BY_START.compare(firstTime, secondTime);
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
