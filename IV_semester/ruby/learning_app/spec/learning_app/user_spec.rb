require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe User do

    before(:each) do
      @good_users = [ User.new("tom", "b4dp4ss") ]
      @bad_users = [
        User.new('Pete', ''),
        User.new('', ''),
        User.new('', 'b43'),
        User.new('Timothy', 'Withwaytolooooooooooongpassword'),
      ]
    end

    context "new user validation" do
      it "should be valid" do
        @good_users.each {|good_user| good_user.should be_valid }
      end

      it "should not be valid" do
        @bad_users.each {|bad_user| bad_user.should_not be_valid }
      end

      #it "should validate itself correctly" do
      #  validate = lambda {|user| user.name && user.pass && user.name.length >= 3 && user.pass.length >= 6 && user.pass.length < 15 }
      #  @good_users.concat(@bad_users).each { |user| user.valid?.should == validate.call(user) }
      #end

    end
  end
end
