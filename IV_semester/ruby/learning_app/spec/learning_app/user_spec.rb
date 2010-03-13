require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe User do
    include UserSamples
    include WordSamples

    before(:each) do
      @good_users = good_users
      @bad_users = bad_users
    end

    context "User validation" do
      it "should be valid" do
        @good_users.each {|good_user| good_user.should be_valid }
      end

      it "should not be valid" do
        @bad_users.each {|bad_user| bad_user.should_not be_valid }
      end
    end

    context "Managing words" do
      before(:each) do
        @good_word_samples = good_word_samples
        @user = @good_users[0]
      end

      it "should be able to add new words" do
          @good_word_samples.each do |sample|
            @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
          end
          @user.should have(@good_word_samples.size).words
      end

      it "should be able to remove words" do
        @good_word_samples.each do |sample|
          @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint])) 
        end

        @user.each_word {|word| @user.remove_word(word)}
        @user.should have(0).words
      end
    end

  end
end
