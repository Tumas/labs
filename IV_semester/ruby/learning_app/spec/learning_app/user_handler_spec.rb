require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem

  describe UserHandler do

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
    ]

    @user_handler = UserHandler.new
    end

    after(:each) do
      UserHandler.delete_users
    end

    context "Registration" do 
      context "registering correct users" do
        it "should register users" do
          @good_users.each do |good_user|
            lambda { @user_handler.register(good_user) }.should_not raise_error
          end

        end

        it "should provide a meaningful string after succesfull registration" do
          @good_users.each do |good_user|
            message = @user_handler.register(good_user)
            message.should =~ /User (.*) has been succesfully registered!/
          end
        end
      end

      context "registering incorrect users" do
         it "should raise an exception" do
          @bad_users.each do |bad_user|
            lambda { @user_handler.register(bad_user) }.should raise_error
          end
        end
      end

      context "registering duplicate users" do
        it "should raise an exception with specific message" do
          @user_handler.register(@good_users[0]) 
          lambda { @user_handler.register(@good_users[0]) }.should raise_error (
            /User name (.*) is already taken/
          )
        end
      end
    end
    
    context "Login" do
      context "incorrect logins" do
        it "should reject wrong logins" do 
          @bad_users.each do |bad_user| 
            lambda { bad_user = @user_handler.login(bad_user) }.should raise_error
            bad_user.should_not be_logged_in
          end
        end
      end

      context "correct logins" do
        it "should accept correct logins" do
          @good_users.each do |good_user|
            @user_handler.register(good_user)
            lambda { good_user = @user_handler.login(good_user) }.should_not raise_error
            good_user.should be_logged_in
          end
        end
      end
    end

    context "Logout" do
      before(:each) do
        @good_users.each {|good_user| @user_handler.register(good_user) }
        @good_users = @good_users.map {|good_user| @user_handler.login(good_user) }
      end

      it "should let users to logout" do
        @good_users.each do |good_user|
          good_user.should be_logged_in

          @user_handler.logout(good_user)
          good_user.should_not be_logged_in
        end
      end

      it "should notify users with a string after logout" do
        @good_users.each do |good_user|
          message = @user_handler.logout(good_user)
          message.should =~ /User (.*) has been successfully logged out/
        end
      end
    end
  end
end
