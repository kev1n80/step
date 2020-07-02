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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function addRandomFact() {
  const facts =
    ['I love to dance!', 'I am a lefty', 'I use to fence foil', 
    'I am Peruvian!', 'I reached Plat in SC2', 
    'I have never left the Americas', 
    'My quarantine hobby is photography'];

  // Pick a random greeting.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/** Retrieves a number of comments after and including the first comment shown. */
function getComment(numComments, pageNumber, blogNumber) {
  var queryString = '/list-comments?num-comments=' + numComments;
  queryString = queryString + '&page-number=' + pageNumber;
  queryString = queryString + '&blog-number=' + blogNumber;

  console.log("Retrieving comments");
  fetch(queryString).then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-container');

    console.log("Printing comments")
    commentListElement.innerHTML = '';
    if (comments.length > 0) {
      comments.forEach((comment) => {
        commentListElement.appendChild(
          createListElement(comment.content));
      })
    }
    else {
      commentListElement.appendChild(
          createListElement("There are no comments"));
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Deletes all comments. */
function deleteAllComments(blogNumber) {
  console.log("Deleting all comments");
  var queryString = '/delete-comment?blog-number=' + blogNumber;
  fetch(queryString, {method: 'POST'}).then(() => {
    loadCommentsSection(blogNumber);
    });
}

/** Creates pagination to go through all comments. */
function loadPagination(numComments, blogNumber) {
  var queryString = '/pagination-comment?num-comments=' + numComments;
  queryString = queryString + '&blog-number=' + blogNumber;

  console.log("Fetching comments");
  fetch(queryString).then(response => response.json()).then((maxPageNum) => {
    const paginationElement = document.getElementById('pagination');
    console.log("Loading pagination");
    paginationElement.innerHTML = '';
      for (var i = 1; i < maxPageNum + 1; i++) {
        paginationElement.appendChild(
          createPageElement(i, numComments, blogNumber));
      }
  });
}

/** Creates an elemet that represents a page */
function createPageElement(pageNumber, numComments, blogNumber) {
  console.log("Creating page number " + pageNumber);
  const pageElement = document.createElement('a');
  pageElement.innerText = pageNumber;
  pageElement.addEventListener('click', () => {
    console.log("Loading comments for page: " + pageNumber);
    getComment(numComments, pageNumber, blogNumber);
  });
  return pageElement;
}

/** Loads pagination and comments. */
function loadCommentsSection(blogNumber) {
  var numComments = document.getElementById("num-comments").value;
  getComment(numComments, 1, blogNumber);
  loadPagination(numComments, blogNumber);
}
