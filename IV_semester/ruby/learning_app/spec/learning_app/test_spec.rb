require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Test do
    include WordSamples

    before(:each) do
      @user = User.new("Tumas", "secr3tPass")
      good_word_samples.each do |sample|
        @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
      end
    end

    context "manipulating tests" do
      it "should let users to add created tests and reference them by name" do 
        @user.add_test(Test.new("Sample test"))
        @user.test("Sample test").should be_instance_of(Test)
      end

      it "should raise error when creating test without names" do
        [nil, "  "].each do |bad_name|
          lambda { @user.add_test(Test.new(bad_name)) }.should raise_error
        end
      end
    end

    context "editing" do
      it "should let user add words" do
        @user.add_test(Test.new("Sample test"))
        @user.each_word {|word| @user.test("Sample test").add_word(word) }
        @user.test("Sample test").should have(@user.words.size).words
      end

      it "should let remove words" do
        # overrides previously defined test
        @user.add_test(Test.new("Sample test"))
        @user.each_word {|word| @user.test("Sample test").add_word(word) }
        @user.each_word {|word| @user.test("Sample test").remove_word(word) }
        @user.test("Sample test").should have(0).words
      end
    end
  end
end
