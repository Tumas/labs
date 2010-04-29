require File.dirname(__FILE__) + '/../spec_helper'

describe Guess do
  fixtures :users
  fixtures :words
  fixtures :guesses

  describe Guess, " being guessed" do
    it "should increase its counter" do
      g = guesses(:correct_by_value)

      lambda { 2.times { g.guessed } }.should change {
        Guess.find_by_value_and_part("spouse", "value").count
        #guesses(:correct_by_value).count
      }.from(0).to(2)
    end
  end
end
