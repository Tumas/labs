require 'spec_helper'

describe UsersController do

  describe UsersController, " GET new " do
    it "should route from /register " do
      params_from(:get, "/register").should == {:controller => "users", :action => 'new'}
    end
  end
  # Registration
  describe UsersController, " POST create" do
    context "when submitted parameters are valid" do
      fixtures :users

      before do
        User.any_instance.stubs(:valid?).returns(true)
      end

      it "should redirect to users_path with a notice on succesful creation" do
        post 'create'
        flash[:error].should be_nil
        assigns[:user].should_not be_new_record
        response.should redirect_to(user_path(assigns[:user]))
      end

      it "should register new user" do
        User.expects(:new).with(nil).returns(users(:john))
        users(:john).stubs(:registered?).returns(false)
        users(:john).expects(:register)
        post :create
      end

      it "should not register new user when it already exists" do
        User.expects(:new).with(nil).returns(users(:john))
        users(:john).stubs(:registered?).returns(true)
        users(:john).expects(:register).never
        post :create
      end
    end

    it "should re-render its template if user validation fails" do
      User.any_instance.stubs(:valid?).returns(false)
      post 'create'
      response.should render_template('new')
    end
  end

  describe UsersController, " DELETE destroy" do
    fixtures :users

    it "should redirect to home page" do
      delete "destroy", :id => users(:john).id
      response.should redirect_to('/')
    end

    it "should delete the user" do 
      User.expects(:delete).with(users(:john).id)
      delete "destroy", :id => users(:john).id
    end
  end

  # should create session if redirect to panel
  # register -> create
  # unregister -> delete
  # /users/id  -> user panel | show
  # /users/id/edit -> change password or user_name | edit => update
end
