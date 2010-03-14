require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe WordList do
    include WordSamples

    before(:each) do
    end

    context "creating" do
      it "should not be created with empty name" do
        [nil, "  ", ""].each do |bad_name|
          lambda { WordList.new(bad_name) }.should raise_error
        end
      end
    end
    
    context "editing" do
      before(:each) do
      end

      it "should add words" do
        wl = WordList.new("Name")
        good_word_samples.each do |sample| 
          wl.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
        end
        wl.should have(good_word_samples.size).words
      end

      it "should let remove words" do
        wl = WordList.new("Name")
        words = []
        good_word_samples.each do |sample| 
          words << Word.new(sample[:value], sample[:translation], sample[:hint])
          wl.add_word(words.last)
        end

        wl.remove_word(words.first)
        wl.should have(good_word_samples.size - 1).words
      end
    end

    context "taking quizzes" do
      it "should know how many times it was taken" do
        wl = WordList.new("Name")
        lambda { 3.times { wl.take } }.should change{ wl.times_taken }.from(0).to(3)
      end
    end

  end
end
