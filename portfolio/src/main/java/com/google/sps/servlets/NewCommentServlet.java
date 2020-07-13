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


import java.util.Enumeration;
import java.util.*;
/** 
 * Servlet that handles blog comment forms
 */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  static final int MAX_COMMENT_LEN = 264;
  static final int MAX_NAME_LEN = 50; 

  /** 
   * Creates comment entities and stores them in the datastore
   *
   * @param request which contains data to create a new comment
   * @param response redirect user to home page
   */
  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {
    
    Enumeration<String> paramNames = request.getParameterNames();
    // for (String myVar : paramNames) {
    //   System.err.println(myVar);
    // }

    while(paramNames.hasMoreElements()) {
      System.err.println(paramNames.nextElement());
    }

    // Receive input from the create a comment form
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

    String content;
    try {
      content = ValidateInput.getUserString(request, "content", 1, 
          MAX_COMMENT_LEN);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
      return;
    }   

    String name;
    try {
      name = ValidateInput.getUserString(request, "name", 1, 
          MAX_NAME_LEN);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
      return;
    }       

    String imageURL;
    try {
      imageURL = ValidateInput.getUploadedFileUrl(request, "image");
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      System.err.println(errorMessage);

      String jsonErrorMessage = new Gson().toJson(errorMessage);
      response.setContentType("application/json;");
      response.getWriter().println(jsonErrorMessage);
      return;
    }     

    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("blogNumber", blogNumber);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("image", imageURL);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

    // return a message saying that this function call was successful
    response.setContentType("application/json;");
    response.getWriter().println(CommentConstants.SUCCESS);
  }
}
