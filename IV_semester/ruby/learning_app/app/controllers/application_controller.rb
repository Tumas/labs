# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  protect_from_forgery # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  filter_parameter_logging :password

  before_filter :authorize

  def current_user
    @user ||= User.find(session[:user_id]) unless session[:user_id].nil?
  end

  protected
    def authorize
      @user = current_user
      if @user.nil?
        flash[:error] = "Login required"
        redirect_to root_url 
      end
    end
end
