require File.join(File.dirname(__FILE__), '/word_list') 

module LearningSystem
  class User
    include WordList

    attr_reader :name, :exams
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false

      @exams = {}
    end

    def valid?
      if @name.nil? || @pass.nil? || @name.length < 3 || @pass.length < 6 || @pass.length > 14
        false
      else
        true
      end
    end

    # Adding items
    def add_exam(exam, opts = {})
      opts[:overwrite] = false if opts[:overwrite].nil?

      raise "Exam is already there!" if not opts[:overwrite] and not @exams[exam.to_sym].nil?
      @exams[exam.to_sym] = exam
    end

    # removing invidual items
    def remove_exam(exam_name)
      @exams.delete(exam_name.to_sym)
    end

    # Logging 
    def login
      @logged_in = true
    end

    def logout
      @logged_in = false
    end

    def logged_in?
      @logged_in
    end

    # referencing/finding items
    def exam(name) 
      @exams[name.to_sym]
    end

    # Iterating over items 
    def each_exam(&block)
      @exams.each_value(&block)
    end
  end
end
