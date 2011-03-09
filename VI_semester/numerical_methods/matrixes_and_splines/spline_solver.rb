#!/usr/bin/ruby

require File.join(File.dirname(__FILE__), 'spline') 
require 'gnuplot'
require 'benchmark'

class SplineSolver
  attr_reader :spline 

  def initialize(filename)
    temp = []
    if File.exists?(filename)
      File.open(filename, "r"){ |f| 2.times { temp << f.readline.split(" ").map { |x| x.to_f }}}
      @spline = Spline.new(*temp)
    else
      puts "ERROR: File: #{filename} is not present"
      exit
    end
  end

  def approximate
    @spline.approximate
    puts @spline
    puts "Coeficients: "

    print_result("e", @spline.e)
    print_result("G", @spline.big_g)
    print_result("H", @spline.big_h)
  rescue Exception => e
    puts e.message
  end

  def print_result(msg, col)
    col.each_with_index do |e, i|
      puts "\t#{msg}#{i}: #{e}"
    end
    puts 
  end
end

path = ARGV[0]
xs = ARGV[1..-1]
default_path = 'tests_sp/t1'

p Benchmark.measure {
  puts "************ Approximating: #{path} ********** "
  @ss = SplineSolver.new(path)
  @ss.approximate

  puts "Approximated function values:"
  xs.each do |x|
    puts "at #{x}: #{@ss.spline.value(x)}"
  end

  print "#{@ss.spline.x}\n"
  print "#{@ss.spline.y}\n"
}

Gnuplot.open do |gp|
  Gnuplot::Plot.new( gp ) do |plot|
  
    plot.xrange "[#{@ss.spline.x.first}:#{@ss.spline.x.last}]"
   
    x = (@ss.spline.x.first..@ss.spline.x.last).step(0.1).to_a
    y = x.map {|x| @ss.spline.value(x) if x <= @ss.spline.x.last }
    plot.data << Gnuplot::DataSet.new([x, y]) do |ds|
      ds.with = "lines"
      ds.linewidth = 1
      ds.title = "spline in."
    end

    y = x.map {|x| (1 + x) / (3 + x*x) }
    plot.data << Gnuplot::DataSet.new([x, y]) do |ds|
      ds.with = "lines"
      ds.linewidth = 1
      ds.title = "f(x)"
    end if path == default_path 
  end
end
