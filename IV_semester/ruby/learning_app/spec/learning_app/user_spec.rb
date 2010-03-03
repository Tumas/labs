require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe User do

    context "new user validation" do
      it "should be valid" do
        user = User.new("tom", "b4dp4ss")
        user.should be_valid
      end

      it "should validate itself correctly" do
        user = User.new("tom", "b4dp4ss")
        user.valid?.should == (user.name && user.pass && user.name.length >= 3 && user.pass.length >= 6) 
      end
    end

    context "registration state" do
      it "should be unregistered" do
        pending
      end

      it "should be registered" do
        pending
      end
    end
  end
end
