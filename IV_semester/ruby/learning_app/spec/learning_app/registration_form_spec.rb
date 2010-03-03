require File.join(File.dirname(__FILE__), '/../spec_helper') 

module LearningSystem
  describe RegistrationForm do
    context "Registration"  do
      before(:each) do
        @rf = RegistrationForm.new
      end

      it "should be empty" do
        @rf.should be_empty
      end
    end
  end
end
