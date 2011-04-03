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
  
  def initialize(f, x0, epsilon, delta)
    @f = f
    @x0, @x1 = x0, nil
    @epsilon, @delta = epsilon, delta
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
     puts "Iteration limit (#{iterations}) reached. Stopping"
     nil
    else 
      @x1
    end
  end

  def accurate?
    (@x1 - @x0).abs <= @delta * @epsilon
  end
end

class SecantMethod
  attr_reader :f

  def initialize(f, x0, x1, epsilon)
    @f = f
    @x0, @x1, @x2 = x0, x1, 0
    @epsilon = epsilon
  end

  def solve(max_iterations = MAX_ITERATIONS_COUNT)
    iterations = 0

    loop do
      @x0, @x1 = @x1, @x2
      yield @x2, @x1, @x0, iterations if block_given?

      iterations += 1
      val = @f.value(@x1)
      @x2 = @x1 - val * (@x1 - @x0) / (val - @f.value(@x0))

      break if accurate? or iterations == max_iterations
    end

    if iterations == max_iterations
     puts "Iteration limit (#{iterations}) reached. Stopping"
     nil
    else 
      @x2
    end
  end

  def accurate?
    (@x2 - @x1).abs <= @epsilon
  end
end

sim = SIMethod.new(Function.new("2*x - 1"), 0, 0.000001, 9)
x = sim.solve do |x1, x0, i|
  msg = "Iteration: #{i}, x#{i}: #{x1}"
  msg += ", |x#{i} - x#{i-1}|: #{(x1-x0).abs}" unless x1.nil?
  puts msg
end

puts "Solution: #{x}"
puts "*" * 20 

sm = SecantMethod.new(Function.new("x**3 - 3*x + 1"), 0.5, 0.1, 0.000001)
x = sm.solve do |x2, x1, x0, i|
  puts "Iteration: #{i}, x: #{x2}, f: #{sm.f.value(x2)}"
end
puts "Solution: #{x}"
