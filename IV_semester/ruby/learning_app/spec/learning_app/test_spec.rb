require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe Test do
    before(:each) do
      valid_word_samples = [
        { :value => "spouse", :translation => "a person's partner in marriage" },
        { :value => "audacity", :translation => "fearless daring" },
        { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
      ]

      @user = User.new("Tumas", "secr3tPass")

      valid_word_samples.each do |sample|
        @user.create_word(sample[:value], sample[:translation], sample[:hint])
      end
    
      #@user.tests = []
    end

    context "manipulating tests" do
      it "should let users to create tests and reference them by name" do 
        @user.create_test("Sample test")
        @user.test("Sample test").should be_instance_of(Test)
      end

      it "should raise error when creating test without names" do
        [nil, "  "].each do |bad_name|
          lambda { @user.create_test(bad_name) }.should raise_error
        end
      end
    end

    context "editing" do
      it "should let user add words" do
        @user.create_test("Sample test")
        @user.words.each {|word| @user.test("Sample test").add_word(word) }
        @user.test("Sample test").should have(@user.words.size).words
      end

      it "should let remove words" do
        # overrides previously defined test
        @user.create_test("Sample test")
        @user.words.each {|word| @user.test("Sample test").add_word(word) }
        @user.test("Sample test").delete_word(@user.words[0])
        @user.test("Sample test").should have(@user.words.size - 1).words
      end
    end
  end
end
