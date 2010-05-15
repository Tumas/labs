class UsersController < ApplicationController
  def new
    @user = User.new
  end

  def create
    @user = User.new(params[:user])

    if !@user.valid?
      @user.errors.each_full {|error| flash[:error] += error + '\n' }
      render 'users/new'
    elsif @user.already_registered?
      flash[:error] = "User name #{@user.name} taken"
      render 'users/new'
    else
      @user.save
      flash[:message] = "User #{@user.name} succesfully created"
      redirect_to(user_path(@user))
    end
  end

  def show
  end
end
