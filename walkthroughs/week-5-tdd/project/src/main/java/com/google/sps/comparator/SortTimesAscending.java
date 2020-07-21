//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.comparator;

import java.util.Comparator; 

/** 
   * Used for sorting in ascending order of start time and for a tie breaker
   *     in descending order of duration (or latest end time).
  */
public class SortTimesAscending implements Comparator<int[]> { 
  /** 
   * Used for sorting in ascending order of start time and for a tie breaker
   *     in descending order of duration (or latest end time).
   * Time complexity: O(1)
   *
   * @param first the first Time or int[] in minutes
   * @param second the second Time or int[] in minutes
   * @return an int that states the ordering of the two events
   */
  @Override
  public int compare(int[] first, int[] second) { 
    int firstStartTimeMinutes = first[0];
    int secondStartTimeMinutes = second[0];
    int order = Long.compare(firstStartTimeMinutes, secondStartTimeMinutes);
    /** 
     * if they start at the same time, the one with the longest duration (or 
     *     the latest end time) shows up first
     */
    if (order == 0) {
      int firstEndTimeMinutes = first[1];
      int secondEndTimeMinutes = second[1];
      return (-1 * Long.compare(firstEndTimeMinutes, secondEndTimeMinutes));
    }
    return order;
  } 
} 
