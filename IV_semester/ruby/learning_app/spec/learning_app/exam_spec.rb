require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Exam do
    include WordSamples

    context "creating" do
      it "should not be created with empty name" do
        [nil, "  ", ""].each do |bad_name|
          lambda { Exam.new(bad_name) }.should raise_error
        end
      end
    end
    
    context "taking" do
      before(:each) do
        @e = Exam.new("name")
        good_word_samples.each do |s|
          @e.add_word(Word.new(s[:value], s[:translation], s[:hint]))
        end
      end

      it "should return score 1 if all answers are correct" do
        @e.take( Proc.new {|word, answer| word.value == answer } ) { |w| w.value }.should == 1
        @e.take( Proc.new {|word, answer| word.translation == answer } ) { |w| w.translation }.should == 1
      end

      it "should return score 0 if all answers are incorrect" do
        @e.take(Proc.new {|word, answer| word.translation == answer }) {|w| w.value }.should == 0
        @e.take(Proc.new {|word, answer| word.value == answer }) {|w| w.translation }.should == 0
      end

      it "should increment its number of times taken" do
        @e.times_taken.should == 0 
        @e.take( Proc.new {|w, a| w.guess( :value => a)}) {|w| w.value }
        @e.times_taken.should == 1
      end
    end
  end
end
