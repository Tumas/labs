require 'spec_helper'

describe 'sessions/new.html' do
  it "should render a form to register a user" do
    render 'sessions/new.html.haml'
    response.should have_tag('form[method=?][action=?]', 'post', session_path) do |form|
      form.should have_tag('input[type=text][name=?]', 'login')
      form.should have_tag('input[type=password][name=?]', 'password')
      form.should have_tag('input[type=submit]')
    end
  end

  it "should render register link" do
    render 'sessions/new.html.haml'
    response.should have_tag('a[href=?]', '/register')
  end
end
