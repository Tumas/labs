module LearningSystem
  class Test
    attr_reader :words

    def initialize(name)
      raise "Test must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
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
  end
end
