require 'spec_helper'

describe UsersController do

  # Registration
  describe UsersController, " POST create" do
    context "when submitted parameters are valid" do
      fixtures :users

      before do
        User.any_instance.stubs(:valid?).returns(true)
        @user = users(:john)
      end

      it "should redirect to users_path with a notice on succesful creation" do
        post 'create'
        flash[:error].should be_nil
        assigns[:user].should_not be_new_record
        response.should redirect_to(user_path(assigns[:user].id))
      end

      it "should save new user" do
        User.expects(:new).with(nil).returns(@user)
        @user.stubs(:already_registered?).returns(false)
        @user.expects(:save)
        post :create
      end

      it "should not save new user when it already exists" do
        User.expects(:new).with(nil).returns(@user)
        @user.stubs(:already_registered?).returns(true)
        @user.expects(:save).never
        post :create
      end
    end

    it "should re-render its template if user validation fails" do
      User.any_instance.stubs(:valid?).returns(false)
      post 'create'
      response.should render_template('new')
    end
  end

end
