//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.predicate;

import java.util.ArrayList;
import java.util.Comparator; 
import java.util.function.Predicate;

/**
 * Filter and sorts an array of objects based on the comparator and predicate 
 *     given
 *
 * @param array the array to be filtered and sorted
 * @param pred the condition for the filter 
 * @param comp the comparator used to sort
 * @return a filtered and sorted array
 */
public class IncludeIf<T> {
  /**
   * Returns an array with all of the objects that meet the requirements set in 
   *    the predicate.
   * Time Complexity: O(n * Time Complexity of pred.test(n))
   *
   * @param array the array of objects that we are going to filter through
   * @param pred the predicate that will decide whether or not to keep an object
   * @return an array of objects that meet the requirements set in the predicate
   */
  public ArrayList<T> includeIf(T[] array, Predicate<T> pred) {
    ArrayList<T> filteredObjs = new ArrayList<T>();
    for (T obj : array) {
      if (pred.test(obj)) {
        filteredObjs.add(obj);
      }
    }
    return filteredObjs;
  }
  
  public ArrayList<T> includeIf(ArrayList<T> list, Predicate<T> pred) {
    ArrayList<T> filteredObjs = new ArrayList<T>();
    for (T obj : list) {
      if (pred.test(obj)) {
        filteredObjs.add(obj);
      }
    }
    return filteredObjs;
  }
}