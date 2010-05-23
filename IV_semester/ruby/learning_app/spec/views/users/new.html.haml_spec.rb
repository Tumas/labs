require 'spec_helper'

describe 'users/new.html' do
  it "should render a form to register a user" do
    render 'users/new.html.haml'
    response.should have_tag('form[method=?][action=?]', 'post', users_path) do |form|
      form.should have_tag('input[type=text][name=?]', 'user[name]')
      form.should have_tag('input[type=password][name=?]', 'user[password]')
      form.should have_tag('input[type=submit]')
    end
  end

  it "should render login link" do
    render 'users/new.html.haml'
    response.should have_tag('a[href=?]', '/login') 
  end
end
