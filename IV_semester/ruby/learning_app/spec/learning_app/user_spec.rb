require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe User do

    before(:each) do
      @good_users = [ User.new("tom", "b4dp4ss") ]
      @bad_users = [
        User.new('Pete', ''),
        User.new('', ''),
        User.new('', 'b43'),
        User.new('Timothy', 'Withwaytolooooooooooongpassword'),
      ]
    end

    context "user validation" do
      it "should be valid" do
        @good_users.each {|good_user| good_user.should be_valid }
      end

      it "should not be valid" do
        @bad_users.each {|bad_user| bad_user.should_not be_valid }
      end
    end

    context "registering word" do
      before(:each) do
        @good_words = [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]
      end

      it "should be able to register a new word" do
        @good_users.each do |user|
          @good_words.each {|word| user.register_word(word) }
          user.should have(@good_words.size).words
        end
      end
    end
  end
end
