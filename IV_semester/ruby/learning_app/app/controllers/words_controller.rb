class WordsController < ApplicationController
  def create
    @word = Word.new(params[:word])

    @user.words << @word
    if @word.save
      flash[:message] = "New word #{@word.value} was succesfully added"
      redirect_to user_words_url
    else
      flash[:error] = "Wrong arguments"
      render 'new'
    end
  end

  def destroy
    Word.delete(params[:id])
    redirect_to root_url
  end

  def index
    @words = @user.words
  end
end
