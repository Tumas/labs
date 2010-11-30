window.onload = function (){
  document.getElementById("start").addEventListener('click', init, false);
};

var canvas = null;
var context2D = null;

var rotateAngle = Math.PI / 3;
var sin = Math.sin(rotateAngle);
var cos = Math.cos(rotateAngle);

/* pre-calculated probabilities */
var p1 = 0.3251;
var p2 = 0.1335;
var p3 = 0.2504;
var p4 = 0.2910; 

var p1p2 = p1 + p2;
var p1p2p3 = p1 + p2 + p3;

function init()
{
  canvas = document.getElementById('canvas');
  context2D = canvas.getContext('2d');

    setInterval( function() {
      for (var i = 0; i < 5000; i++)
        play(14);
      }, 

    1000 / 30);
}

function selectTransformation()
{
  var randomNumber = Math.random();

  if (randomNumber < p1) return 1;
  if (randomNumber >= p1 && randomNumber < p1p2) return 2;
  if (randomNumber >= p1p2 && randomNumber < p1p2p3) return 3;
  if (randomNumber >= p1p2p3) return 4;
}

function iy(y)
{
  return canvas.width - y;
}

function drawLine(x1, y1, x2, y2)
{
  context2D.beginPath();
  context2D.moveTo(x1, iy(y1));
  context2D.lineTo(x2, iy(y2));
  context2D.stroke();
  context2D.closePath();
}

function drawRect(p1, p2, p3, p4)
{
  drawLine(p1.x, p1.y, p2.x, p2.y);
  drawLine(p2.x, p2.y, p3.x, p3.y);
  drawLine(p3.x, p3.y, p4.x, p4.y);
  drawLine(p1.x, p1.y, p4.x, p4.y);
}

function w1p(point){
  return {
    x: 0.7344 * (cos * point.x - sin * point.y) + (0.635 * canvas.width),
    y: 0.7344 * (sin * point.x + cos * point.y)
  }
}

function w2p(point){
  return {
    x: 0.254 * point.x + (0.11 * canvas.width),
    y: 0.254 * point.y + (0.23 * canvas.height)
  }
}

function w3p(point){
  return {
    x: 0.254 * point.x + (0.63 * canvas.width),
    y: 0.254 * point.y + (0.23 * canvas.height)
  }
}

function w4p(point){
  return {
    x: 0.254 * point.x + (0.37 * canvas.width),
    y: 0.254 * point.y + (0.67 * canvas.height)
  }
}

function play(count)
{
  var init1 = {x: 0, y:0};
  var init2 = {x:canvas.width, y:0};
  var init3 = {x:canvas.width, y:canvas.height};
  var init4 = {x:0, y:canvas.height};

  for (var i = 0; i < count; i++){
    switch (selectTransformation()){
      case 1: 
        init1 = w1p(init1);
        init2 = w1p(init2);
        init3 = w1p(init3);
        init4 = w1p(init4);
        break;
      case 2:
        init1 = w2p(init1);
        init2 = w2p(init2);
        init3 = w2p(init3);
        init4 = w2p(init4);
        break;
      case 3:
        init1 = w3p(init1);
        init2 = w3p(init2);
        init3 = w3p(init3);
        init4 = w3p(init4);
        break;
      case 4:
        init1 = w4p(init1);
        init2 = w4p(init2);
        init3 = w4p(init3);
        init4 = w4p(init4);
        break;
    }
  }

  drawRect(init1, init2, init3, init4);
}
