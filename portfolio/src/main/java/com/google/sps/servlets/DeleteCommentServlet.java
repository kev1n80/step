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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.data.Comment;
import com.google.sps.utility.CommentConstants;
import com.google.sps.utility.ValidateInput;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that deletes comment entities
 */
@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  /**
   * Deletes comments from that Datastore based on the blog post the button the 
   * user pressed is associated with.
   * 
   * @param request which contains data used to identify a comment in order to
   * delete it
   * @param response
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Receive input on which blog we are retrieving comments from
    int blogNumber; 
    try {
      blogNumber = ValidateInput.getUserNum(request, "blog-number", 1, 
          CommentConstants.MAX_NUM_BLOGS);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }

    // Retrieve Comments from Datastore for the given blog post
    FilterPredicate filterBlogComments = new FilterPredicate("blogNumber", 
        FilterOperator.EQUAL, blogNumber);
    Query query = new Query("Comment").setFilter(filterBlogComments);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Key> commentKeys = new ArrayList<> ();
    for (Entity entity : results.asIterable()) {
      Key commentEntityKey = entity.getKey();
      commentKeys.add(commentEntityKey);
    }
    
    datastore.delete(commentKeys);
  }
}
