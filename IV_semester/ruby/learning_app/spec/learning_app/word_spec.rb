require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Word do
    context "creating" do
      before(:each) do
        @good_word_samples = [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]

        @bad_word_samples = [
          { :value => "", :translation => "" },
          { :value => "facet", :translation => nil, :hint => "" },
          { :translation => "a distinct feature" },
          { :hint => "unknown" }
        ]
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
