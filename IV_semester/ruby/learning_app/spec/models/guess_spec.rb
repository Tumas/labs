require File.dirname(__FILE__) + '/../spec_helper'

describe Guess do
  fixtures :users
  fixtures :words
  fixtures :guesses

  describe Guess, " validations/associations" do
    it { should validate_presence_of :value }
    it { should validate_inclusion_of(:part, :in => %w(value translation)) }
    it { should belong_to :user }
    it { should belong_to :word }
  end

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
