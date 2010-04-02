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

      it "should raise an exception if we add word with the same value" do
        sample =  @good_word_samples[0]  
        @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
        lambda { @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint])) }.should raise_error
      end

      it "should remove words" do
        @good_word_samples.each do |sample|
          @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint])) 
        end

        @user.each_word {|word| @user.remove_word(word)}
        @user.should have(0).words
      end

      context "finding words" do
        before(:each) do
          @words_to_find = []
          @good_word_samples.each do |sample|
            w = Word.new(sample[:value], sample[:translation], sample[:hint])
            
            @user.add_word(w)
            @words_to_find << w
          end
        end

        it "should find words by value" do
          @words_to_find.each do |w|
            @user.word({ :value => w.value }).should === w
          end
        end

        it "should find words by translation" do
          @words_to_find.each do |w|
            @user.word({ :translation => w.translation }).should === w
          end
        end

        it "should find words by value and by translation" do
          @words_to_find.each do |w|
            @user.word({ :value => w.value, :translation => w.translation }).should === w
          end
        end

        it "should return nil if word was not found" do
          sample =  bad_word_samples[0]  

          @user.word({ :value => sample[:value]}).should be_nil
          @user.word({ :translation => sample[:translation]}).should be_nil
          @user.word({ :value => sample[:value], :translation => sample[:translation]}).should be_nil
        end
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
  end
end
