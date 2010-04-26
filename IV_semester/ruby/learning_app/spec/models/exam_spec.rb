require File.dirname(__FILE__) + '/../spec_helper'

describe Exam do
  describe Exam, " creating" do
    before(:each) do
      @valid_attributes = {
        :title => "value for title",
        :public => false
      }
    end

    it "should have a title" do
      Exam.new(@valid_attributes.except(:title)).should_not be_valid
    end
  end

  describe Exam, " taking and calculating statistics" do
    fixtures :users
    fixtures :words
    fixtures :exams

    before(:each) do
      @e = Exam.find_by_title('FirstExam')
    end

    it "should return score 1 if all answers are correct" do 
      @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.value }.should == 1
    end

    it "should return score 0 if all answers are incorrect" do
      @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.translation }.should == 0
    end

    it "should return a float number between 0 and 1 with maximum two decimal places as score" do
      i = 0
      @e.take( Proc.new {|word, answer| word.value == answer } ) do |w|
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
        @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.translation }.should == 0
      }.should change { @e.scores(true).size }.from(scores_size).to(scores_size + 1)
    end

    #it "should reset its scoring history" 
    it "should know its average score rating" do
      @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.value }
      @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.translation }
      @e.average_score.should == 0.5
    end

    it "should return its average score as a float number between 0 and 1 and with maximum two decimal places as score" do
     2.times { @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.value }}
      @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.translation }
      @e.average_score.should have_valid_format
    end

    it "should know time since last being taken"
  end
end
