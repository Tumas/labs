require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem

  describe User do
    before(:each) do
      @user = User.new
    end

    context "unregistered" do
      it "should not be registered" do
        @user.should_not be_registered
      end
    end
  end
end
