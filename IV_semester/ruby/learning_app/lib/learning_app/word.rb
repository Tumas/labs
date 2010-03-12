module LearningSystem
  class Word
    #attr_reader :word, :translation, :hint

    # Word
    #   value
    #   translation
    #   hint
    #   examples NY
    #

    def valid?(word, translation)
      if word == nil or translation == nil or word.size == 0 or translation.size == 0
        false
      else
        true
      end
    end

    def initialize(word, translation, hint = nil)
      raise "Word and translation must be not empty" unless valid?(word, translation)
      @word = word
      @translation = translation
      @hint = hint
    end
  end
end
