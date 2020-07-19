//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.filterAndSort;

import java.util.ArrayList;
import java.util.Comparator; 
import java.util.function.Predicate;
import com.google.sps.algorithms.MergeSort;
import com.google.sps.predicate.IncludeEventIf;

/**
 * Represents a FilterAndSort object
 */
public class FilterAndSort<T> {
  /**
  * Filter and sorts an array of objects based on the comparator and predicate 
  *     given
  * Time Complexity: Depends on the time complexity of pred and comp
  *
  * @param array the array to be filtered and sorted
  * @param pred the condition for the filter 
  * @param comp the comparator used to sort
  * @return a filtered and sorted array
  */  
  public ArrayList<T> filterAndSort(T[] array, Predicate<T> pred, 
      Comparator<T> comp) throws Exception {
    
    IncludeEventIf<T> includeEventIf = new IncludeEventIf<T>();
    ArrayList<T> filteredObjs = includeEventIf.includeEventIf(array, pred);

    MergeSort<T> merge = new MergeSort<T>();
    merge.sort(filteredObjs, comp);

    return filteredObjs;
  }

  public ArrayList<T> filterAndSort(ArrayList<T> list, Predicate<T> pred, 
      Comparator<T> comp) throws Exception {
    
    IncludeEventIf<T> includeEventIf = new IncludeEventIf<T>();
    ArrayList<T> filteredObjs = includeEventIf.includeEventIf(list, pred);

    MergeSort<T> merge = new MergeSort<T>();
    merge.sort(filteredObjs, comp);

    return filteredObjs;
  }  
}