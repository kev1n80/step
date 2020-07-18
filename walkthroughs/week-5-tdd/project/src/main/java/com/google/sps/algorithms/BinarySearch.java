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

package com.google.sps.algorithms;

import com.google.sps.TimeRange;

/**
 * Represents a binary search object
 */
public final class BinarySearch { 
    /**
     * Returns the index of the target in the specified array or return -1
     * Time complexity: O(ln(n))
     *
     * @param stringArr the array that we are searching through
     * @param left the leftmost index of the subarray we are currently in
     * @param right the rightmost index of the subarray we are currently in
     * @param target the strign we are looking for
     * @return the index of the target or -1
     */
    public static int binarySearchString(String[] stringArr, int left, 
        int right, String target) { 
        if (right >= left) { 
            int mid = left + (right - left) / 2; 
  
            // If the element is the middle element, return middle index
            if (stringArr[mid].equals(target)) 
                return mid; 
  
            // If element is smaller than middle element, then search left 
            //    subarray
            if (stringArr[mid].compareTo(target) > 0) 
                return binarySearchString(stringArr, left, mid - 1, target); 
  
            // Else the element must be in the right subarray
            return binarySearchString(stringArr, mid + 1, right, target); 
        } 
  
        // Element is not in array
        return -1; 
    }

    /**
     * Returns the index of the target in the specified array or return -1
     * Time complexity: O(ln(n))
     *
     * @param timeRangeArr the array that we are searching through
     * @param left the leftmost index of the subarray we are currently in
     * @param right the rightmost index of the subarray we are currently in
     * @param target the strign we are looking for
     * @return the index of the target, -1 if the index does not exist, or -2 
     *     if there is an overlapping between two 
     */
    public static int binarySearchTimeRange(TimeRange[] timeRangeArr, int left, 
        int right, TimeRange target) { 
        if (right >= left) { 
            int mid = left + (right - left) / 2; 
  
            // If the element is the middle element, return middle index
            TimeRange midTimeRange = timeRangeArr[mid];
            if (midTimeRange.equals(target) || midTimeRange.contains(target)) {
              return mid; 
            }   

            if (midTimeRange.overlaps) {
              if (!target.contains(midTimeRange)) {
                return -2;
              }
            }
  
            // If element is smaller than middle element, then search left 
            //    subarray
            if (TimeRange.ORDER_BY_START.compare(timeRangeArr[mid], target) > 0) 
                return binarySearchTimeRange(timeRangeArr, left, mid - 1, target); 
  
            // Else the element must be in the right subarray
            return binarySearchTimeRange(timeRangeArr, mid + 1, right, target); 
        } 
  
        // Element is not in array
        return -1; 
    } 
} 
