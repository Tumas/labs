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
        @good_word_samples = [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]
      end

      it "should be able to register a new word" do
        @good_users.each do |user|
          @good_word_samples.each do |sample|
            user.create_word(sample[:value], sample[:translation], sample[:hint])
          end
          user.should have(@good_word_samples.size).words
        end
      end
    end

    context "deleting words" do
      before(:each) do
        @good_word_samples = [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]
      end

      it "should be able to remove words" do
        @good_word_samples.each { |sample| @good_users[0].create_word(sample[:value], sample[:translation], sample[:hint]) }
        @good_users[0].delete_word(@good_users[0].words[0])
        @good_users[0].should have(@good_word_samples.size - 1).words
      end
    end
  end
end
