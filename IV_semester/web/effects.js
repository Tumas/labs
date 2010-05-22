window.onload=function(){

  var userName = document.getElementById('name');
  var userMail = document.getElementById('e-mail');
  var userBirthdayDate = document.getElementById('birthday')
  var userTelephoneNumber = document.getElementById('telephone');
  var albumNumber = document.getElementById('album-number');
  var userComment = document.getElementById('comment');

  userName.onblur = function(){
    var label = document.getElementById('namelabel');
    changeColorOnValidation(isPresented, this, label);
  };

  userMail.onblur = function(){
    var label = document.getElementById('e-maillabel');
    changeColorOnValidation(isValidEmail, this, label);
  };

  userBirthdayDate.onblur = function(){
    var label = document.getElementById('birthdaylabel');
    changeColorOnValidation(isValidDate, this, label);
  };

  userTelephoneNumber.onblur = function(){
    var label = document.getElementById('telephonelabel');
    changeColorOnValidation(isValidTelephone, this, label);
  };

  albumNumber.onblur = function(){
    var label = document.getElementById('album-numberlabel')d{2}/(\d{2};
    changeColorOnValidation(isValidAlbumNumber, this, label);
  };

  var button = document.getElementById('comment-button');
  button.onclick = function(){
    var label = document.getElementById('commentlabel');
    changeColorOnValidation(isPresented, userComment, label);

    if (!isPresented(userName)) {
      alert("Please enter your name");
      return false;
    }
    else if (!isValidEmail(userMail)) {
      alert("Please correct email");
      return false;
    }
    else if (!isValidDate(userBirthdayDate)){
      alert("Please enter your birthday correctly");
      return false;
    }
    else if (!isValidTelephone(userTelephoneNumber)){
      alert("Please enter your teleophone number");
      return false;
    }
    else if (!isValidAlbumNumber(albumNumber)){
      alert("Please enter correct album number");
      return false;
    }
    else if (!isPresented(userComment)){
      alert("Please enter your comment");
      return false;
    }

    var commentedAlbum = getAlbumElement(albumNumber.value);
    var newComment = document.createElement('div');

    if (getNumberOfComments(commentedAlbum) % 2 == 0){
      newComment.setAttribute('class', 'comment odd');
    } else {
      newComment.setAttribute('class', 'comment even');
    }

    address = isValidEmail(userMail); 
    newComment.innerHTML = '<div class="controls"><span class="remove-comment">Remove </span></div><div class="comentator-meta"><span class="host bold">' + address[1] + '</span> at <span class="domain bold">' + address[2] + '</span> dot <span class="domain-suffix bold">' + address[3] + '</span></div><div class="comment-text">' + userComment.value + '</div>';
    commentedAlbum.appendChild(newComment);
  };
}

function changeColorOnValidation(validationFunction, elementToValidate, elementToChangeColor){
  if (validationFunction(elementToValidate)){
    elementToChangeColor.style.color = "green";
  } else {
    elementToChangeColor.style.color = "red";
  }
}

//Validation functions 
function isValidEmail(textField){
  var results = /^(\w+)@(\w+).(\w+)$/.exec(trim(textField.value));
  return results;
}

function isValidDate(textField){
  var dateObj = false, timeStamp = Date.parse(textField.value);
  if (timeStamp) {
    dateObj = new Date(timeStamp);
  }
  return dateObj;
}

//supported formats: 863140982, +370 63140982
function isValidTelephone(textField){
  var number = trim(textField.value);
  return /^\d{9}$/.test(number) || /^\+?\d{3}\s?\d{8}$/.test(number) 
}

function isPresented(textField){
  if (trim(textField.value).length == 0) 
    return false;
  else 
    return true;
}

function isValidAlbumNumber(textField){
  var number = parseInt(textField.value);
  return number == textField.value && number <= getAlbumCount() && number > 0
} 

//helper functions
function trim(text){
  return text.replace(/^s+|s+$/, "");
}

function getAlbumCount(){
  var sum = 0;
  candidates = document.getElementsByTagName('div');
  for (var i = 0; i < candidates.length; i++){
    if (candidates[i].getAttribute('class') == 'album'){
      sum++;
    }
  }
  return sum;
}

function getAlbumElement(number){
  if (getAlbumCount() < number){
    return false;
  }

  var sum = 0;
  var divs = document.getElementsByTagName('div');
  for (var i = 0; i < candidates.length; i++){
    if (candidates[i].getAttribute('class') == 'album'){
      sum++;
      if (sum == number)
        return candidates[i];
    }
  }
}

function getNumberOfComments(albumElement){
  var sum = 0;
  var candidates = albumElement.getElementsByTagName('div');
  for (var i = 0; i < candidates.length; i++){
    if (candidates[i].getAttribute('class') == 'comment odd' || candidates[i].getAttribute('class') == 'comment even'){
      sum++;
    }
  }
  return sum;
}
