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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.sps.utility.ValidateInput;

/** 
* Servlet that creates comment entities and redirects the user back to the 
* blog section of the portfolio page.
*
* @param request which contains data to retrieve comments
* @param response
* @return comments in the form of json more specifically a List<Comment>
*/
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  // Will only show the 30 most recent comments. Returns a List<Comment> 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Receive input from the modify number of comments shown form
    ValidateInput validateInput = new ValidateInput();
    int maxNumComments = 5;
    int numComments = validateInput.getUserNum(request, "num-comments", 1, maxNumComments);
    if (numComments == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      1 + " to " + maxNumComments + ".");
      return;
    }

    // Receive input on which blog we are retrieving comments from
    int maxNumBlogs = 5;
    int blogNumber = validateInput.getUserNum(request, "blog-number", 1, maxNumBlogs);
    System.err.println("blog number " + blogNumber);
    if (blogNumber == -1) {
      System.err.println("Please enter an integer between " +  
      1 + " to " + maxNumBlogs + ".");
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      1 + " to " + maxNumBlogs + ".");
      return;
    }

    // Retrieve Comments from Datastore for the given blog post
    FilterPredicate filterBlogComments = new FilterPredicate("blogNumber", 
    FilterOperator.EQUAL, blogNumber);
    Query query = new Query("Comment").setFilter(filterBlogComments).addSort("timestamp", SortDirection.DESCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Receive input from the pagination to see which comments to show
    FetchOptions entitiesLimit = FetchOptions.Builder.withLimit(30);
    double totalComments = results.countEntities(entitiesLimit);

    // If there are comments then return a list of comments
    List<Comment> comments = new ArrayList<> ();
    if (totalComments > 0) {
      int maxPageNum = (int) Math.ceil(totalComments / numComments);
      int pageNum = validateInput.getUserNum(request, "page-number", 0, maxPageNum);
      if (pageNum == -1) {
        response.setContentType("text/html");
        response.getWriter().println("Please enter an integer between " +  
        0 + " to " + maxPageNum + ".");
        return;
      }
      
      int commentStartIndex = (pageNum - 1) * numComments;
      int commentEndIndex = commentStartIndex + numComments;
      int lastComment = Math.min(commentEndIndex, (int) totalComments);

      // Turn prepared query into a list
      List<Entity> entitiesList = results.asList(entitiesLimit);
      for (int i = commentStartIndex; i < lastComment; i++) {
        Entity entity = entitiesList.get(i);
        long id = entity.getKey().getId();
        String content = (String) entity.getProperty("content");
        long timestamp = (long) entity.getProperty("timestamp");
        String name = (String) entity.getProperty("name");

        Comment comment = new Comment(id, content, timestamp, name);
        comments.add(comment);
      }
    }

    String jsonComments = new Gson().toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }
}
