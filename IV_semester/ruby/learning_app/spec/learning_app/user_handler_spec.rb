require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem

  describe UserHandler do
    include UserSamples

    before(:each) do
      @good_users = good_users
      @bad_users = bad_users
      @user_handler = UserHandler.new([])
    end

    describe UserHandler, " registration" do 
      describe UserHandler, " registering correct users" do
        it "should register users" do
          @good_users.each do |good_user|
            lambda { @user_handler.register(good_user) }.should_not raise_error
          end

        end

        it "should return string after succesfull registration" do
          @good_users.each do |good_user|
            message = @user_handler.register(good_user)
            message.should =~ /User (.*) has been succesfully registered!/
          end
        end
      end

      describe UserHandler, " registering incorrect users" do
         it "should raise an exception" do
          @bad_users.each do |bad_user|
            lambda { @user_handler.register(bad_user) }.should raise_error
          end
         end
      end

      describe UserHandler, " registering duplicate users" do
        it "should raise an exception with specific message" do
          @user_handler.register(@good_users[0]) 
          lambda { @user_handler.register(@good_users[0]) }.should raise_error(
            /User name (.*) is already taken/
          )
        end
      end

      describe UserHandler, " deleting accounts" do
        it "should delete user accounts" do
          @good_users.each do |good_user|
            @user_handler.register(good_user)
          end

          @good_users[0].pass = Digest::SHA1.hexdigest(@good_users[0].pass)
          lambda {
            @user_handler.delete(@good_users[0])
          }.should change{ @user_handler.users.size }.from(@good_users.size).to(@good_users.size - 1)
        end
      end

    end
    
    describe UserHandler, " managing login" do
      it "should reject wrong logins" do 
        @bad_users.each do |bad_user| 
          lambda { bad_user = @user_handler.login(bad_user) }.should raise_error
          bad_user.should_not be_logged_in
        end
      end

      it "should accept correct logins" do
        @good_users.each do |good_user|
          @user_handler.register(good_user)
          lambda { good_user = @user_handler.login(good_user) }.should_not raise_error
          good_user.should be_logged_in
        end
      end
    end

    describe UserHandler, " managing logout" do
      before(:each) do
        @good_users.each {|good_user| @user_handler.register(good_user) }
        @good_users_v2 = @good_users.map {|good_user| @user_handler.login(good_user) }
      end

      it "should let users to logout" do
        @good_users_v2.each do |good_user|
          good_user.should be_logged_in

          @user_handler.logout(good_user)
          good_user.should_not be_logged_in
        end
      end

      it "should notify users with a string after logout" do
        @good_users_v2.each do |good_user|
          message = @user_handler.logout(good_user)
          message.should =~ /User (.*) has been successfully logged out/
        end
      end
    end
  end
end
