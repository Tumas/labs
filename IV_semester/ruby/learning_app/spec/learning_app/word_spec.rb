require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Word do
    include WordSamples

    context "creating" do
      before(:each) do
        @good_word_samples = good_word_samples
        @bad_word_samples = bad_word_samples
      end

      context "creating valid words" do
        it "should create valid words smoothly" do
          @good_word_samples.each do |sample|
            lambda { Word.new(sample[:value], sample[:translation], sample[:hint])}.should_not raise_error
          end
        end
      end

      context "creating invalid words" do
        it "should raise an exception when invalid parameters are specified" do
          @bad_word_samples.each do |sample|
            lambda { Word.new(sample[:value], sample[:translation], sample[:hint])}.should raise_error
          end
        end
      end

      context "guessing " do
        before(:each) do
          @word = Word.new("Test", "form of examination", "similar to quiz")
          @correct_guesses = ["Test", "TEST", "test", " test ", "test ", " test"]
        end

        it "should return true if guess was correct" do
          @correct_guesses.each do |answer|
            @word.guess_value(answer).should == true
          end
        end

        it "should return false if guess was incorrect" do
          @word.guess_value("quiz").should == false
        end

        # #{correct_guesses.size} does not work
        it "should be guessed and answerred ? times" do
          @correct_guesses.each do |answer|
            @word.guess_value(answer)
          end
          @word.times_guessed.should == @correct_guesses.size
          @word.times_answered.should == @correct_guesses.size 
        end

        it "should be guessed 2 times and answerred 0 times" do
          2.times { @word.guess_value("quiz") }
          @word.times_guessed.should == 2
          @word.times_answered.should == 0
        end
      end
    end
  end
end
