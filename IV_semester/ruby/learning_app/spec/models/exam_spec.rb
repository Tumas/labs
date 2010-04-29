require File.dirname(__FILE__) + '/../spec_helper'

describe Exam do
  fixtures :users
  fixtures :words
  fixtures :exams

  describe Exam, " taking and calculating statistics" do
    before do
      @e = exams(:one)
      @predicate = Proc.new {|word, answer| word.value == answer }
    end

    it "should return score 1 if all answers are correct" do 
      @e.take(@predicate) { |w| w.value }.should == 1
    end

    it "should return score 0 if all answers are incorrect" do
      @e.take(@predicate) { |w| w.translation }.should == 0
    end

    it "should return a float number between 0 and 1 with maximum two decimal places as score" do
      i = 0
      # we should answer 1 word out of 3 by this logic
      @e.take(@predicate) do |w|
        i += 1
        if i == 1
          w.value
        else
          w.translation
        end
      end.should have_valid_format
    end

    it "should know how many times it was taken" do
      scores_size = @e.scores.size
      lambda {
        @e.take(@predicate) { |w| w.translation }.should == 0
      }.should change { @e.scores(true).size }.from(scores_size).to(scores_size + 1)
    end

    it "should reset its scoring history" 

    # Move out Proc
    it "should know its average score rating" do
      @e.take(@predicate) { |w| w.value }
      @e.take(@predicate) { |w| w.translation }
      @e.average_score.should == 0.5
    end

    it "should return its average score as a float number between 0 and 1 and with maximum two decimal places as score" do
      2.times { @e.take(@predicate) { |w| w.value }}
      @e.take(@predicate) { |w| w.translation }
      @e.average_score.should have_valid_format
    end

    it "should know time since last being taken"
  end
end
