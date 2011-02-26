#!/usr/bin/ruby

# maziausiai 4 testai
#  skirtingos dimensijos

#  pagrindines istrizaines svyravimai
#     failing tests

# pakankama vs butina 
#  skaicius dalinasi is 4 tai jis pirminis - pakankama
#  skaicius dalinasi is 2 tai jis pirminis - butina (nes jei pirminis tai dalinasi is 2, bet nebutinai is 4)

class TMatrix
  # a - sub-diagonal 
  # b - main diagonal
  # c - sup-diagonal 
  # d - free variables 
  
  def initialize(a, b, c, d)
    @m= { :a => a.to_a, :b => b.to_a, :c => c.to_a, :d => d.to_a }
  end

  def size
    @m[:b].size
  end

  def valid?
    all, one = true, false

    0.upto size-1 do |i|
      sum = @m[:a][i].abs + @m[:c][i].abs
      all = false if @m[:b][i].abs < sum
      one = true if @m[:b][i].abs > sum
    end

    return false if not all or not one
    true
  end

  def solve
    raise "ERROR: Sufficient condition is not met." unless valid?
    s = size - 1

    # go upwards
    @m[:c][0] = -1 * @m[:c][0] / @m[:b][0]
    @m[:d][0] = @m[:d][0] / @m[:b][0]
    
    1.upto s do |i|
      denominator = @m[:a][i] * @m[:c][i-1] + @m[:b][i]
      @m[:c][i] = -1 * @m[:c][i] / denominator unless i == s
      @m[:d][i] = (@m[:d][i] - @m[:a][i] * @m[:d][i-1]) / denominator 
    end
    
    # go downwards and find solutions
    x = [@m[:d][s]]
    (s-1).downto 0 do |i|
      x << @m[:c][i] * x.last + @m[:d][i] 
    end

    x.reverse
  end

  def to_s
    # >_<
    sep = "  " * 2
    first_line = "\t#{@m[:b][0]}" + sep+ "#{@m[:c][0]}" + sep + skip_nums(size-2, sep) + "| #{@m[:d][0]}\n\t"
    last_line = skip_nums(size-2, sep) + "#{@m[:a][size-1]}" + sep  + "#{@m[:b][size-1]}" + sep + "| #{@m[:d][size-1]}\n\t"

    1.upto(size-2).inject(first_line) do |str, i|
      str + skip_nums(i-1, sep)  + [@m[:a][i], @m[:b][i], @m[:c][i]].join(sep) +
      sep + skip_nums(size-2-i, sep) + "| #{@m[:d][i]}\n\t"
    end + last_line
  end

  protected
    def skip_nums(count, sep, empty = 0.to_f)
      count.times.inject("") {|s, i| s + "#{empty}" + sep }
    end
end
