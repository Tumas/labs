include LearningSystem

def app
  @app ||= Application.new(UserHandler.new)
end

Given /^I am not yet registered$/ do
  @current_user = User.new
  @current_user.should_not be_registered
end

When /^I press the "([^\"]*)" button$/ do |arg1|
  pending
  app.send(arg1.downcase)
end

Then /^registration form should appear$/ do
  @registration_form = app.register
end

Given /^I have pressed the "([^\"]*)" button$/ do |arg1|
  app.send(arg1.downcase)
end

Given /^I have filled all the required fields$/ do
  @registration_form.should be_filled
end

When /^I press press the "([^\"]*)" button$/ do |arg1|
  @message = app.send(arg1.downcase)
end

Then /^confirmation dialog should appear$/ do
  @message.should_not be_nil
end

Then /^it should say "([^\"]*)"$/ do |arg1|
  @message.should include(arg1)
end
