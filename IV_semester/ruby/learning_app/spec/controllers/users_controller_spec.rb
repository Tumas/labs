require 'spec_helper'

describe UsersController do
  fixtures :users

  describe UsersController, " GET new " do
    it "should route from /register " do
      params_from(:get, "/register").should == {:controller => "users", :action => 'new'}
    end
  end

  # Registration
  describe UsersController, " POST create" do
    context "when submitted parameters are valid" do

      before do
        User.any_instance.stubs(:valid?).returns(true)
      end

      it "should redirect to user_path with a notice on succesful creation " do
        post 'create'
        flash[:error].should be_nil
        assigns[:user].should_not be_new_record
        session[:user_id].should == assigns[:user].id
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

    it "should redirect to home page" do
      delete "destroy", :id => users(:john).id
      response.should redirect_to root_url
    end

    it "should delete the user" do 
      controller.stubs(:current_user).returns(users(:john))
      User.expects(:delete).with(users(:john).id.to_s)
      delete "destroy", :id => users(:john).id
    end
  end

  describe UsersController, " GET show" do
    #it { should require_login_on { :method => 'get', :action => 'show' } }

    it "should assign authorized user to @user" do
      controller.stubs(:current_user).returns(users(:john))
      get :show, :id => users(:john).id
      assigns[:user].should == users(:john)
    end
  end

  describe UsersController, "GET edit" do

    it "should assign requested user to @user" do
      controller.stubs(:current_user).returns(users(:john))
      get :edit, :id => users(:john).id
      assigns[:user].should == users(:john)
    end
  end

  describe UsersController, "PUT update" do

    before do
      controller.stubs(:current_user).returns(users(:john))
    end

    it "should re-render its template if new values are incorrect" do
      put :update, :id => users(:john).id, :user => { :name => "", :password => "" }
      flash[:error].should_not be_nil
      response.should render_template('edit')
      assigns[:user].should == users(:john)
    end

    it "should redirect to user_path when new values are correct" do
      users(:john).expects(:save).returns(true)
      put :update, :id => users(:john).id, :user => { :password => "newPassword" }
      flash[:message].should_not be_nil
      response.should redirect_to user_path(users(:john))
    end
  end
end
