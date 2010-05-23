class UsersController < ApplicationController
  skip_before_filter :authorize, :only => [:new, :create]

  def create
    @user = User.new(params[:user])

    if !@user.valid?
      flash[:error] = ""
      @user.errors.each_full {|error| flash[:error] += error + ';' }
      render 'users/new'
    elsif @user.registered?
      flash.now[:error] = "User name #{@user.name} taken"
      render 'users/new'
    else
      @user.register
      flash[:message] = "User #{@user.name} succesfully created"
      session[:user_id] = @user.id
      redirect_to(user_path(@user))
    end
  end

  def destroy
    if params[:id]
      User.delete(params[:id])
      redirect_to root_url
    end
  end

  def show
    current_user
  end

  def edit
  end

  def update
    current_user

    if params[:user][:password]
      params[:user][:password] = User.digest(params[:user][:password])
    end

    if @user.update_attributes(params[:user])
      flash[:message] = "User #{@user.name} succesfully updated"
      redirect_to user_path(@user)
    else
      flash[:error] = "Update failed."
      render 'edit'
    end
  end
end
