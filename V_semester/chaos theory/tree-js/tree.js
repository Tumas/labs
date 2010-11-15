window.onload = function (){
  document.getElementById("start").addEventListener('click', init, false);
};

var canvas = null;
var context2D = null;

// Calculation data
var k1, k2, k3;
var angle1, angle2, angle3;
var sin1, sin2, sin3;
var cos1, cos2, cos3;

/*
 Cool patterns:
  k1 = k2 = k3 = 0.5
  a = c = 90 
  b = 180

  k1 = k3 = 0.5
  k2 = 0.6
  a = c = 90
  b = 180
*/

var iterationCount = 9;

function init()
{
  canvas = document.getElementById('canvas');
  context2D = canvas.getContext('2d');

  /* Input parsing */
  var form = document.getElementById('parameters');

  k1 = parseFloat(form.elements['k1'].value);
  k2 = parseFloat(form.elements['k2'].value);
  k3 = parseFloat(form.elements['k3'].value);

  angle1 = toRadians(parseInt(form.elements['a'].value));
  angle2 = toRadians(parseInt(form.elements['b'].value));
  angle3 = toRadians(360 - parseInt(form.elements['c'].value));

  sin1 = Math.sin(angle1);
  cos1 = Math.cos(angle1);
  sin2 = Math.sin(angle2);
  cos2 = Math.cos(angle2);
  sin3 = Math.sin(angle3);
  cos3 = Math.cos(angle3);
  /* end of input */

  var initiator = { 
    p1: { x:canvas.width/2, y:200 },
    p2: { x:canvas.width/2, y:0 }
  };

  context2D.clearRect(0, 0, canvas.width, canvas.height);

  drawLine(initiator);
  iterate(iterationCount, initiator);
}

function iterate(iteration, line)
{
  if (iteration == 0) return ;

  var line1 = w(line, sin1, cos1, k1);
  var line2 = w(line, sin2, cos2, k2);
  var line3 = w(line, sin3, cos3, k3);

  drawLine(line1);
  drawLine(line2);
  drawLine(line3);

  iterate(iteration - 1, line1);
  iterate(iteration - 1, line2);
  iterate(iteration - 1, line3);
}

function w(initiator, sin, cos, k)
{
  var temp = {
    p1: { x:0, y:0 }, 
    p2: { x:initiator.p2.x - initiator.p1.x, y:initiator.p2.y - initiator.p1.y }
  };

  temp.p2 = rotate(temp.p2, sin, cos, k);
  temp.p1 = rotate(temp.p1, sin, cos, k);

  return {
    p1: { x: temp.p2.x + initiator.p1.x, y: temp.p2.y + initiator.p1.y },
    p2: { x: initiator.p1.x, y: initiator.p1.y }
  };
}

function iy(y)
{
  return canvas.width - y;
}

function drawLine(line)
{
  context2D.beginPath();
  context2D.moveTo(line.p1.x, iy(line.p1.y));
  context2D.lineTo(line.p2.x, iy(line.p2.y));
  context2D.stroke();
  context2D.closePath();
}

function rotate(point, sin, cos, k)
{
  return {
    x: k * (cos * point.x - sin * point.y),
    y: k * (sin * point.x + cos * point.y)
  }
}

function toRadians(degrees)
{
  return degrees / 57.2958;
}
