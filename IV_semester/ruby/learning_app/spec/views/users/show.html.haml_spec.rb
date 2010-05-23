require 'spec_helper'

describe 'users/show.html' do
  fixtures :users

  before do
    assigns[:user] = users(:john)
  end

  context "displaying notifications" do
    it "should display messages" do
      render 'users/show.html.haml'
      response.should have_tag('div.messages')
    end

    it "should render errors" do
      render 'users/show.html.haml'
      response.should have_tag('div.errors')
    end
  end

  it "should display users words" do
    render 'users/show.html.haml'
    response.should have_tag('div.words')
  end

  it "should display users exams" do
    render 'users/show.html.haml'
    response.should have_tag('div.exams')
  end

  # better ways?
  it "should let users to logout" do
    render 'users/show.html.haml'
    response.should have_tag('a', 'Logout')
  end
end
