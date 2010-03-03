require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe UserHandler do
    before(:each) do
      @us = UserHandler.new
      @app = Application.new(@us)
    end

    context "Registration" do
      it "should receive a request for a registration form" do
        @us.should_receive(:register)
        @app.register
      end

      it "should receive a request to complete the registration" do
        @us.should_receive(:complete_registration)
        @app.complete_registration(User.new)
      end

      #context "VaWith right values"
      #context "With not right values"
    end
  end
end
