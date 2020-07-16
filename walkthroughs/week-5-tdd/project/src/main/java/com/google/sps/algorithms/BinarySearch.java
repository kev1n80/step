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
  
            // If the element is present at the 
            // middle itself 
            if (stringArr[mid].equals(target)) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (stringArr[mid].compareTo(target) > 0) 
                return binarySearchString(stringArr, left, mid - 1, target); 
  
            // Else the element can only be present 
            // in right subarray 
            return binarySearchString(stringArr, mid + 1, right, target); 
        } 
  
        // We reach here when element is not present 
        // in array 
        return -1; 
    } 
} 
