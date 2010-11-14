window.onload = function (){
  document.getElementById("start").addEventListener('click', init, false);
};

var canvas = null;
var context2D = null;

// Calculation data
var rotateAngle = Math.PI / 4;
var sin = Math.sin(rotateAngle);
var cos = Math.cos(rotateAngle);

var iterationCount = 10;

function init()
{
  canvas = document.getElementById('canvas');
  context2D = canvas.getContext('2d');

  var init1 = {x: 0, y:0};
  var init2 = {x:canvas.width, y:0};
  var init3 = {x:canvas.width, y:canvas.height};
  var init4 = {x:0, y:canvas.height};

  iterate(iterationCount, init1, init2, init3, init4);
}

function iy(y)
{
  return canvas.width - y;
}

function iterate(count, p1, p2, p3, p4){
    var a1 = w1p(p1);
    var a2 = w1p(p2);
    var a3 = w1p(p3);
    var a4 = w1p(p4);

    var b1 = w2p(p1);
    var b2 = w2p(p2);
    var b3 = w2p(p3);
    var b4 = w2p(p4);

    var c1 = w3p(p1);
    var c2 = w3p(p2);
    var c3 = w3p(p3);
    var c4 = w3p(p4);

    if (count == 0){
      drawRect(a1, a2, a3, a4);
      drawRect(b1, b2, b3, b4);
      drawRect(c1, c2, c3, c4);
      return ;
    }

    iterate(count - 1, a1, a2, a3, a4);
    iterate(count - 1, b1, b2, b3, b4);
    iterate(count - 1, c1, c2, c3, c4);
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
    x: 0.5 * (sin * point.x - cos * point.y) + (0.355 * canvas.width), 
    y: 0.5 * (cos * point.x + sin * point.y)
  }
}

function w2p(point){
  return {
    x: 0.65 * point.x + (0.35 * canvas.width),
    y: 0.65 * point.y + (0.35 * canvas.height)
  }
}

function w3p(point){
  return {
    x: 0.3 * point.x + (0.7 * canvas.width),
    y: 0.35 * point.y
  }
}
