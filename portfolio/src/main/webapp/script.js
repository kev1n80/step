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

/** 
 * Retrieves a number of comments after and including the first comment shown. 
 *
 * @param numComments the number of comments to display
 * @param pageNumber the page number the user clicked 
 * @param blogNumber the blog the comments are assoicated with
 */
function getComment(numComments, pageNumber, blogNumber) {
  let queryString = '/list-comments?num-comments=' + numComments;
  queryString = queryString + '&page-number=' + pageNumber;
  queryString = queryString + '&blog-number=' + blogNumber;

  console.log("Fetching comments for blog post " + blogNumber);
  fetch(queryString).then(response => response.json()).then((comments) => {
    // if it returns a string that what is returned is an error message
    if (typeof comments == "string") {
      console.log(comments);
      window.alert(comments);
    } else {
      const commentListElement = document.getElementById('comment-container-' + 
          blogNumber);

      console.log("Printing comments for blog post " + blogNumber);
      commentListElement.innerHTML = '';
      commentListElement.appendChild(createHElement("Comments", 4));
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
    }

    
  });
}

/** 
 * Creates a comment element 
 *
 * @param content the text the user commented
 * @param name the name of the user who commented
 * @return returns a div that represents a comment
 */
function createCommentElement(content, name) {
  const divElement = createDivElement("comment", "");
  divElement.appendChild(createHElement(name, 5));
  divElement.appendChild(createPElement(content));
  return divElement;
}

/** 
 * Creates an <li> element containing text. 
 *
 * @param text the text that will be displayed
 * @return returns an li element with the text
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** 
 * Creates a <p> element containing text. 
 *
 * @param text the text that will be displated
 * @return returns a p element with the text
 */
function createPElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

/** 
 * Creates a <h> element with rank and containing text. 
 * 
 * @param text the text that will be displayed
 * @param rank the rank of the header
 * @return returns an h element with a rank and text
 */
function createHElement(text, rank) {
  const hElement = document.createElement('h' + rank);
  hElement.innerText = text;
  return hElement;
}

/** 
 * Deletes all comments. 
 *
 * @param blogNumber the blog whose comments will be deleted
 */
function deleteAllComments(blogNumber) {
  console.log("Deleting all comments");
  const queryString = '/delete-comment?blog-number=' + blogNumber;
  fetch(queryString, {method: 'POST'}).then(response => response.json())
      .then((response) => {
        if (response == "success") {
          loadCommentSection(blogNumber);
        } else {
          // if it doesn't return a success then it is an error
          console.log(response);
          window.alert(response);
        }
      });
}

/** 
 * Creates pagination to go through all comments. 
 * 
 * @param numComments the number of comments displayed per page
 * @param blogNumber the blog this func is creating a pagination for
 */
function loadCommentPagination(numComments, blogNumber) {
  let queryString = '/pagination-comment?num-comments=' + numComments;
  queryString = queryString + '&blog-number=' + blogNumber;

  console.log("Fetching pagination for blog post " + blogNumber);
  fetch(queryString).then(response => response.json()).then((maxPageNum) => {
    if (typeof maxPageNum == "number") {
      const paginationElement = document.getElementById('comment-pagination-' + 
          blogNumber);
      console.log("Loading pagination w/ " + numComments + " comments per page");
      paginationElement.innerHTML = '';
      for (let i = 1; i < maxPageNum + 1; i++) {
        paginationElement.appendChild(
            createPageElement(i, numComments, blogNumber));
      }
    } else {
      // return a message saying that this function call was successful
      response.setContentType("application/json;");
      response.getWriter().println(maxPageNum);
    }


  });
}

/** 
 * Creates an elemet that represents a page. 
 *
 * @param pageNumber the page number that this element represents
 * @param numComments the number of comments displayed per page
 * @param blogNumber the blog that this page element is relateed to 
 * @return returns an <a> element that represents a page number in the 
 * pagination
 */
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

/**
 * Loads pagination and comments. 
 *
 * @param blogNumber the blog this func is loading the pagination and
 * comments for
 */
function loadCommentSection(blogNumber) {
  const numComments = document.getElementById("num-comments-" + blogNumber).value;
  getComment(numComments, 1, blogNumber);
  loadCommentPagination(numComments, blogNumber);
}

/** 
 * Creates a <div> element containing class and id attribute. 
 *
 * @param classAttribute the name of the class of this div element
 * @param idAttribute the name of the id of this div element
 * @return returns a div element with a class and id
 */
function createDivElement(classAttribute, idAttribute) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", classAttribute);
  divElement.setAttribute("id", idAttribute);
  return divElement;
}

/** 
 * Creates a <label> element containing text and for and form attribute. 
 * @param forAttribute the for attribute of this label
 * @param text the text displayed
 * @param classAttribute the name of the class of this div element
 * @return returns a label element with text, a class, and an id
 */
function createLabelElement(forAttribute, text, classAttribute) {
  const labelElement = document.createElement('label');
  labelElement.innerText = text;
  labelElement.setAttribute("for", forAttribute);
  labelElement.setAttribute("class", classAttribute);
  return labelElement;
}

/** 
 * Creates a <select> element containing a name, onchange, and id attrubute. 
 *
 * @param nameAttribute the name attribute of a select element
 * @param onchangeAttribute the js function to execute when the select changes
 * @param idAttribute the name of the id of this div element
 * @return returns a select element
 */
function createSelectElement(nameAttribute, onchangeAttribute, id) {
  const selectElement = document.createElement('select');
  selectElement.setAttribute("name", nameAttribute);
  selectElement.setAttribute("onchange", onchangeAttribute);
  selectElement.setAttribute("id", id);
  return selectElement;
}

/** 
 * Creates an <option> element containing a value and text. 
 *
 * @param valueAttribute the value that this option returns when chosen
 * @param text the text that will be displayed
 * @return an option element
 */
function createOptionElement(valueAttribute, text) {
  const optionElement = document.createElement('option');
  optionElement.innerText = text;
  optionElement.setAttribute("value", valueAttribute);
  return optionElement;
}

/** 
 * Creates a select for the user to choose the number of comments they want to 
 * be displayed per page. 
 *
 * @param blogNumber the blog that this page element is relateed to 
 * @param numComments the number of comments displayed per page
 * @param defaultValue the default value that this select element will have
 * @return returns a div element that contains a select element
 */
function createCommentSelect(blogNumber, numComments, defaultValue) {
  console.log("Creating comment select")
  const divElement = createDivElement("blog-select-comments-num", "");

  const selectName = "num-comments";
  const selectId = selectName + "-" + blogNumber;

  const selectDescription = "Number of Comments Displayed:";
  divElement.appendChild(createLabelElement(selectId, selectDescription, ""));
  divElement.appendChild(document.createElement("BR"));

  const onchange = "loadCommentSection('" + blogNumber + "')";
  const selectElement = createSelectElement(selectName, onchange, selectId);
  divElement.appendChild(selectElement);

  const optionMessage = "Select a value";
  const defaultOptionElement = createOptionElement(defaultValue, optionMessage);
  selectElement.appendChild(defaultOptionElement);
  defaultOptionElement.setAttribute("selected", true);
  defaultOptionElement.setAttribute("disabled", true);
  defaultOptionElement.setAttribute("hidden", true);

  for (var i = 1; i < numComments + 1; i++) {
      selectElement.appendChild(
          createOptionElement(i, i));
  }

  return divElement;
}

/** 
 * Creates a <form> element containing an action and a method. 
 *
 * @param actionAttribute the servlet that this form will send input to
 * @param methodAttribute the method attribute of the form element
 * @param classAttribute the name of the class of this div element
 * @param idAttribute the name of the id of this div element
 * @return returns a form element
 */
function createFormElement(actionAttribute, methodAttribute, classAttribute, 
      idAttribute) {
  const formElement = document.createElement('form');
  formElement.setAttribute("action", actionAttribute);
  formElement.setAttribute("method", methodAttribute);
  formElement.setAttribute("class", classAttribute);
  formElement.setAttribute("id", idAttribute);
  return formElement;
}

/** 
 * Creates an <input> text element containing a type, name and maxlength 
 * attribute. 
 * 
 * @param nameAttribute the name attribute for this input element
 * @param minLengthAttribute the minimum length of text accepted
 * @param maxLengthAttribute the maximum length of text accepted
 * @param placeholderAttribute the text that will be displayed before the user  * inputs text
 * @param classAttribute the name of the class of this div element
 * @param idAttribute the name of the id of this div element
 * @return return an input element of type text
 */
function createInputTextElement(nameAttribute, minLengthAttribute,
      maxLengthAttribute, placeholderAttribute, classAttribute, idAttribute) {
  const inputElement = document.createElement('input');
  inputElement.setAttribute("type", "text");
  inputElement.setAttribute("name", nameAttribute);
  inputElement.setAttribute("minLength", minLengthAttribute);
  inputElement.setAttribute("maxLength", maxLengthAttribute);
  inputElement.setAttribute("placeholder", placeholderAttribute);
  inputElement.setAttribute("class", classAttribute);
  inputElement.setAttribute("id", idAttribute);
  return inputElement;
}

/** 
 * Creates an <input> submit element containing a type. 
 *
 * @param classAttribute the name of the class of this div element
 * @return return an input element of type submit
 */
function createInputSubmitElement(classAttribute) {
  const inputElement = document.createElement('input');
  inputElement.setAttribute("type", "submit");
  inputElement.setAttribute("class", classAttribute);
  return inputElement;
}

/** 
 * Creates a form to create a comment 
 *
 * @param blogNumber the blog that this form element is related to 
 * @return a form element that is related to a blog and creates a comment
 */
function createCommentForm(blogNumber) {
  console.log("Creating comment form");

  const formClass = "blog-form";
  const formId = "blog-" + blogNumber + "-form";
  const formElement = createDivElement(formClass, formId);

  const formDescription = "Add a comment!";
  formElement.appendChild(createLabelElement(formId, formDescription, 
      "blog-form-label"));

  const nameDescription = "Name:";
  const nameInputClass = "blog-form-input";
  const nameInputId = "blog-" + blogNumber + "-form-name";
  formElement.appendChild(createLabelElement(nameInputId, nameDescription, ""));

  const namePlaceholder = "Enter name (char limit 50)";
  const nameInputElement = createInputTextElement("name", "1", "50", 
      namePlaceholder, nameInputClass, nameInputId);
  formElement.appendChild(nameInputElement);

  const contentDescription = "Comment:";
  const contentInputClass = "blog-form-content";
  const contentInputId = "blog-" + blogNumber + "-content-name";
  formElement.appendChild(createLabelElement(contentInputId, contentDescription,
      ""));

  const contentPlaceholder = "Enter a comment (char limit 264)";
  formElement.appendChild(createInputTextElement("comment", "1", "264", 
      contentPlaceholder, contentInputClass, contentInputId));

  const submitButtonElement = createInputSubmitElement("blog-form-submit");
  submitButtonElement.onclick = function() {sendFormData(blogNumber, 
      nameInputId, contentInputId)};
  formElement.appendChild(submitButtonElement);

  return formElement;
}

/** 
 * Sends name and content input data, and it clears the input elements
 * 
 * @param blogNumber the blog associated with the input
 * @param nameInputId the id of the input element with the name data
 * @param contentInputId the id of the input element with the content data
 */
function sendFormData(blogNumber, nameInputId, contentInputId) {
  let url = "/new-comment?blog-number=" + blogNumber;
    
  const nameInputElement = document.getElementById(nameInputId);
  url = url + "&name=" + nameInputElement.value;
  const contentInputElement = document.getElementById(contentInputId);
  url = url + "&content=" + contentInputElement.value;

  console.log(url);
  console.log("sending form data");
  fetch(url, {method: 'POST'}).then(response => response.json())
      .then(() => {
        if (response == "success") {
          loadCommentSection(blogNumber);
          nameInputElement.value = '';
          contentInputElement.value = '';          
        } else {
          // if it doesn't return a success then it is an error
          console.log(response);
          window.alert(response);
        }
      });
}

/** 
 * Creates a <button> submit element containing a type and onclick attribute 
 * and text. 
 *
 * @param typeAttribute the type of button this is
 * @param onclickAttribute the js that is executed when this button is clicked
 * @param text the text that is displated
 * @param classAttribute the name of the class of this div element
 * @return a button element
 */
function createButtonElement(typeAttribute, onclickAttribute, text,
    classAttribute) {
  const buttonElement = document.createElement('button');
  buttonElement.innerText = text;
  buttonElement.setAttribute("type", typeAttribute);
  buttonElement.setAttribute("onclick", onclickAttribute);
  buttonElement.setAttribute("class", classAttribute);
  return buttonElement;
}


/** 
 * Creates a comments section. 
 *
 * @param blogNumber the blog this comment section is related to
 */
function createCommentSection(blogNumber) {
  const commentSection = document.getElementById('comment-section-' + blogNumber);

  const numComments = 5;
  const defaultValue = 3;
  commentSection.appendChild(
      createCommentSelect(blogNumber, numComments, defaultValue));

  const commentContainerId = "comment-container-" + blogNumber;
  commentSection.appendChild(createDivElement("comment-container", 
      commentContainerId));

  commentSection.appendChild(createCommentForm(blogNumber));

  const commentPaginationId = "comment-pagination-" + blogNumber;
  commentSection.appendChild(createDivElement("pagination", commentPaginationId));

  const deleteButtonOnclick = "deleteAllComments('" + blogNumber + "')";
  const deleteButtonDescription = "Delete All Comments";
  const deleteButtonClass = "delete-comment-button";
  commentSection.appendChild(
      createButtonElement("button", deleteButtonOnclick, 
      deleteButtonDescription, deleteButtonClass));
}

/** 
 * Loads blog post comment section. 
 * 
 * @param numberOfBlogs the number of blogs to create a comment section for 
 */
function loadBlogpostComment(numberOfBlogs) {
  for (var i = 1; i < numberOfBlogs + 1; i++) {
    console.log("Creating comment section for blog post " + i);
    createCommentSection(i);
  }
}

/** 
 * Toggles the blog post comment section. 
 * 
 * @param numberOfBlogs the number of blogs to create a comment section for 
 */
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
