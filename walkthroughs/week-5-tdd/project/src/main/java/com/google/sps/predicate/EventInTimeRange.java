//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.predicate;

import com.google.sps.Event;
import com.google.sps.TimeRange;
import java.util.ArrayList; 
import java.util.function.Predicate;

public class EventInTimeRange<Event> implements Predicate<Event> {  
  private final ArrayList<TimeRange> timeRanges;
  public EventInTimeRange(ArrayList<TimeRange> timeRanges) {
    this.timeRanges = timeRanges;
  }

  /**
   * Checks if an event occurs within one of the time ranges
   * Time Complexity: O(n)
   *
   * @param event the event 
   * @return a boolean stating whether this event occurs within one of the time 
   *     ranges
   */
  @Override
  public boolean test(Event event) {
    TimeRange eventTimeRange = event.getWhen();
    boolean contains;

    for (TimeRange timeRange : this.timeRanges) {
      // enter binary search
      if (timeRange.contains(eventTimeRange)) {
        return true;
      }
    }

    return false;      
  }
}