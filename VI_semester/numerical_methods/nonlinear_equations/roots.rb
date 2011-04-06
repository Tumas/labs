MAX_ITERATIONS_COUNT = 100

class Function
  include Math

  def initialize(expression)
    @e = expression.to_s
  end

  def value(x)
    eval(@e.gsub("x", x.to_s))
  end
end

class SIMethod
  attr_reader :f, :epsilon, :delta

  # simple iterations method
  #   f(x) = 0
  #   x = g(x)
  #   delta = (1 - q)/q, g'(x) <= q, ok if q < 1
  
  def initialize(f, x0, q, epsilon = 0.000001)
    @f = f
    @x0, @x1 = x0.to_f, nil
    @epsilon, @delta = epsilon.to_f, (1 - q)/q.to_f

    puts "Warning: solution may diverge: #{q}" if q >= 1
  end

  def solve(max_iterations = MAX_ITERATIONS_COUNT)
    iterations = 0

    loop do
      iterations += 1
      @x1 = @f.value(@x0)

      yield @x1, @x0, iterations if block_given?
      break if accurate? or iterations == max_iterations

      @x0 = @x1
    end

    if iterations == max_iterations
      puts "Iteration limit (#{iterations}) reached. Stopping. Could not find solution." 
    end 
    @x1
  end

  def accurate?
    (@x1 - @x0).abs <= @delta * @epsilon
  end
end

class SecantMethod
  attr_reader :f

  def initialize(f, x0, x1, epsilon = 0.000001)
    @f = f
    @x0, @x1, @x2 = x0.to_f, x1.to_f, 0.0
    @epsilon = epsilon.to_f
  end

  def solve(max_iterations = MAX_ITERATIONS_COUNT)
    iterations = 0

    loop do
      yield @x2, @x1, @x0, iterations if block_given?

      iterations += 1
      val = @f.value(@x1)
      denom = (val - @f.value(@x0))

      unless denom.zero?
        @x2 = @x1 - val * (@x1 - @x0) / denom 
      else
        puts "function values are the same. Adjusting first x by 0.1"
        @x1 += 0.1
        next
      end

      break if accurate? or iterations == max_iterations
      @x0, @x1 = @x1, @x2
    end

    if iterations == max_iterations
      puts "Iteration limit (#{iterations}) reached. Stopping. Could not find solution." 
    end 
    @x2
  end

  def accurate?
    (@x2 - @x1).abs <= @epsilon
  end
end

sim = SIMethod.new(Function.new("cos(x)"), 0.2, 1)
x = sim.solve do |x1, x0, i|
  msg = "Iteration: #{i}, x#{i}: #{x1}"
  msg += ", |x#{i} - x#{i-1}|: #{(x1-x0).abs}" unless x1.nil?
  puts msg
end

#sim = SIMethod.new(Function.new("atan(x + 1) / 5"), 0, 0.1)
#x = sim.solve do |x1, x0, i|
#  msg = "Iteration: #{i}, x#{i}: #{x1}"
#  msg += ", |x#{i} - x#{i-1}|: #{(x1-x0).abs}" unless x1.nil?
#  puts msg
#end

puts "Solution: #{x}"
puts "*" * 20 

#sm = SecantMethod.new(Function.new("atan(x + 1) / 5 - x"), 0, 0.1)
sm = SecantMethod.new(Function.new("cos(x) - x"), 0, 1)
x = sm.solve do |x2, x1, x0, i|
  puts "Iteration: #{i}, x: #{x2}, f: #{sm.f.value(x2)}"
end
puts "Solution: #{x}"

#sm = SecantMethod.new(Function.new("x**3 - 3*x + 1"), 0, 1, 0.000000000000000000000001)
#x = sm.solve do |x2, x1, x0, i|
#  puts "Iteration: #{i}, x: #{x2}, f: #{sm.f.value(x2)}"
#end
#puts "Solution: #{x}"
