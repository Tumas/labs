#!/usr/bin/ruby

require File.join(File.dirname(__FILE__), 'tmatrix') 
require 'benchmark'

class Solver
  def initialize(filename)
    temp = []
    if File.exists?(filename)
      File.open(filename, "r"){ |f| 4.times { temp << f.readline.split(" ").map { |x| x.to_f }}}
      @t_matrix = TMatrix.new(*temp)
      puts @t_matrix
    else
      puts "ERROR: File: #{filename} is not present"
      exit
    end
  end

  def solve
    solutions = @t_matrix.solve
    puts "Solved: "
    solutions.each_with_index do |sol, i|
      puts "\tx#{i+1}: #{sol}"
    end
  rescue Exception => e
    puts e.message
  end
end

ARGV.each do |path|
  p Benchmark.measure {
    puts "************ Solving: #{path} ********** "
    Solver.new(path).solve
  }
end
