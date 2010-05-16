class SessionsController < ApplicationController
  def create
    user = User.authenticate(params[:login], params[:password])
    if user
      session[:user_id] = user.id
      flash[:message] = "User #{user.name} sucessuflly logged in"
      redirect_to user_path(user.id)
    else
      flash[:error] = "Invalid login"
      render :action => 'new'
    end
  end

  def destroy
    reset_session
    redirect_to('/')
  end
end
