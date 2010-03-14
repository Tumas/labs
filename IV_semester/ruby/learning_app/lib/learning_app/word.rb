module LearningSystem
  class Word
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
    end

    def to_sym
      @value.to_sym
    end
  end
end
