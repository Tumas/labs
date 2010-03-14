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

      it "should add new words" do
          @good_word_samples.each do |sample|
            @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
          end
          @user.should have(@good_word_samples.size).words
      end

      it "should remove words" do
        @good_word_samples.each do |sample|
          @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint])) 
        end

        @user.each_word {|word| @user.remove_word(word)}
        @user.should have(0).words
      end
    end

    context "Managing Tests" do
      before(:each) do
        @good_word_samples = good_word_samples
        @user = User.new("Tumas", "Secr3tpass")

        @user.reset_tests
      end

      it "should create tests" do
        @user.should have(0).tests
        @user.add_test(Test.new("New Test"))
        @user.add_test(Test.new("New Test Other"))
        @user.should have(2).tests
      end

      it "should reference tests" do
        @user.add_test(Test.new("Sample test"))
        @user.test("Sample test").should be_instance_of(Test)
      end

      it "should remove tests" do
        @user.add_test(Test.new("New Test"))
        @user.remove_test("New Test")
        @user.should have(0).tests
      end

      it "should take tests" do
        @user.add_test(Test.new("All words test"))
        @user.take_test(@user.test("All words test"))
        @user.test("All words test").times_taken.should == 1
      end
    end

    context "Managing Quizzes" do
    end

  end
end
