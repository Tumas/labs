require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem

  describe UserHandler do

    context "Registration" do 
      before(:each) do
      @good_users = [ 
        User.new("tom", "b4dp4ss"),
        User.new('Tomm', 'NotS3cure'),
      ]

      @bad_users = [
        User.new('Pete', ''),
        User.new('', ''),
        User.new('', 'b43'),
        User.new('Timothy', 'Withwaytolooooooooooongpassword'),
        nil,
      ]
      end

      after(:each) do
        UserHandler.delete_users
      end

      context "registering correct users" do
        it "should register users" do
          @good_users.each do |good_user|
            lambda { UserHandler.new.register(good_user) }.should_not raise_error
          end

        end

        it "should provide a meaningful string after succesfull registration" do
          @good_users.each do |good_user|
            message = UserHandler.new.register(good_user)
            message.should =~ /User (.*) has been succesfully registered!/
          end
        end
      end

      context "registering incorrect users" do
         it "should raise an exception" do
          @bad_users.each do |bad_user|
            lambda { UserHandler.new.register(bad_user) }.should raise_error
          end
        end
      end

      context "registering duplicate users" do
        it "should raise an exception with specific message" do
          UserHandler.new.register(@good_users[0]) 
          lambda { UserHandler.new.register(@good_users[0]) }.should raise_error (
            /User name (.*) is already taken/
          )
        end
      end
    end
  end
end
