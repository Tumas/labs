class UsersController < ApplicationController
  def create
    @user = User.new(params[:user])

    if !@user.valid?
      @user.errors.each_full {|error| flash[:error] += error + '\n' }
      render 'users/new'
    elsif @user.registered?
      flash[:error] = "User name #{@user.name} taken"
      render 'users/new'
    else
      @user.register
      flash[:message] = "User #{@user.name} succesfully created"
      redirect_to(user_path(@user))
    end
  end

  def destroy
    if params[:id] and params[:id].to_i
      User.delete(params[:id].to_i)
      redirect_to('/')
    end
  end
end
