require File.join(File.dirname(__FILE__), '/taggable') 
require File.join(File.dirname(__FILE__), '/word_list') 

module LearningSystem
  class Exam
    include Taggable
    include WordList

    attr_reader :times_taken
    attr_accessor :name

    def initialize(name)
      raise "#{self.class} must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
      @times_taken = 0
    end

    def to_sym
      @name.to_sym
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
      local_score / @words.size
    end
  end
end
