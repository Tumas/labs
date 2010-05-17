require 'spec_helper'

describe WordsController do
  fixtures :users
  fixtures :words

  before do
    controller.stubs(:current_user).returns(users(:john))
  end

  describe WordsController, "GET index" do
    it "should list all users words" do
      get :index, :user_id => users(:john).id
      response.should be_success
      assigns[:user].should == users(:john)
      assigns[:words].should == users(:john).words
    end

    it "should list empty array when there are no words" do
      Word.destroy_all
      get :index, :user_id => users(:john).id
      assigns[:words].should == [] 
    end
  end

  describe WordsController, "POST create" do
    it "should re-render its template with invalid params" do
      Word.expects(:new).with(nil).returns(words(:spouse))
      words(:spouse).stubs(:save).returns(false)
      post :create, :user_id => users(:john).id
      flash[:error].should_not be_nil
      response.should render_template('new')
    end

    it "should create new word with valid params" do
      Word.expects(:new).with(nil).returns(words(:spouse))
      words(:spouse).stubs(:save).returns(true)
      post :create, :user_id => users(:john).id
      flash[:message].should_not be_nil
      response.should redirect_to(user_words_url)
    end

    it "should add word to @user.words" do
      post :create, :user_id => users(:john).id
      assigns[:user].should == users(:john)
      assigns[:word].user_id.should == users(:john).id
    end
  end

  describe WordsController, "DELETE destroy" do
    it "should delete word" do
      Word.expects(:delete).with(words(:spouse).id.to_s)
      delete :destroy, :user_id => users(:john).id, :id => words(:spouse).id
    end

    it "should redirect to words index" do
      delete :destroy, :user_id => users(:john).id, :id => words(:spouse)
      response.should redirect_to root_url
    end
  end
end
