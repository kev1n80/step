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
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


/** Servlet responsible for deleting tasks. */
@WebServlet("/pagination-comment")
public class PaginationCommentServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Receive input from the modify number of comments shown form
    int maxNumComments = 5;
    int numComments = getUserNum(request, "num-comments", 1, maxNumComments);
    if (numComments == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      1 + " to " + maxNumComments + ".");
      return;
    }
    
    // Receive input on which blog we are retrieving comments from
    int maxNumBlogs = 5;
    int blogNumber = getUserNum(request, "blog-number", 1, maxNumBlogs);
    if (blogNumber == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between " +  
      1 + " to " + maxNumBlogs + ".");
      return;
    }

    // Retrieve Comments from Datastore for the given blog post
    FilterPredicate filterBlogComments = new FilterPredicate("blogNumber", 
    FilterOperator.EQUAL, blogNumber);
    Query query = new Query("Comment").setFilter(filterBlogComments);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Receive input from the pagination to see which comments to show
    FetchOptions entitiesLimit = FetchOptions.Builder.withLimit(30);
    double totalComments = results.countEntities(entitiesLimit);
    int maxPageNum = (int) Math.ceil(totalComments / numComments);

    String jsonMaxPageNum = new Gson().toJson(maxPageNum);
    response.setContentType("application/json;");
    response.getWriter().println(jsonMaxPageNum);
  }

  /** Returns the number of comments shown entered by the user, or -1 if the 
  comment was invalid. Min must be greater than -1 and Max must be greater than 
  or equal to min */
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
