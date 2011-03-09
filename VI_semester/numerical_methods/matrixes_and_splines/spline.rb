#!/usr/bin/ruby
require File.join(File.dirname(__FILE__), 'tmatrix') 

class Spline
  attr_reader :e, :big_g, :big_h
  attr_reader :x, :y

  def initialize(x, y)
    @h, @f, @g = [], [], []
    @x, @y = [x, y].each { |col| col.to_a.map! { |e| e.to_f } }
  end

  def size
    @x.size
  end

  def find_pieces
    1.upto(size-1) { |i| @h << @x[i] - @x[i-1] }
    1.upto(size-1) { |i| @f << (@y[i] - @y[i-1]) / @h[i-1] }

    # creating tridiagonal matrix to solve and solving it
    a, b, c, d = [], [], [], []
    1.upto(size-2) do |i|
      a << @h[i-1]
      b << 2*(@h[i-1] + @h[i]) 
      c << @h[i]
      d << 6*(@f[i]-@f[i-1])
    end

    @m = TMatrix.new(a, b, c, d)
    puts @m
    @g = [0.0] | @m.solve 
    @g << 0.0
  end

  def find_cfs
    @e, @big_g, @big_h = [], [], []

    (size-1).times do |i| 
      @e << @f[i] - @g[i+1]*@h[i]/6.0 - @g[i]*@h[i]/3.0 
      @big_g << @g[i] / 2.0
      @big_h << (@g[i+1] - @g[i]) / (6*@h[i])
    end
  end

  def approximate
    find_pieces
    find_cfs
  end

  def to_s
    "#{show_line(@x, "X: ")}#{show_line(@y, "Y: ")}\n\t" +
    "#{show_line(@h, "h: ")}\t#{show_line(@f, "f: ")}\t#{show_line(@g, "g: ")}"
  end

  def value(x)
    x = x.to_f
    raise "Specified x is out of range" if x < @x.first or x > @x.last
    approximate if @e.empty? or @big_g.empty? or @big_h.empty?

    1.upto(size-1) do |i| 
      return value_at(x, i-1) if x >= @x[i-1] and x <= @x[i] 
    end
  end

  private

  def value_at(x, i)
    x -= @x[i]
    #puts "#{@y[i]} + #{@e[i]}*#{x} + #{@big_g[i]}*(#{x}**2) + #{@big_h[i]}*(#{x}**3)"
    @y[i] + @e[i]*x + @big_g[i]*(x**2) + @big_h[i]*(x**3)
  end

  def show_line(col, msg)
    "#{col.inject(msg) {|str, e| str + e.to_s + "\t" }}\n"
  end
end
