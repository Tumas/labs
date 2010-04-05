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

      it "should not raise an exception if we add word with the same value when we specify an option" do
        sample =  @good_word_samples[0]  
        @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]))
        lambda { @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint]), true) }.should_not raise_error
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

    context "Managing exams" do
      before(:each) do
        @user = User.new("Tumas", "Secr3tpass")
      end

      it "should create exams" do
        @user.should have(0).exams
        @user.add_exam(Exam.new("Sample exam"))
        @user.add_exam(Exam.new("Sample exam 2"))
        @user.should have(2).exams
      end

      it "should not create exams with the same name by default" do
        @user.add_exam(Exam.new("Sample exam"))
        lambda { @user.add_exam(Exam.new("Sample exam")) }.should raise_error
      end

      it "should create exams with the same name on purpose" do
        @user.add_exam(Exam.new("Sample exam"))
        lambda { @user.add_exam(Exam.new("Sample exam"), :overwrite => true ) }.should_not raise_error
      end

      it "should remove exams" do
        @user.add_exam(Exam.new("Sample exam"))
        @user.should have(1).exams
        @user.remove_exam("Sample exam")
        @user.should have(0).exams
      end

      context "Finding one exam" do
        it "should find exam by name" do
          test_exam = Exam.new("Sample exam")
          @user.add_exam(test_exam)
          @user.exam("Sample exam").should === test_exam
        end

        it "should get nil if exam was not found " do
          @user.add_exam(Exam.new("Sample exam"))
          @user.exam("Some random exam").should be_nil
        end
      end

      context "Finding more than one exam" do
        it "should find exams by specific word"
        it "should find exams by word count"
        it "should get empty array if exams were not found"
      end
    end

=begin
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

      it "should raise an exception when adding tests with the same name" do
        @user.add_test(Test.new("Sample test"))
        lambda { @user.add_test(Test.new("Sample test")) }.should raise_error
      end
      
      it "should not raise an exception when adding tests with the same name when special options is set" do
        @user.add_test(Test.new("Sample test"))
        lambda { @user.add_test(Test.new("Sample test"), true) }.should_not raise_error
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

      it "should raise error when adding quiz with the same name" do
        @user.add_quiz(Quiz.new("Test quiz"))
        lambda { @user.add_quiz(Quiz.new("Test quiz")) }.should raise_error
      end

      it "should not raise an exception if we add quiz with the same name when we say so (overwrite = true)" do
        @user.add_quiz(Quiz.new("Test quiz"))
        lambda { @user.add_quiz(Quiz.new("Test quiz"), true) }.should_not raise_error
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
=end
  end
end
