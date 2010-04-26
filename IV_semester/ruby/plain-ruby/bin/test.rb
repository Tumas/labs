$: << File.join(File.dirname(__FILE__), "/../lib")
require 'learning_app'

include LearningSystem

Shoes.app :width => 755, :height => 400, :title => 'FL Learning system', :resizable => false do

  @user = User.new("Tomas", "asdasdads")
  @user.add_exam(Exam.new "exam")
  @user.exam("exam").add_word(Word.new("test", "test"))
  @user.exam("exam").add_word(Word.new("test1", "test"))

  x = @user.exam("exam").take(Proc.new {|word, answer| word.guess(:translation => answer)}) do |word|
    ask word.value
  end

  puts "Score is #{x}"
end
