require File.dirname(__FILE__) + '/../spec_helper'

describe Guess do
  describe Guess, " creating" do
    before(:each) do
      @valid_attributes = {
        :value => "attempt to guess smth",
        :part => "value",
      }
    end

    it "should have a value" do
      Guess.new(@valid_attributes.except(:value)).should_not be_valid
    end

    it "should have a part" do
      Guess.new(@valid_attributes.except(:part)).should_not be_valid
    end

    it "should have part with value :value" do
      Guess.new(@valid_attributes).should be_valid
    end

    it "should have part with value :translation" do
      @valid_attributes[:part] = "translation"
      Guess.new(@valid_attributes).should be_valid
    end

    it "should have part with value :translation or :value only" do
      @valid_attributes[:part] = "ton"
      Guess.new(@valid_attributes).should_not be_valid
    end
  end

  describe Guess, " being guessed" do
    fixtures :users
    fixtures :words
    fixtures :guesses

    it "should increase its counter" do
      g = Guess.find_by_value_and_part("spouse", "value")

      lambda { 2.times { g.guessed } }.should change {
        Guess.find_by_value_and_part("spouse", "value").count
      }.from(0).to(2)
    end
  end
end
