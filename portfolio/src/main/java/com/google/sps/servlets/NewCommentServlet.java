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
import com.google.sps.utility.CommentConstants;
import com.google.sps.utility.ValidateInput;

/** 
 * Servlet that handles blog comment forms
 */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  static final int MAX_COMMENT_LEN = 264;

  /** 
   * Creates comment entities and stores them in the datastore
   *
   * @param request which contains data to create a new comment
   * @param response redirect user to home page
   */
  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {
    ValidateInput validateInput = new ValidateInput();

    // Receive input from the create a comment form
    int blogNumber;
    try {
      blogNumber = validateInput.getUserNum(request, "blog-number", 1, 
          CommentConstants.MAX_NUM_BLOGS);
    } catch (Exception e) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 1 to " + 
          CommentConstants.MAX_NUM_BLOGS + ".");
      return;
    }    

    String comment;

    try {
      comment = validateInput.getUserString(request, "comment", 1, 
          MAX_COMMENT_LEN);
    } catch (Exception e) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 1 to " + 
          MAX_COMMENT_LEN + ".");
      return;
    }    

    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("blogNumber", blogNumber);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }
}
