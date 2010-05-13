require File.dirname(__FILE__) + '/../spec_helper'

describe Exam do
  fixtures :users
  fixtures :words
  fixtures :exams
  fixtures :scores

  describe Exam, " validations/associations" do
    it { should validate_presence_of :title }
    it { should have_many :tags }
    it { should have_many :scores }
    it { should belong_to :user }
  end

  describe Exam, " taking and calculating statistics" do
    before do
      @predicate = Proc.new {|word, answer| word.value == answer }
    end

    it "should return score 1 if all answers are correct" do 
      exams(:one).take(@predicate) {|w| w.value }.should == 1
    end

    it "should return score 0 if all answers are incorrect" do
      exams(:one).take(@predicate) {|w| w.translation }.should == 0
    end

    it "should return a float number between 0 and 1 with maximum two decimal places as score" do
      i = 0
      # we should answer 1 word out of 3 by this algorithm
      exams(:one).take(@predicate) do |w|
        i += 1
        if i == 1
          w.value
        else
          w.translation
        end
      end.should have_valid_format
    end

    it "should know how many times it was taken" do
      scores_size = exams(:one).scores.size
      lambda {
        exams(:one).take(@predicate) { |w| w.translation }.should == 0
      }.should change { exams(:one).scores(true).size }.from(scores_size).to(scores_size + 1)
    end

    it "should reset its scoring history"  do
      scores_size = exams(:one).scores.size
      lambda { 
        exams(:one).reset_scores
      }.should change { exams(:one).scores(true).size }.from(scores_size).to(0)
    end

    it "should know its average score rating" do
      exams(:one).average_score.should == 0.52
    end

    it "should have averago score 0 if it hasn't been taken yet" do
      exams(:three).average_score.should == 0
    end

    it "should return its average score as a float number between 0 and 1 and with maximum two decimal places as score" do
      2.times { exams(:one).take(@predicate) { |w| w.value }}
      exams(:one).take(@predicate) { |w| w.translation }
      exams(:one).average_score.should have_valid_format
    end

    it "should find its last score" do
      scores(:average).created_at = Time.new.tomorrow.to_s(:db)
      scores(:average).save
      exams(:one).last_score.should === scores(:average)
    end

    describe Exam, " counting inactivity time" do
      before do
        last_score = Score.new
        last_score.stubs(:created_at).returns("2010-04-28 22:25:59")
        exams(:one).stubs(:last_score).returns(last_score)
      end

      it "should know time in hours since last taking" do
        exams(:one).hours_from_last_taking_to("2010-04-29 01:25:59").should == 3
      end

      it "should return 2 if 1.5h have passed" do
        exams(:one).hours_from_last_taking_to("2010-04-28 23:56:59").should == 2
      end

      it "should raise an exception if given date is earlier" do
        lambda {
          exams(:one).hours_from_last_taking_to("2010-04-28 20:56:59") }.should raise_error 
      end
    end
  end
end
