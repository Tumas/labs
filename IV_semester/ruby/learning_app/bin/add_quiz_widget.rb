class Quizzer < Shoes::Widget
  include LearningSystem

  def initialize( opts = {} )
    @user = opts[:user]
    @title = opts[:title]
    @mode = opts[:mode]

    # subject - test/quiz
    # subject_object - test or quiz object
    # subjects - plural or subject tests/quizzes

    @subject = opts[:subject]
    @subject = 'exam' if @subject.nil?

    if opts[:exam].nil?  
      @subject_object = Exam.new('name of the exam') 
    else
      @subject_object = opts[:exam]
    end
    @subjects = 'exams'

    @final_text = if @mode == 'add'
      "add #{@subject}"
    elsif @mode == 'edit'
       "update #{@subject}"
    end

    @subject_words = []
    @user_words = []

    stack :margin_top => 10 do
      subtitle @title, :stroke => white, :align => "center"

      stack :top => 80, :left => 160, :align => "center" do
        @name = edit_line :width => 400, :text => @subject_object.name 
      end

      stack :top => 200, :left => 20 do
        para(link("oh, nervermind..", :click => "/#{@subjects}"), :top => 370, :left => 310)

        @user.each_word {|w| @user_words << [w.value] }
        update_subject_words_list

        @wt = table(:top => -80, :left => 130, :rows => 5, :headers => [["user words", 150],], :items => @user_words, :blk => nil)
        @qw = table(:top => -80, :left => 400, :rows => 5, :headers => [["#{@subject} words", 150],], :items => @subject_words, :blk => nil)

        button "-->", :top => -30, :left => 320 do
          unless @wt.selected.nil?
            begin
              @subject_object.add_word(@user.word({ :value => @user_words[@wt.selected][0] }))
              update_subject_words_list
              @qw.update_items(@subject_words)
            rescue Exception => msg
              alert msg
            end
          end
        end

        button "<--", :top => 0, :left => 320 do
          @subject_object.remove_word(@user.word({ :value => @subject_words[@qw.selected][0]}))
          update_subject_words_list
          @qw.update_items(@subject_words)
        end

        button @final_text, :margin_left => 300, :margin_top => 130, :margin_bottom => 10  do
          begin
            @subject_object.name = @name.text
            if @mode == 'edit'
              if @subject == 'exam' 
                @user.add_exam(@subject_object, true) 
              end
            else
                @user.add_exam(@subject_object)
            end

            LearningSystemShoesScript.save_state
            mes = 'added'
            mes = 'updated' if @mode == 'edit'
            alert "#{@subject} #{@name.text} was succesfully #{mes}"
            visit "/#{@subjects}" 
          rescue Exception => msg
            alert msg
          end
        end
      end
    end
  end
  
  # ------------------------------ 
  def update_subject_words_list
    @subjects_words = []
    @subject_object.each_word {|w| @subject_words << [w.value] }
  end
end
