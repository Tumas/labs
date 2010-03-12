require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Word do
    context "creating" do
      before(:each) do
        @good_words = [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]

        @bad_words = [
          { :value => "", :translation => "" },
          { :value => "facet", :translation => nil, :hint => "" },
          { :translation => "a distinct feature" },
          { :hint => "unknown" }
        ]
      end

      context "creating valid words" do
        it "should create valid words smoothly" do
          @good_words.each do |word|
            lambda { Word.new(word[:value], word[:translation], word[:hint])}.should_not raise_error
          end
        end
      end

      context "creating invalid words" do
        it "should raise an exception when invalid parameters are specified" do
          @bad_words.each do |word|
            lambda { Word.new(word[:value], word[:translation], word[:hint])}.should raise_error
          end
        end
      end

    end
  end
end
