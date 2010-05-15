require File.dirname(__FILE__) + '/../spec_helper'

describe User do
  fixtures :users

  before do
    @user = User.find_by_name('John')
  end

  describe User, " validation" do
    # remarkable_rails plugin for rails validations testing
    it { should validate_presence_of :name }
    it { should validate_length_of :name,  :maximum => 20, :minimum => 3 }
    it { should validate_length_of :password,  :maximum => 50, :minimum => 6 }

    # remarkable_rails for association testing
    it { should have_many :words }
    it { should have_many :exams }
    it { should have_many :guesses }
    it { should have_many :scores }
    it { should have_and_belong_to_many :tags }
  end

  describe User, "registration " do
    it "should check if user.name is already taken" do
      User.new(:name => 'John').should be_already_registered
    end
  end

  describe User, " managing words" do
    fixtures :words
    before do
      @new_word = Word.new(:value => 'X', :translation => 'Y')
    end

    it "should register new words" do
      words_size = @user.words.size
      
      lambda {
        @user.add_word(@new_word)
      }.should change { #@user.words.size
        User.find_by_name(@user.name).words(true).size
      }.from(words_size).to(words_size + 1)
    end

    it "should raise an exception when registering word with the same value" do
      @user.add_word(@new_word)
      lambda {
        @user.add_word(Word.new(:value => @new_word.value, :translation => "Y"))
      }.should raise_error 
    end

    it "should not raise an exception when registering word with the same value and the overwrite option is set" do
      @user.add_word(@new_word)
      lambda {
        @user.add_word(Word.new(:value => @new_word.value, :translation => "Y"), :overwrite => true)
      }.should_not raise_error 
    end

    it "should remove from its words" do
      words_size = @user.words.size
      
      lambda {
        @user.remove_word(@user.words.first)
      }.should change {
        #@user.words.size 
        User.find_by_name(@user.name).words(true).size
        }.from(words_size).to(words_size - 1)
    end

    it "should remove words from the database" do
      words_size = Word.all.size
      
      lambda {
        @user.remove_word(@user.words.first)
      }.should change {
        Word.all(true).size
        }.from(words_size).to(words_size - 1)
    end
  end

  describe User, " finding words" do
    fixtures :words

    it "should find words by value" do
      @user.words.find_each do |w|
        @user.word(:value => w.value).should === w
      end
    end

    it "should find words by translation" do
      @user.words.find_each do |w|
        @user.word(:translation => w.translation).should === w
      end
    end

    it "should find words by value and translation" do
      @user.words.find_each do |w|
        @user.word(opts = { :value => w.value, :translation => w.translation }).should === w
      end
    end

    it "should return nil if word wasn't found" do
      @user.word(opts = { :value => "x2384783", :translation => "asdjkl"}).should be_nil
    end
  end

  describe User, " managing exams" do
    fixtures :exams

    before do
      @exam = Exam.new(:title => "X")
    end

    it "should create exams" do
      exams_size = @user.exams(true).size
      
      lambda { @user.add_exam(@exam) }.should
      change { User.find_by_name(@user.name).exams(true).size }.from(exams_size).to(exams_size + 1)
    end

    it "should raise an exception when creating exam with the same name" do
      @user.add_exam(@exam)
      lambda { @user.add_exam(Exam.new(:title => @exam.title)) }.should raise_error
    end

    it "should not raise an exception when creating exam with the same name and the overwrite option is set" do
      @user.add_exam(@exam)
      lambda { @user.add_exam(Exam.new(:title => @exam.title), :overwrite => true) }.should_not raise_error
    end

    it "should remove exams" do
      @user.add_exam(@exam)
      exams_size = @user.exams(true).size
      
      lambda { @user.remove_exam(@user.exams.first) }.should change { 
        User.find_by_name(@user.name).exams(true).size
      }.from(exams_size).to(exams_size - 1)
    end

    describe User, " finding exam" do
      fixtures :exams

      it "should find exam by title" do
        @user.add_exam(@exam)
        @user.exam(:title => @exam.title).should === @exam
      end

      it "should return nil if exam wasn't found" do
        @user.exam(:title => "some_random_name_that_should_not_exist").should be_nil
      end
    end
  end
end
