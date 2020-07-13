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
import com.google.sps.utility.CommentConstants;
import com.google.sps.utility.ValidateInput;

/** 
 * Servlet that creates comment objects from entities and returns the list of 
 * comment entities.
 */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  static final int COMMENT_LIMIT = 30;
  static final int URL_LIMIT = 35;

  /** 
   * Will only show the 30 most recent comments.
   * Returns the comments associated with the page the user is on, which 
   * is found based on their input.
   *
   * @param request which contains data to retrieve comments
   * @param response
   * @return comments in the form of json (List<Comment>)
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Receive input from the modify number of comments shown form
    int numComments;
    try {
      numComments = ValidateInput.getUserNum(request, "num-comments", 1, 
          CommentConstants.MAX_NUM_COMMENTS);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
      return;
    }

    // Receive input on which blog we are retrieving comments from
    int blogNumber;
    try {
      blogNumber = ValidateInput.getUserNum(request, "blog-number", 1, 
          CommentConstants.MAX_NUM_BLOGS);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
      return;
    }    

    // Retrieve Comments from Datastore for the given blog post
    FilterPredicate filterBlogComments = new FilterPredicate("blogNumber", 
        FilterOperator.EQUAL, blogNumber);
    Query query = new Query("Comment").setFilter(filterBlogComments);
    query = query.addSort("timestamp", SortDirection.DESCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Receive input from the pagination to see which comments to show
    FetchOptions entitiesLimit = FetchOptions.Builder.withLimit(COMMENT_LIMIT);
    double totalComments = results.countEntities(entitiesLimit);

    // If there are comments then return a list of comments
    List<Comment> comments = new ArrayList<> ();
    if (totalComments > 0) {
      int maxPageNum = (int) Math.ceil(totalComments / numComments);
      int pageNum;
      try {
        pageNum = ValidateInput.getUserNum(request, "page-number", 0,      
            maxPageNum);
      } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
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
        String imageURL = (String) entity.getProperty("image");

        Comment comment = new Comment(id, content, timestamp, name, imageURL);
        comments.add(comment);
      }
    }

    String jsonComments = new Gson().toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }
}
