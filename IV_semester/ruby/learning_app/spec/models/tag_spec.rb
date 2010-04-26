require File.dirname(__FILE__) + '/../spec_helper'

describe Tag do
  describe Tag, " creating" do
    before(:each) do
      @valid_attributes = {
        :title => "tag",
      }
    end

    it "should have a title" do
      Tag.new(@valid_attributes.except(:title)).should_not be_valid
    end
  end
end
