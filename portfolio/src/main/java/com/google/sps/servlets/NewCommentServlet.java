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

/** 
 * Servlet that creates comment entities and redirects the user back to the 
 * blog section of the portfolio page.
 *
 * @param request which contains data to create a new comment
 * @param response
 */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  static final int MIN_COMMENT_LEN = 1;
  static final int MAX_COMMENT_LEN = 264;
  static final int MIN_NUM_BLOGS = 1;
  static final int MAX_NUM_BLOGS = 5;

  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {

    // Receive input from the create a comment form
    int blogNumber = getUserNum(request, "blog-number", MIN_NUM_BLOGS,        MAX_NUM_BLOGS);

    String comment = request.getParameter("comment");
    int commentLen = comment.length();
    if (commentLen >= MIN_COMMENT_LEN || commentLen <= MAX_COMMENT_LEN) {
      long timestamp = System.currentTimeMillis();

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      commentEntity.setProperty("blogNumber", blogNumber);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    else {
      response.setContentType("text/html");
      response.getWriter().println("Please enter a comment with " + 
          MIN_COMMENT_LEN + " to " + MAX_COMMENT_LEN + " characters.");
      return;
    }

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /** 
   * Returns the number of comments shown entered by the user, or -1 if the 
   * comment was invalid. Min must be greater than -1 and Max must be greater 
   * than or equal to min 
   *
   * @param request the request received from the form that contains user input
   * @param parameter the name of the input parameter one is retreiving
   * @param min used to establish the lower bound of the input
   * @param max used to establish the upper bound of the input
   * @return the user's input (number) or -1 if it does not follow guidelines
   */
  private int getUserNum(HttpServletRequest request, String parameter, int min, int max) {
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
    String userNumString = request.getParameter(parameter);

    // Convert the input to an int.
    int userNum;
    try {
      userNum = Integer.parseInt(userNumString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + userNumString);
      return -1;
    }

    // Check that the input is between 0 and max.
    if (userNum < min || userNum > max) {
      System.err.println("Value for " + parameter + " is out of range (" + min 
          + " - " + max + "): " + userNumString);
      return -1;
    }

    return userNum;
  }
}
