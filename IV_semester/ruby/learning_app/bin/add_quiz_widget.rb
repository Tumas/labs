class Quizzer < Shoes::Widget
  include LearningSystem

  def initialize( opts = {} )
    @user = opts[:user]
    @title = opts[:title]
    @mode = opts[:mode]
    @quiz = if opts[:quiz].nil?
      Quiz.new('name of the quiz') 
    else
      opts[:quiz]
    end

    @final_text = if @mode == 'add'
      'add quiz'
    elsif @mode == 'edit'
       'update quiz'
    end

    @quiz_words = []
    @user_words = []

    stack :margin_top => 10 do
      subtitle @title, :stroke => white, :align => "center"

      stack :top => 80, :left => 160, :align => "center" do
        @name = edit_line :width => 400, :text => @quiz.name 
      end

      stack :top => 200, :left => 20 do
        para(link("oh, nervermind..", :click => "/quizzes"), :top => 370, :left => 310)

        @user.each_word {|w| @user_words << [w.value] }
        update_quiz_words_list

        @wt = table(:top => -80, :left => 130, :rows => 5, :headers => [["User words", 150],], :items => @user_words, :blk => nil)
        @qw = table(:top => -80, :left => 400, :rows => 5, :headers => [["Quiz words", 150],], :items => @quiz_words, :blk => nil)

        button "-->", :top => -30, :left => 320 do
          unless @wt.selected.nil?
            begin
              @quiz.add_word(@user.word({ :value => @user_words[@wt.selected][0] }))
              update_quiz_words_list
              @qw.update_items(@quiz_words)
            rescue Exception => msg
              alert msg
            end
          end
        end

        button "<--", :top => 0, :left => 320 do
          @quiz.remove_word(@user.word({ :value => @quiz_words[@qw.selected][0]}))
          update_quiz_words_list
          @qw.update_items(@quiz_words)
        end

        button @final_text, :margin_left => 300, :margin_top => 130, :margin_bottom => 10  do
          begin
            @quiz.name = @name.text
            @user.add_quiz(@quiz, true) if @mode == 'edit'

            LearningSystemShoesScript.save_state
            mes = 'added'
            mes = 'updated' if @mode == 'edit'
            alert "Quiz #{@name.text} was succesfully #{mes}"
            visit '/quizzes'
          rescue Exception => msg
            alert msg
          end
        end
      end
    end
  end
  
  # ------------------------------ 
  def update_quiz_words_list
    @quiz_words = []
    @quiz.each_word {|w| @quiz_words << [w.value] }
  end
end
