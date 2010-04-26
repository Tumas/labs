require File.dirname(__FILE__) + '/../spec_helper'

describe Word do
  describe Word, "validation" do
    before(:each) do
      @valid_attributes = {
        :value => "value for value",
        :translation => "value for translation",
        :example => "value for example"
      }
    end

    it "should be invalid without a name" do
      Word.new(@valid_attributes.except(:value)).should_not be_valid
    end

    it "should be invalid without a translation" do
      Word.new(@valid_attributes.except(:translation)).should_not be_valid
    end
  end

  describe Word, " guessing" do
    fixtures :words
    fixtures :guesses

    before(:each) do
      @word = Word.find_by_value("spouse")

      @guess = mock()
      @guess.expects(:guessed)
    end

    it "should return true if guess was correct when guessing by value " do
      @guess.expects(:value).returns('spouse').at_most(2)
      @guess.expects(:part).returns('value').at_most(2)

      @word.guess(@guess).should == true
    end

    it "should return true if guess was correct when guessing by translation " do
      @guess.expects(:value).returns('a persons partner in marriage').at_most(2)
      @guess.expects(:part).returns('translation').at_most(2)

      @word.guess(@guess).should == true
    end

    it "should return false if guess was incorrect when guessing by value" do
      @guess.expects(:value).returns('e').at_most(2)
      @guess.expects(:part).returns('value').at_most(2)

      @word.guess(@guess).should == false
    end

    it "should return false if guess was incorrect when guessing by translation" do
      @guess.expects(:value).returns('e').at_most(2)
      @guess.expects(:part).returns('translation').at_most(2)

      @word.guess(@guess).should == false
    end

    #it "should be guessed by both value and translation"
    it "should be guessed with specified accuracy"
  end

  describe Word, " marking guess as guessed" do
    fixtures :words

    before(:each) do
      @word = Word.find_by_value("spouse")
      @guess = mock()
      @guess.expects(:part).returns('value').at_most(4)
      @guess.expects(:guessed).times(2)
    end

    it "should mark given guess as guessed when answered correctly" do
      @guess.expects(:value).returns('spouse').at_most(4)
      2.times { @word.guess(@guess) }
    end

    it "should mark given guess as guessed when answered incorrectly" do
      @guess.expects(:value).returns('aksdjlas').at_most(4)
      2.times { @word.guess(@guess) }
    end
  end

  describe Word, " counting guessing statistics" do
    fixtures :users
    fixtures :words
    fixtures :guesses

    before(:each) do
      @word = Word.find_by_value("spouse")
    end

    it "should know how many times it was guessed" do
      2.times { @word.guess(Guess.find_by_value_and_part("spouse", "value")) }
      2.times { @word.guess(Guess.find_by_value_and_part("spouse", "translation")) }

      @word.times_guessed.should == 4
    end

    it "should know how many times it was answered" do
      2.times { @word.guess(Guess.find_by_value_and_part("spouse", "value")) }
      2.times { @word.guess(Guess.find_by_value_and_part("spouse", "translation")) }

      @word.times_answered.should == 2
    end

    it "should be guessed and answerred equally" do
      2.times { @word.guess(Guess.find_by_value_and_part("spouse", "value")) }
      @word.times_guessed.should == @word.times_answered
    end

    it "should know how many different wrong guesses were attempted" do
      pending
      @word.guess(Guess.find_by_value_and_part("spouse", "value")) 
      @word.guess(Guess.find_by_value_and_part("wrong", "translation")) 
 
      @word.should have(2).wrong_guesses
    end

    it "should know its wrong guesses and times they were guessed" do
      pending
      p = Guess.find_by_value_and_part("spouse", "translation")
      2.times do 
        @word.guess(p) 
        @word.guess(Guess.find_by_value_and_part("wrong", "translation")) 
      end

      @word.wrong_guesses.first.should === p
    end

    it "should know its correct guesses"
  end
end
