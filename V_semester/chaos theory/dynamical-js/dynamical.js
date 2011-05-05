window.onload = function (){

  /* 0.4 and 6 */
  var ffunk = function(c, x) { 
    return c * x*x* (1 - x); 
    //return Math.sin(x) * c;
  };

  var plot = null;
  var setPixel = function(imageData, x, y, r, g, b, a){
    index = (x + y * imageData.width) * 4;

    imageData.data[index] = r;
    imageData.data[index + 1] = g;
    imageData.data[index + 2] = b;
    imageData.data[index + 3] = a;
  };

  document.getElementById("timeline-button").addEventListener('click', function(){
    var p = getFormParams();
    
    plot = new Plot({
      canvas: document.getElementById('canvas'),
      maxX: p.maxX,
      minX: p.minX,
      maxY: p.maxY,
      minY: p.minY,
    });

    plot.ctx.clearRect(0, 0, plot.canvas.width, plot.canvas.height);
    plot.grid(15);

    plot.hAxis({step:p.stepX, offset:0});
    plot.vAxis({step:p.stepY, offset:0});

    var iterations = 100;
    var x = p.x;
    var tempStyle = plot.ctx.strokeStyle;

    plot.ctx.strokeStyle = "red";
    plot.ctx.beginPath();
    plot.ctx.moveTo(plot.rtoax(0), plot.iY(plot.rtoay(x)));
    x = ffunk(p.c, x);

    for (var i = 1; i < iterations;  i++){
      try {
        plot.ctx.lineTo(plot.rtoax(i), plot.iY(plot.rtoay(x)));  
        x = ffunk(p.c, x);
      }
      catch (e){
        plot.ctx.stroke();
        alert("abnormal x, cannot continue: " + x);

        break;
      }
    }

    plot.ctx.stroke();
    plot.ctx.strokeStyle = tempStyle;
  }, false);

  document.getElementById("iterate-button").addEventListener('click', function(){
    var p = getFormParams();
    
    plot = new Plot({
      canvas: document.getElementById('canvas'),
      maxX: p.maxX,
      minX: p.minX,
      maxY: p.maxY,
      minY: p.minY,
    });

    plot.ctx.clearRect(0, 0, plot.canvas.width, plot.canvas.height);
    plot.grid(15);

    plot.hAxis({step:p.stepX, offset:0});
    plot.vAxis({step:p.stepY, offset:0});

    // y = x
    plot.draw({step: 1, funk: function(x){ return x;}, color: "blue"});
    plot.draw({step: 0.01, funk: function(x){ return ffunk(p.c, x); }, color: "red" });

    var iterations = 100;
    var tempStyle = plot.ctx.strokeStyle;
    plot.ctx.strokeStyle = "green";

    plot.ctx.beginPath();
    plot.ctx.moveTo(plot.rtoax(p.x), plot.iY(plot.rtoay(0)));

    var xx;
    for (var i = 0, x = p.x; i < iterations; i++){
      try {
        xx = ffunk(p.c, x);

        // to function
        plot.ctx.lineTo(plot.rtoax(x), plot.iY(plot.rtoay(xx)));  
        // to x = y
        plot.ctx.lineTo(plot.rtoax(xx), plot.iY(plot.rtoay(xx)));  
        x = xx;
      }
      catch (e){
        plot.ctx.stroke();
        alert("abnormal x, cannot continue: " + x);

        break;
      }
    }

    plot.ctx.stroke();
    plot.ctx.strokeStyle = tempStyle;
  }, false);

  // bifurcation
  document.getElementById("ftree-button").addEventListener('click', function(){
    var p = getFormParams();
    
    plot = new Plot({
      canvas: document.getElementById('canvas'),
      maxX: p.maxX,
      minX: p.minX,
      maxY: p.maxY,
      minY: p.minY,
    });

    plot.ctx.clearRect(0, 0, plot.canvas.width, plot.canvas.height);

    var imageData = plot.ctx.createImageData(plot.canvas.width, plot.canvas.height);
    var iterations = 1000;
    var bar = iterations / 4;
    var x = p.x;

    for (var c = p.minX; c < p.maxX; c += 0.01){
      try {
        for (var i = 0; i < iterations; i++){
          x = ffunk(c, x);

          if (i > bar){
            setPixel(imageData, parseInt(plot.rtoax(c)), parseInt(plot.iY(plot.rtoay(x))), 255, 10, 12, 255); 
          }

        }

        x = p.x;
      }
      catch (e){
        plot.ctx.stroke();
        alert("abnormal x, cannot continue: " + x);

        break;
      }
    }

    plot.ctx.putImageData(imageData, 0, 0);

    plot.hAxis({step:p.stepX, offset:0});
    plot.vAxis({step:p.stepY, offset:0});

  }, false);
};

var getFormParams = function(){
  var form = document.getElementById('parameters');
  var p = {}; //params

  p.c = parseFloat(form.elements['paramc'].value);
  p.x = parseFloat(form.elements['xzero'].value);

  p.minX = parseFloat(form.elements['minx'].value);
  p.maxX = parseFloat(form.elements['maxx'].value);
  p.minY = parseFloat(form.elements['miny'].value);
  p.maxY = parseFloat(form.elements['maxy'].value);

  p.stepX = parseFloat(form.elements['stepx'].value);
  p.stepY = parseFloat(form.elements['stepy'].value);

  return p;
}

var Plot = function(params){
  if (!params.canvas)
    throw new Error("Canvas not found!");

  this.canvas = params.canvas 
  this.ctx =  this.canvas.getContext('2d');

  this.maxX = params.maxX;
  this.minX = params.minX;
  this.maxY = params.maxY;
  this.minY = params.minY;

  // find out relative width and height of the plot 
  if (params.minX <= 0 && params.maxX <= 0){
    this.rw = Math.abs(params.minX) - Math.abs(params.maxX);
  }
  else if (params.minX > 0 && params.maxX > 0){
    this.rw = params.maxX - params.minX;
  }
  else {
    this.rw = (Math.abs(params.minX) + Math.abs(params.maxX));
  }

  if (params.minY <= 0 && params.maxY <= 0){
    this.rh = Math.abs(params.minY) - Math.abs(params.maxY);
  }
  else if (params.minY > 0 && params.maxY > 0){
    this.rh = params.maxY - params.minY;
  }
  else {
    this.rh = Math.abs(params.minY) + Math.abs(params.maxY);
  }

  this.unitX = this.canvas.width / this.rw;
  this.unitY = this.canvas.height / this.rh;

  // determine absolute coordinates of (0, 0)
  if (params.minX <= 0 && params.maxX <= 0){
    this.ox = this.canvas.width + Math.abs(params.minX) * this.unitX; 
  }
  else if (params.minX > 0 && params.maxX > 0){
    this.ox = -params.minX * this.unitX;
  }
  else {
    this.ox = Math.abs(params.minX) * this.unitX;
  }

  if (params.minY <= 0 && params.maxY <= 0){
    this.oy = this.canvas.height + Math.abs(params.minY) * this.unitY;
  }
  else if (params.minY > 0 && params.maxY > 0){
    this.oy = -params.minY * this.unitY;
  }
  else {
    this.oy = Math.abs(params.minY) * this.unitY;
  }
};

/* 
 * Draw specific axis  (beware of code duplication)
 *
 *   Example: plot.hAxis{step:0.2, offset: 20}
 */
Plot.prototype.hAxis = function(o){
  if (this.oy < 0) return;

  this.ctx.beginPath();  
  this.ctx.moveTo(this.canvas.width - o.offset, this.iY(this.oy));
  this.ctx.lineTo(o.offset, this.iY(this.oy));  
  this.ctx.stroke();  

  if (o.step){
    var invisible = 0;

    while (invisible * o.step * this.unitX < o.offset){
      invisible += 1;
    }

    // axis width
    var w = this.canvas.width - o.offset;
    var current = this.minX + (invisible * o.step); 
    var ipos = this.iY((this.oy < 20 ? this.oy + 10 : this.oy - 15));

    for (var i = invisible * o.step * this.unitX; i < w; i += o.step * this.unitX, current += o.step, invisible += 1){
      this.ctx.beginPath();
      this.ctx.moveTo(i, this.iY(this.oy - 5));
      this.ctx.lineTo(i, this.iY(this.oy + 5));  
      this.ctx.stroke();

      // hackish formatting 
      this.ctx.fillText(Math.round(current * 100) / 100, i-5, ipos);
    }
  };
}

Plot.prototype.vAxis = function(o){
  if (this.ox < 0) return;

  this.ctx.beginPath();  
  this.ctx.moveTo(this.ox, this.iY(o.offset));
  this.ctx.lineTo(this.ox, this.iY(this.canvas.height - o.offset));  
  this.ctx.stroke();  


  if (o.step){
    var invisible = 0;

    while (invisible * o.step * this.unitY < o.offset){
      invisible += 1;
    }

    // axis width
    var w = this.canvas.height - o.offset;
    var current = this.minY + (invisible * o.step); 
    var xpos = (this.ox > 25 ? this.ox - 25 : this.ox + 15);

    for (var i = invisible * o.step * this.unitY; i < w; i += o.step * this.unitY, current += o.step, invisible += 1){
      this.ctx.beginPath();
      this.ctx.moveTo(this.ox + 5, this.iY(i));
      this.ctx.lineTo(this.ox - 5, this.iY(i));  
      this.ctx.stroke();

      // hackish formatting 
      this.ctx.fillText(Math.round(current * 10) / 10, xpos, this.iY(i));
    }
  }
};

/* add grid to canvas
 *   param size side of a square
 */
Plot.prototype.grid = function(size){
  var tempWidth = this.ctx.lineWidth;

  this.ctx.lineWidth = 0.1;
  for (var i = 0; i < this.canvas.width; i += size)
    for (var j = size; j < this.canvas.height + size; j += size){
      this.ctx.strokeRect(i, this.iY(j), size, size); }

  this.ctx.lineWidth = tempWidth;
};

/* reset y */
Plot.prototype.iY = function(y){ 
  return this.canvas.height - y; 
};

/* relative to absolute coordinate conversions */
Plot.prototype.rtoax = function(ax){
  if (ax < 0) 
    return (Math.abs(this.minX) - Math.abs(ax)) * this.unitX;
  else 
    return this.canvas.width - (this.maxX - ax) * this.unitX;
}

Plot.prototype.rtoay = function(ay){
  if (ay < 0) 
    return (Math.abs(this.minY) - Math.abs(ay)) * this.unitY;
  else 
    return this.canvas.height - (this.maxY - ay) * this.unitY;
}

/*
 * Draw graphic of a given function 
 *  params
 *    o.funk -> single var function to draw
 *    o.color -> color of function graphic
 *    o.interval (array) -> interval in which to draw function graphic
 *    o.step -> pick points in specified interval to draw 
 *
 *    Example:
 *      p.draw({ funk: function(x){return x*x;}, o.color: "red", o.interval: [-1, 1], o.step: 0.2});
 */
Plot.prototype.draw = function(o){
  this.ctx.beginPath();

  // make this with default values
  var from = (o.interval ? o.interval[0] : this.minX);
  var to = (o.interval ? o.interval[1] : this.maxX);

  if (o.color){
    var tempColor = this.ctx.strokeStyle;
    this.ctx.strokeStyle = o.color; 
  }

  this.ctx.moveTo(this.rtoax(from), this.iY(this.rtoay(o.funk(from))));

  var rstep = this.unitX * o.step;
  for (var x = from + o.step; x <= to; x += o.step){
    this.ctx.lineTo(this.rtoax(x), this.iY(this.rtoay(o.funk(x))));  
  }

  this.ctx.stroke();

  if (o.color){
    this.ctx.strokeStyle = tempColor;
  }
}
