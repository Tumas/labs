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
    
    context "editing" do
      it "should add words" do
        wl = Exam.new("Name")
        good_word_samples.each do |sample| 
          wl.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
        end
        wl.should have(good_word_samples.size).words
      end

      it "should let remove words" do
        wl = Exam.new("Name")
        words = []
        good_word_samples.each do |sample| 
          words << Word.new(sample[:value], sample[:translation], sample[:hint])
          wl.add_word(words.last)
        end

        wl.remove_word(words.first)
        wl.should have(good_word_samples.size - 1).words
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
        @e.take {|w| w.value }.should == 1
      end

      it "should return score 0 if all answers are incorrect" do
        @e.take {|w| "wrong answer" }.should == 0
      end
    end
  end
end
