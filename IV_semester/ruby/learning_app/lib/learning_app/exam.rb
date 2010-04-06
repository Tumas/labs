require File.join(File.dirname(__FILE__), '/taggable') 

module LearningSystem
  class Exam
    include Taggable

    attr_reader :words, :times_taken
    attr_accessor :name

    def initialize(name)
      raise "#{self.class} must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
      @times_taken = 0
      @words = {}
    end

    def add_word(word, overwrite = false)
      raise "Word is already there!" if not overwrite and not @words[word.to_sym].nil? 
      @words[word.to_sym] = word
    end

    def remove_word(word)
      @words.delete(word.to_sym)
    end

    def to_sym
      @name.to_sym
    end

    def each_word(&block)
      @words.each_value(&block)
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
