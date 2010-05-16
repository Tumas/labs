require File.dirname(__FILE__) + '/../spec_helper'

describe User do
  fixtures :users

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

  describe User, "registration and authentication" do
    it "should check if user.name is already taken" do
      User.new(:name => 'John').should be_registered
    end

    it "should register new users" do
      users(:john).expects(:save)
      users(:john).register
    end

    it "should hash user password when registering" do
      users(:john).expects(:password=).with(User.digest(users(:john).password))
      users(:john).register
    end

    it "should return user when authenticated" do
      old_pass = users(:john).password
      users(:john).register
      User.authenticate(users(:john).name, old_pass).should === users(:john)
    end

    it "should return nil when not authenticated" do
      User.authenticate(users(:john).password, users(:john).password).should be_nil
    end
  end

  describe User, " managing words" do
    fixtures :words

    it "should register new words" do
      words_size = users(:john).words.size
      
      lambda { users(:john).add_word(words(:jam)) }.should change {
        users(:john).words(true).size
      }.from(words_size).to(words_size + 1)
    end

    it "should raise an exception when registering word with the same value " do
      users(:john).add_word(words(:jam))
      lambda { users(:john).add_word(words(:jam)) }.should raise_error 
    end

    it "should not raise an exception when registering word with the same value and the overwrite option is set" do
      users(:john).add_word(words(:jam))
      lambda { users(:john).add_word(words(:jam), :overwrite => true) }.should_not raise_error 
    end

    it "should remove word" do
     Word.expects(:delete).with(words(:spouse).id)
     users(:john).remove_word(words(:spouse))
    end
  end

  describe User, " finding words" do
    fixtures :words

    it "should find words by value" do
      users(:john).words.find_each do |w|
        users(:john).word(:value => w.value).should === w
      end
    end

    it "should find words by translation" do
      users(:john).words.find_each do |w|
        users(:john).word(:translation => w.translation).should === w
      end
    end

    it "should find words by value and translation" do
      users(:john).words.find_each do |w|
        users(:john).word(opts = { :value => w.value, :translation => w.translation }).should === w
      end
    end

    it "should return nil if word wasn't found" do
      users(:john).word(opts = { :value => "x2384783", :translation => "asdjkl"}).should be_nil
    end
  end

  describe User, " managing exams" do
    fixtures :exams

    it "should create exams" do
      exams_size = users(:john).exams(true).size
      
      lambda { users(:john).add_exam(exams(:no_owner)) }.should
      change { users(:john).exams(true).size }.from(exams_size).to(exams_size + 1)
    end

    it "should raise an exception when creating exam with the same name" do
      users(:john).add_exam(exams(:no_owner))
      lambda { users(:john).add_exam(exams(:no_owner)) }.should raise_error
    end

    it "should not raise an exception when creating exam with the same name and the overwrite option is set" do
      users(:john).add_exam(exams(:no_owner))
      lambda { users(:john).add_exam(exams(:no_owner), :overwrite => true) }.should_not raise_error
    end

    it "should remove exams" do
      exams_size = users(:john).exams(true).size
      
      lambda { users(:john).remove_exam(exams(:one)) }.should change { 
        users(:john).exams(true).size
      }.from(exams_size).to(exams_size - 1)
    end

    describe User, " finding exam" do
      fixtures :exams

      it "should find exam by title" do
        users(:john).add_exam(exams(:no_owner))
        users(:john).exam(:title => exams(:no_owner).title).should === exams(:no_owner)
      end

      it "should return nil if exam wasn't found" do
        users(:john).exam(:title => "some_random_name_that_should_not_exist").should be_nil
      end
    end
  end
end
