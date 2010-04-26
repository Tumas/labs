require File.dirname(__FILE__) + '/../spec_helper'

describe User do
  fixtures :users

  before(:each) do
    @user = User.find_by_name('John')
  end

  describe User, " validation" do
    it "should have name with at least 3 and at most 20 symbols and password with at least 6 and at most 50 symbols " do
      User.find_all_by_name(['om', 'Pete']).each do |user|
        user.should_not be_valid
      end
    end
  end

  describe User, " managing words" do
    fixtures :words

    it "should register new words" do
      words_size = @user.words.size
      
      lambda {
        @user.add_word(Word.new(:value => "X", :translation => "Y"))
      }.should change { #@user.words.size
        User.find_by_name(@user.name).words(true).size
      }.from(words_size).to(words_size + 1)
    end

    it "should raise an exception when registering word with the same value" do
      @user.add_word(Word.new(:value => "X", :translation => "Y"))
      lambda {
        @user.add_word(Word.new(:value => "X", :translation => "Y"))
      }.should raise_error 
    end

    it "should not raise an exception when registering word with the same value and the overwrite option is set" do
      @user.add_word(Word.new(:value => "X", :translation => "Y"))
      lambda {
        @user.add_word(Word.new(:value => "X", :translation => "Y"), :overwrite => true)
      }.should_not raise_error 
    end

    it "should remove words" do
      words_size = @user.words.size
      
      lambda {
        @user.remove_word(@user.words.first)
      }.should change {
        #@user.words.size 
        User.find_by_name(@user.name).words(true).size
        }.from(words_size).to(words_size - 1)
    end
  end

  # Not quite needed when using ActiveRecord
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

    it "should create exams" do
      exams_size = @user.exams(true).size
      
      lambda {
        @user.add_exam(Exam.new(:title => "X"))
      }.should change { #@user.words.size
        User.find_by_name(@user.name).exams(true).size
      }.from(exams_size).to(exams_size + 1)
    end

    it "should raise an exception when creating exam with the same name" do
      @user.add_exam(Exam.new(:title => "X"))

      lambda {
        @user.add_exam(Exam.new(:title => "X"))
      }.should raise_error
    end

    it "should not raise an exception when creating exam with the same name and the overwrite option is set" do
      @user.add_exam(Exam.new(:title => "X"))

      lambda {
        @user.add_exam(Exam.new(:title => "X"), :overwrite => true)
      }.should_not raise_error
    end

    it "should remove exams by title" do
      @user.add_exam(Exam.new(:title => "X"))
      exams_size = @user.exams(true).size
      
      lambda {
        @user.remove_exam(:title => "X")
      }.should change { #@user.words.size
        User.find_by_name(@user.name).exams(true).size
      }.from(exams_size).to(exams_size - 1)
    end

    it "should return number of deleted exams when they are removed" do
      @user.add_exam(Exam.new(:title => "X"))
      @user.remove_exam(:title => "X").should == 1
    end
  end

  describe User, " finding exam" do
    fixtures :exams

    it "should find exam by title" do
      e = Exam.new(:title => "X")
      @user.add_exam(e)
      @user.exam(:title => "X").should === e
    end

    it "should return nil if exam wasn't found" do
      @user.exam(:title => "X").should be_nil
    end
  end
end
