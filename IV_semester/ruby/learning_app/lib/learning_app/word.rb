require File.join(File.dirname(__FILE__), '/taggable') 

module LearningSystem
  class Word
    include Taggable 

    attr_reader :times_guessed, :times_answered
    attr_accessor :value, :translation, :hint

    def initialize(value, translation, hint = nil)
      raise "Word and translation must be not empty" unless valid?(value, translation)

      @value = value
      @translation = translation
      @hint = hint

      @times_guessed = 0
      @times_answered = 0
    end

    def valid?(value, translation)
      if value.nil? or translation.nil? or value.empty? or translation.empty?
        false
      else
        true
      end
    end

    def to_sym
      @value.to_sym
    end

    def guess(opts)
      vg = opts[:value].strip.downcase if opts[:value]
      tg = opts[:translation].strip.downcase if opts[:translation]

      count = true
      count = opts[:count] if not opts[:count].nil?

      @times_guessed += 1 if count

      if (@value.downcase == vg and @translation.downcase == tg) or (@value.downcase == vg and tg.nil?) or (@translation.downcase == tg and vg.nil?)
        @times_answered += 1 if count
        true
      else
        false
      end
    end
  end
end
