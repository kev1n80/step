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
import com.google.sps.utility.CommentConstants;
import com.google.sps.utility.ValidateInput;


/** 
 * Servlet that receives input from the blog select input, which creates the 
 * pagination for the blog comments section.
 */
@WebServlet("/pagination-comment")
public class PaginationCommentServlet extends HttpServlet {

  /** 
   * Determines the maximum pages needed for the pagination or how many groups * of comments are need for there to be n number of comments per page. n  
   * being the input selected by the user.
   *
   * @param request which contains data to retrieve comments
   * @param response
   * @return the maximum number of pages created in the form of json
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Receive input from the modify number of comments shown form
    int numComments;
    try {
      numComments = ValidateInput.getUserNum(request, "num-comments", 1, 
          CommentConstants.MAX_NUM_COMMENTS);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }    
    
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

    // Receive input from the pagination to see which comments to show
    FetchOptions entitiesLimit = FetchOptions.Builder.withLimit(30);
    double totalComments = results.countEntities(entitiesLimit);
    int maxPageNum = (int) Math.ceil(totalComments / numComments);

    String jsonMaxPageNum = new Gson().toJson(maxPageNum);
    response.setContentType("application/json;");
    response.getWriter().println(jsonMaxPageNum);
  }
}
