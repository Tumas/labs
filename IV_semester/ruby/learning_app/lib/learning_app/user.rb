module LearningSystem
  class User
    attr_reader :name, :words, :exams
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false

      @words = {}
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
    def add_word(word, overwrite = false)
      raise "Word is already there!" if not overwrite and not @words[word.to_sym].nil? 
      @words[word.to_sym] = word
    end

    def add_exam(exam, opts = {})
      opts[:overwrite] = false if opts[:overwrite].nil?

      raise "Exam is already there!" if not opts[:overwrite] and not @exams[exam.to_sym].nil?
      @exams[exam.to_sym] = exam
    end

    # removing invidual items
    def remove_exam(exam_name)
      @exams.delete(exam_name.to_sym)
    end

    def remove_word(word)
      @words.delete(word.to_sym)
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

    def word(info)
      return nil if info[:value].nil? and info[:translation].nil?

      each_word do |w|
        return w if (info[:value].nil? or info[:value] == w.value) and (info[:translation].nil? or info[:translation] == w.translation)
      end

      nil
    end

    # Iterating over items 
    def each_word(&block)
      @words.each_value(&block)
    end

    def each_exam(&block)
      @exams.each_value(&block)
    end
  end
end
