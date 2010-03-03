require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem

  describe UserHandler do
    context "Registration" do 
      before(:each) do
        @user1 = User.new('Tom', 'NotS3cure')
        @user2 = User.new('Pete', 'PeteMachalkin')
        @user3 = User.new('Pete', '')
        @user4 = User.new('', '')
        @user5 = User.new('', 'b43')
      end

      context "registering correct users" do
        it "should register users" do
          lambda { UserHandler.new.register(@user1) }.should_not raise_error
          lambda { UserHandler.new.register(@user2) }.should_not raise_error
        end

        it "should provide a meaningful string after succesfull registration" do
          message = UserHandler.new.register(@user1)
          message.should =~ /User (.*) has been succesfully registered!/
          message = UserHandler.new.register(@user2)
          message.should =~ /User (.*) has been succesfully registered!/
        end
      end

      context "registering incorrect users" do
         it "should raise an exception" do
          lambda { UserHandler.new.register(@user3) }.should raise_error
          lambda { UserHandler.new.register(@user4) }.should raise_error
          lambda { UserHandler.new.register(@user5) }.should raise_error
          lambda { UserHandler.new.register(nil) }.should raise_error
        end
      end

    end
  end
end
