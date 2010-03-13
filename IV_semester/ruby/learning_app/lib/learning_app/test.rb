module LearningSystem
  class Test
    attr_reader :words

    def initialize(name)
      raise "Test must have a name" if name.nil? or name =~ /^( )*$/

      @name = name
      @words = {}
    end

    def add_word(word)
      @words[word.value.to_sym] = word
    end

    # should be refactored into word.to_sym
    def delete_word(word)
      @words.delete(word.value.to_sym)
    end
  end
end
