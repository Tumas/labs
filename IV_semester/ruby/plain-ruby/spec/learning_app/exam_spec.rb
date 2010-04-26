require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Exam do
    include WordSamples

    #Spec::Runner.configure do |config|
    #  config.include(Matchers)
    #end

    describe Exam, " creating" do
      it "should not be created with empty name" do
        [nil, "  ", ""].each do |bad_name|
          lambda { Exam.new(bad_name) }.should raise_error
        end
      end
    end

    describe " taking" do
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

      # maybe a custom matcher here?
      it "should return a float number between 0 and 1 with maximum two decimal places as score" do
        # we need to get say 0.33
        @e2 = Exam.new("test exam")
        [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ].each {|s| @e2.add_word(Word.new(s[:value], s[:translation], s[:hint])) }
        
        i = 1
        @e2.take( Proc.new {|w, a| w.guess(:value => a)} ) do |word|
          i += 1
          if i % 3 == 0
            "some wrong answer"
          else
            word.value
          end
        end.should be_valid_format
      end

      describe Exam, " counting statistics" do
        it "should know how many times it was taken" do
          lambda { 
            @e.take( Proc.new {|w, a| w.guess(:value => a)} ) {|w| w.value }
          }.should change { @e.times_taken }.from(0).to(1)
        end

        it "should track its scoring history" do
          lambda { 
            @score = @e.take(Proc.new {|w, a| w.guess(:value => a)}) {|w| w.value }
          }.should change { @e.history.size }.from(0).to(1)

          @e.history.first.should == @score
        end

        it "should reset its scoring history" do
          10.times { @e.take(Proc.new {|w, a| w.guess(:value => a)}) {|w| w.value } }
          lambda { @e.history_reset }.should change { @e.history.size }.from(10).to(0)
        end

        it "should know its average score rating" do
          lambda {
            @e.take(Proc.new {|w, a| w.guess(:value => a)}) {|w| w.value }
          }.should change {@e.average_score }.from(0).to(1)

          lambda {
            @e.take(Proc.new {|w, a| w.guess(:translation => a)}) {|w| w.value }
          }.should change {@e.average_score }.from(1).to(0.5)
        end
        
        it "should return its average score in valid format" do
          @e.take(Proc.new {|w, a| w.guess(:value => a)}) {|w| w.value }
          17.times { @e.take(Proc.new {|w, a| w.guess(:translation => a)}) {|w| w.value } }

          @e.average_score.should be_valid_format
        end
      end
    end
  end
end
