require File.dirname(__FILE__) + '/../spec_helper'

describe Taggable do
  class TaggableObject
    include Taggable
  end

  # testuojam moduli
  # stubbinam klases, kurios ji includins

  before(:each) do
    @taggable_object = TaggableObject.new
    # 
    @taggable_object.stubs(:id).returns(111)
    # per fixtures
    @taggable_object.stubs(:user).returns(User.find_or_create_by_name(:name => "Joe", :password => "asdjaksjd"))

    @data = ["tag"]
    Tag.destroy_all
  end

  it "should add tags" do
    tags = Tag.all(true).size
    lambda { 
      @taggable_object.add_tags(@data)
    }.should change { Tag.all(true).size }.from(tags).to(tags + 1)
  end

  it "should set taggable_type and taggable_id on new tag when called on word" do
    @taggable_object.stubs(:class).returns(Word)
    @taggable_object.add_tags(@data)

    Tag.first.taggable_id.should == 111
    Tag.first.taggable_type.should == 'Word'
  end

  it "should set taggable_type and taggable_id on new tag when called on exam" do
    @taggable_object.stubs(:class).returns(Exam)
    @taggable_object.add_tags(@data)

    Tag.first.taggable_id.should == 111
    Tag.first.taggable_type.should == 'Exam'
  end

  it "should set user on tags when" do
    @taggable_object.stubs(:class).returns(Word)
    @taggable_object.add_tags(@data)

    Tag.first.users.should_not be_empty
  end

  it "should skip illogic tags" do
    @data = ["", nil]

    lambda { 
      @taggable_object.add_tags(@data)
    }.should change { Tag.all.size }.by(0)
  end
end
