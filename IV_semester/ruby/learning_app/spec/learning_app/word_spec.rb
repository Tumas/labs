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

    end
  end
end
