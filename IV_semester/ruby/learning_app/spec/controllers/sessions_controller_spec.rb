require 'spec_helper'

describe SessionsController do
  describe SessionsController, " GET new " do
    it "should route from /login" do
      params_from(:get, "/login").should == {:controller => "sessions", :action => 'new'}
    end
  end

  describe SessionsController, " POST create " do
    it "should re-render its template if user is not authenticated" do
      User.stubs(:authenticate).returns(nil)
      post 'create'
      response.should render_template('new')
    end

    context "when authentication succedes" do
      fixtures :users

      before do
        User.stubs(:authenticate).returns(users(:john))
      end

      it "should redirect to users/id" do
        post 'create'
        response.should redirect_to("/users/#{session[:user_id]}")
      end

      it "should set sessions[:id] to user id" do
        post 'create'
        session[:user_id].should == users(:john).id
      end

    end
  end

  describe SessionsController, " DELETE destroy" do
    it "should clear session" do
      delete 'destroy'
      session[:user_id].should be_nil
    end

    it "should redirect to index page" do
      delete 'destroy'
      response.should redirect_to('/') 
    end
  end
end
