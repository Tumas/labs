module LearningSystem
  class Exam
    attr_reader :words, :times_taken

    def initialize(name)
      raise "#{self.class} must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
      @times_taken = 0
      @words = {}
    end

    def add_word(word)
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
      local_score / @words.size
    end

  end
end
