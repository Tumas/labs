module LearningSystem
  class Word
    attr_reader :times_guessed, :times_answered
    attr_accessor :value, :translation, :hint

    def valid?(value, translation)
      if value.nil? or translation.nil? or value.empty? or translation.empty?
        false
      else
        true
      end
    end

    def initialize(value, translation, hint = nil)
      raise "Word and translation must be not empty" unless valid?(value, translation)

      @value = value
      @translation = translation
      @hint = hint

      @times_guessed = 0
      @times_answered = 0
    end

    def to_sym
      @value.to_sym
    end

    def guess_value(value)
      @times_guessed += 1

      if @value.downcase == value.strip.downcase
        @times_answered += 1
        true
      else
        false
      end
    end

  end
end
