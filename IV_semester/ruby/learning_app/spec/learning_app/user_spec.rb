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
        @user = User.new("Tumas", "Secr3tpass")
      end

      it "should create tests" do
        @user.should have(0).tests
        @user.add_test(Test.new("New Test"))
        @user.add_test(Test.new("New other test"))
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
    end

    context "Managing quizzes" do
      before(:each) do
        @user = User.new("Tumas", "S3cr3tpass")
      end

      it "should create quizzes" do
        @user.should have(0).quizzes
        @user.add_quiz(Quiz.new("New Quiz"))
        @user.add_quiz(Quiz.new("New other quiz"))
        @user.should have(2).quizzes
      end
      
      it "should reference quizzes" do
        @user.add_quiz(Quiz.new("New Quiz"))
        @user.quiz("New Quiz").should be_instance_of(Quiz)
      end

      it "should remove quizzes" do
        @user.add_quiz(Quiz.new("New Quiz"))
        @user.remove_quiz("New Quiz")
        @user.should have(0).quizzes
      end
    end

    context "Taking tests and quizes" do
      before(:each) do
        @user = User.new("Tumas", "S3cr3tpass")
        @good_word_smaples = good_word_samples
      end

      context "taking quizzes" do
        before(:each) do
          @quiz_name = "Test quiz"
          @user.add_quiz(@quiz_name)
          @good_word_samples.each do |word_sample|
            @user.quiz(@quiz_name).add_word(Word.new(word_sample[:word], word_sample[:translation], word_sample[:hint]))
          end
        end
      end
    end

  end
end
