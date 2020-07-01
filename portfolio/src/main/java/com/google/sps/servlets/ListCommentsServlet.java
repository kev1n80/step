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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;

/** Servlet that returns some example content. TODO: modify this file to handle 
comments data */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int minNumComments = 1;
    int maxNumComments = 5;
    // Receive input from the modify number of comments shown form
    int numComments = getNumComments(request, minNumComments, maxNumComments);
    if (numComments == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      minNumComments + " to " + maxNumComments + ".");
      return;
    }

    // Retrieve Comments from Datastore
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<> ();
    Iterator<Entity> commentsIterator = results.asIterator();
    for (int i = 0; i < numComments; i++) {
      if (commentsIterator.hasNext()) {
        Entity entity = commentsIterator.next();
        long id = entity.getKey().getId();
        String content = (String) entity.getProperty("content");
        long timestamp = (long) entity.getProperty("timestamp");

        Comment comment = new Comment(id, content, timestamp);
        comments.add(comment);
      }
      else {
        break;
      }
    }

    String jsonComments = new Gson().toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }

  /** Returns the number of comments shown entered by the user, or -1 if the 
  comment was invalid. Min must be greater than -1 and Max must be greater than 
  or equal to min */
  private int getNumComments(HttpServletRequest request, int min, int max) {
    if (min <= -1) {
      System.err.println("Min (" + min + ") must be greater than -1 ");
      return -1;
    }
    
    if (max < min) {
      System.err.println("Max (" + max + ") must be greater than or equal to" + 
      " Min (" + min + ")");
      return -1;
    }

    // Get the input from the form.
    String numCommentsString = request.getParameter("num-comments");

    // Convert the input to an int.
    int numComments;
    try {
      numComments = Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsString);
      return -1;
    }

    // Check that the input is between 0 and max.
    if (numComments < min || numComments > max) {
      System.err.println("Number of comments shown is out of range: " + 
      numCommentsString);
      return -1;
    }

    return numComments;
  }
}
