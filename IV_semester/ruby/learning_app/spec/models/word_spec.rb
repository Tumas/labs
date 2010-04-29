require File.dirname(__FILE__) + '/../spec_helper'

describe Word do
  fixtures :users
  fixtures :words
  fixtures :guesses

  before do
    @word = words(:spouse)
  end

  describe Word, " guessing" do
    it "should return true if guess was correct when guessing by value" do
      @word.guess(guesses(:correct_by_value)).should == true
    end

    it "should return true if guess was correct when guessing by translation " do
      @word.guess(guesses(:correct_by_translation)).should == true
    end

    it "should return false if guess was incorrect when guessing by value" do
      @word.guess(guesses(:incorrect_by_value)).should == false
    end

    it "should return false if guess was incorrect when guessing by translation" do
      @word.guess(guesses(:incorrect_by_translation)).should == false
    end

    it "should be guessed with specified accuracy"
  end

  describe Word, " counting guessing statistics" do
    it "should know how many times it was guessed" do
      2.times { @word.guess(guesses(:correct_by_value)) }
      2.times { @word.guess(guesses(:incorrect_by_value)) }

      @word.times_guessed.should == 4
    end

    it "should know how many times it was answered" do
      2.times { @word.guess(guesses(:correct_by_value)) }
      2.times { @word.guess(guesses(:incorrect_by_translation)) }

      @word.times_answered.should == 2
    end

    it "should be guessed and answerred equally" do
      2.times { @word.guess(guesses(:correct_by_translation)) }
      @word.times_guessed.should == @word.times_answered
    end

    it "should know how many different wrong guesses were attempted" do
      pending
      @word.should have(2).wrong_guesses
    end

    it "should know its wrong guesses and times they were guessed" 
  end
end
