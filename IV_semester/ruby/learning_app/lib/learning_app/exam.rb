require File.join(File.dirname(__FILE__), '/taggable') 
require File.join(File.dirname(__FILE__), '/word_list') 

module LearningSystem
  class Exam
    include Taggable
    include WordList

    attr_reader :times_taken, :history
    attr_accessor :name

    def initialize(name)
      raise "#{self.class} must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
      @times_taken = 0
      @history = []
    end

    def to_sym
      @name.to_sym
    end

    def history_reset
      @history = []
    end

    def average_score
      if @history.empty?
        0
      else
        @history.inject(0) { |x, y| x + y } / @history.size
      end
    end

    def take(p)
      local_score = 0.0
      if block_given?
        each_word do |w|
          answer = yield w
          local_score += 1.0 if p.call(w, answer)
        end
      end

      @times_taken += 1
      score = format("%0.2f", local_score / @words.size).to_f
      @history << score
      score
    end
  end
end
