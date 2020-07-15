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

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns the number of comments stored */
@WebServlet("/num-comments")
public class NumCommentsServlet extends HttpServlet {

  static final int COMMENT_LIMIT = 30;

  /** 
   * Returns the number of comments associated with different blog posts.
   *
   * @param request which contains data to retrieve comments
   * @param response
   * @return a list of the number of comments per blog post in the form of json 
   *    (ArrayList<Comment>)
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve Comments from Datastore for the given blog post
    Query query = new Query("Comment").addSort("blogNumber", 
        SortDirection.ASCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Map<Integer, Integer> numComments = new HashMap<Integer, Integer> ();

    // Receive input from the pagination to see which comments to show
    FetchOptions entitiesLimit = FetchOptions.Builder.withLimit(COMMENT_LIMIT);
    double totalComments = results.countEntities(entitiesLimit);

    if (totalComments > 0) {
      int currentNumber = 0;
      Iterable<Entity> entitiesList = results.asIterable();
      for (Entity entity : entitiesList) {
        Integer blogNumber = Math.toIntExact((long) entity.getProperty("blogNumber"));
        if (blogNumber != currentNumber) {
          numComments.put(blogNumber, 1);
          currentNumber = blogNumber;
        } else {
          Integer count = numComments.get(currentNumber) + 1;
          numComments.put(currentNumber, count);
        }
      }
    }

    String jsonNumComments = new Gson().toJson(numComments);
    response.setContentType("application/json;");
    response.getWriter().println(jsonNumComments);
  }
}