require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe User do
    include UserSamples
    include WordSamples

    before(:each) do
      @good_users = good_users
      @bad_users = bad_users
    end

    describe User, " validation" do
      it "should pass validation " do
        @good_users.each {|good_user| good_user.should be_valid }
      end

      it "should not pass validation" do
        @bad_users.each {|bad_user| bad_user.should_not be_valid }
      end
    end

    describe User, " managing words" do
      before(:each) do
        @good_word_samples = good_word_samples
        @user = @good_users[0]
      end

      it "should register new words" do
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

      describe User, " finding words" do
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

    describe User, " managing exams" do
      before(:each) do
        @user = User.new("Tumas", "Secr3tpass")
      end

      it "should create exams" do
        lambda do
          @user.add_exam(Exam.new("Sample exam"))
          @user.add_exam(Exam.new("Sample exam 2"))
        end.should change { @user.exams.size }.from(0).to(2)
      end

      it "should not create exams with the same name by default" do
        @user.add_exam(Exam.new("Sample exam"))
        lambda { @user.add_exam(Exam.new("Sample exam")) }.should raise_error
      end

      it "should create exams with the same name when specifying overwrite option" do
        @user.add_exam(Exam.new("Sample exam"))
        lambda { @user.add_exam(Exam.new("Sample exam"), :overwrite => true ) }.should_not raise_error
      end

      describe User, " removing exams" do
        before(:each) do
          @user.add_exam(Exam.new("Sample exam"))
        end

        it "should remove exam by title" do
          lambda { @user.remove_exam("Sample exam") }.should change { @user.exams.size }.from(1).to(0)
        end

        it "should get nil when trying to remove non existant exam" do
          @user.remove_exam("Some random exam").should be_nil
        end

        it "should get exam object when it is removed" do
          @user.remove_exam("Sample exam").should be_instance_of(Exam)
        end
      end

      describe User, " finding one exam" do
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
    end

    describe User, " iterating over items" do
      before(:each) do
        @good_word_samples = good_word_samples
        @user = @good_users[0]
      end

      it "should iterate over its all exams" do
        @user.add_exam(Exam.new("ein"))
        @user.add_exam(Exam.new("zwei"))
        @user.add_exam(Exam.new("drei"))

        @user.each_exam.should iterate_over_all_items_of(@user.exams) 
      end

      it "should iterate over all  words" do
        @good_word_samples.each do |sample|
          @user.add_word(Word.new(sample[:value], sample[:translation], sample[:hint])) 
        end

        @user.each_word.should iterate_over_all_items_of(@user.words)
      end
    end
  end
end
