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

  console.log("Fetching comments for blog post " + blogNumber);
  fetch(queryString).then(response => response.json()).then((comments) => {
    // const commentListElement = document.getElementById('comment-container-' + 
    // blogNumber);
    const commentListElement = document.getElementById('comment-container-' + blogNumber);

    console.log("Printing comments for blog post " + blogNumber);
    commentListElement.innerHTML = '';
    if (comments.length > 0) {
      comments.forEach((comment) => {
        commentListElement.appendChild(
          createCommentElement(comment.content, comment.name));
      })
    }
    else {
      commentListElement.appendChild(
          createCommentElement("There are no comments", ""));
    }
  });
}

/** Creates a comment element */
function createCommentElement(content, name) {
  const divElement = createDivElement("comment", "");
  divElement.appendChild(createPElement(name))
  divElement.appendChild(createPElement(content));
  return divElement;
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Creates a <p> element containing text. */
function createPElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

/** Deletes all comments. */
function deleteAllComments(blogNumber) {
  console.log("Deleting all comments");
  var queryString = '/delete-comment?blog-number=' + blogNumber;
  fetch(queryString, {method: 'POST'}).then(() => {
    loadCommentSection(blogNumber);
    });
}

/** Creates pagination to go through all comments. */
function loadCommentPagination(numComments, blogNumber) {
  var queryString = '/pagination-comment?num-comments=' + numComments;
  queryString = queryString + '&blog-number=' + blogNumber;

  console.log("Fetching pagination for blog post " + blogNumber);
  fetch(queryString).then(response => response.json()).then((maxPageNum) => {
    const paginationElement = document.getElementById('comment-pagination-' + blogNumber);
    console.log("Loading pagination w/ " + numComments + " comments per page");
    paginationElement.innerHTML = '';
      for (var i = 1; i < maxPageNum + 1; i++) {
        paginationElement.appendChild(
          createPageElement(i, numComments, blogNumber));
      }
  });
}

/** Creates an elemet that represents a page. */
function createPageElement(pageNumber, numComments, blogNumber) {
  console.log("Creating page number " + pageNumber + " for blog post " + 
  blogNumber);
  const pageElement = document.createElement('a');
  pageElement.innerText = pageNumber;
  pageElement.addEventListener('click', () => {
    console.log("Loading comments for page: " + pageNumber);
    getComment(numComments, pageNumber, blogNumber);
  });
  return pageElement;
}

/** Loads pagination and comments. */
function loadCommentSection(blogNumber) {
  var numComments = document.getElementById("num-comments-" + blogNumber).value;
  getComment(numComments, 1, blogNumber);
  loadCommentPagination(numComments, blogNumber);
}

/** Creates a <div> element containing class and id attribute. */
function createDivElement(classAttribute, idAttribute) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", classAttribute);
  divElement.setAttribute("id", idAttribute);
  return divElement;
}

/** Creates a <label> element containing text and for and form attribute. */
function createLabelElement(forAttribute, text) {
  const labelElement = document.createElement('label');
  labelElement.innerText = text;
  labelElement.setAttribute("for", forAttribute);
  return labelElement;
}

/** Creates a <select> element containing a name, onchange, and id attrubute. */
function createSelectElement(nameAttribute, onchangeAttribute, id) {
  const selectElement = document.createElement('select');
  selectElement.setAttribute("name", nameAttribute);
  selectElement.setAttribute("onchange", onchangeAttribute);
  selectElement.setAttribute("id", id);
  return selectElement;
}

/** Creates an <option> element containing a value and text. */
function createOptionElement(valueAttribute, text) {
  const optionElement = document.createElement('option');
  optionElement.innerText = text;
  optionElement.setAttribute("value", valueAttribute);
  return optionElement;
}

/** Creates a select for the user to choose the number of comments they want to 
be displayed per page. */
function createCommentSelect(blogNumber, numComments, defaultValue) {
  console.log("Creating comment select")
  const divElement = createDivElement("blog-select-comments-num", "");

  const selectName = "num-comments";
  const selectId = selectName + "-" + blogNumber;

  const selectDescription = "Number of Comments Displayed:";
  divElement.appendChild(createLabelElement(selectId, selectDescription));
  divElement.appendChild(document.createElement("BR"));


  const onchange = "loadCommentSection('" + blogNumber + "')";
  const selectElement = createSelectElement(selectName, onchange, selectId);
  divElement.appendChild(selectElement);

  const optionMessage = "Select a value";
  const defaultOptionElement = createOptionElement(defaultValue, optionMessage);
  defaultOptionElement.setAttribute("selected", true);
  defaultOptionElement.setAttribute("disabled", true);
  defaultOptionElement.setAttribute("hidden", true);
  selectElement.appendChild(defaultOptionElement);

  for (var i = 1; i < numComments + 1; i++) {
      selectElement.appendChild(
        createOptionElement(i, i));
  }

  return divElement;
}

/** Creates a <form> element containing an action and a method. */
function createFormElement(actionAttribute, methodAttribute, idAttribute) {
  const formElement = document.createElement('form');
  formElement.setAttribute("action", actionAttribute);
  formElement.setAttribute("method", methodAttribute);
  formElement.setAttribute("id", idAttribute);
  return formElement;
}

/** Creates an <input> text element containing a type, name and maxlength 
attribute. */
function createInputTextElement(nameAttribute, maxLengthAttribute) {
  const inputElement = document.createElement('input');
  inputElement.setAttribute("type", "text");
  inputElement.setAttribute("name", nameAttribute);
  inputElement.setAttribute("maxLength", maxLengthAttribute);
  return inputElement;
}

/** Creates an <input> submit element containing a type. */
function createInputSubmitElement() {
  const inputElement = document.createElement('input');
  inputElement.setAttribute("type", "submit");
  return inputElement;
}

/** Creates a form to create a comment */
function createCommentForm(blogNumber) {
  console.log("Creating comment form");
  const formAction = "/new-comment?blog-number=" + blogNumber;
  const formId = "blog-" + blogNumber + "-form";
  const formElement = createFormElement(formAction, "POST", formId);

  const nameDescription = "Enter you name which can be up to 30 characters!";
  formElement.appendChild(createLabelElement(formId, nameDescription));
  formElement.appendChild(document.createElement("BR"));

  formElement.appendChild(createInputTextElement("name", "30"));
  formElement.appendChild(document.createElement("BR"));

  const commentDescription = "Enter a comment which can be up to 264 characters!";
  formElement.appendChild(createLabelElement(formId, commentDescription));
  formElement.appendChild(document.createElement("BR"));

  formElement.appendChild(createInputTextElement("comment", "264"));
  formElement.appendChild(document.createElement("BR"));

  formElement.appendChild(createInputSubmitElement());
  formElement.appendChild(document.createElement("BR"));
  
  return formElement;
}

/** Creates a <button> submit element containing a type and onclick attribute 
and text. */
function createButtonElement(typeAttribute, onclickAttribute, text) {
  const buttonElement = document.createElement('button');
  buttonElement.innerText = text;
  buttonElement.setAttribute("type", typeAttribute);
  buttonElement.setAttribute("onclick", onclickAttribute);
  return buttonElement;
}


/** Creates a comments section. */
function createCommentSection(blogNumber) {
  const commentSection = document.getElementById('comment-section-' + blogNumber);

  const numComments = 5;
  const defaultValue = 3;
  commentSection.appendChild(
    createCommentSelect(blogNumber, numComments, defaultValue));

  const commentContainerId = "comment-container-" + blogNumber;
  commentSection.appendChild(createDivElement("comment-container", commentContainerId));

  commentSection.appendChild(createCommentForm(blogNumber));

  const commentPaginationId = "comment-pagination-" + blogNumber;
  commentSection.appendChild(createDivElement("pagination", commentPaginationId));

  const deleteButtonOnclick = "deleteAllComments('" + blogNumber + "')";
  const deleteButtonDescription = "Delete All Comments";
  commentSection.appendChild(
    createButtonElement("button", deleteButtonOnclick, deleteButtonDescription));
}

/** Loads blog post comment section */
function loadBlogpostComment(numberOfBlogs) {
  for (var i = 1; i < numberOfBlogs + 1; i++) {
    console.log("Creating comment section for blog post " + i);
    createCommentSection(i);
  }
}

/** Toggles the blog post comment secion */
function toggleBlogpostComment(blogNumber) {
  var commentSection = document.getElementById("comment-section-" + blogNumber);
  if (commentSection.style.display === "inline-flex") {
    console.log("Comment section " + blogNumber + " is now hidden.")
    commentSection.style.display = "none";
  } else {
    console.log("Comment section " + blogNumber + " is now visible.")
    commentSection.style.display = "inline-flex";
    loadCommentSection(blogNumber);
  }
}
