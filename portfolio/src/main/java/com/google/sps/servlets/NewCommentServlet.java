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
import com.google.sps.utility.ValidateInput;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {
    int minNameLen = 1;
    int maxNameLen = 50;
    int minCommentLen = 1;
    int maxCommentLen = 264;
    int minNumBlogs = 1;
    int maxNumBlogs = 5;
    ValidateInput validateInput = new ValidateInput();

    // Receive input from the create a comment form
    int blogNumber = validateInput.getUserNum(request, "blog-number", minNumBlogs, maxNumBlogs);
    if (blogNumber == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      1 + " to " + maxNumBlogs + ".");
      return;
    }

    String name = request.getParameter("name");
    int nameLen = name.length();
    if (nameLen < 1 || nameLen > maxNameLen) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter a string of length " +  
      1 + " to " + maxNameLen + ".");
      return;
    }

    String comment = request.getParameter("comment");
    int commentLen = comment.length();
    if (commentLen >= minCommentLen || commentLen <= maxCommentLen) {
      long timestamp = System.currentTimeMillis();

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      commentEntity.setProperty("blogNumber", blogNumber);
      commentEntity.setProperty("name", name);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    else {
      response.setContentType("text/html");
      response.getWriter().println("Please enter a comment with " + 
      minCommentLen + " to " + maxCommentLen + " characters.");
      return;
    }

    // Redirect back to the HTML page.
    String url = "/index.html#blog-post-" + blogNumber;
    response.sendRedirect(url);
  }
}
