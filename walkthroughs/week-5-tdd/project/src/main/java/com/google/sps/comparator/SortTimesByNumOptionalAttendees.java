//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.comparator;

import java.util.Comparator; 

/** 
  * Used for sorting in ascending order of their number of optional attendees
  *     in descending order of duration (or latest end time).
  * Context:
  * For the arrays the index is as follows:
  * [0] = start, [1] = end, [2] = duration, [3] = numOptionalAttendees     
  * ASSUMPTION:
  * - The int[] is of size 4 
  */
public class SortTimesByNumOptionalAttendees implements Comparator<int[]> { 
  /** 
   * Used for sorting in ascending order of their number of optional attendees
   * Time complexity: O(1)
   *
   * @param first the first Time or int[] of size 4
   * @param second the second Time or int[] of size 4
   * @return an int that states the ordering of the two events
   */
  @Override
  public int compare(int[] first, int[] second) { 
    int firstNumOptionalAttendees = first[3];
    int secondNumOptionalAttendees = second[3];
    return Long.compare(firstNumOptionalAttendees, secondNumOptionalAttendees);
  } 
} 
