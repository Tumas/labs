module LearningSystem
  class User
    attr_reader :name, :words, :tests, :quizzes
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false

      @words = {}
      @tests = {}
      @quizzes = {}
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

    def add_test(test, overwrite = false)
      raise "Test is already there!" if not overwrite and not @tests[test.to_sym].nil?
      
      @tests[test.to_sym] = test
    end

    def add_quiz(quiz, overwrite = false)
      raise "Quiz is already there!" if not overwrite and not @quizzes[quiz.to_sym].nil?

      @quizzes[quiz.to_sym] = quiz
    end

    # removing invidual items
    def remove_word(word)
      @words.delete(word.to_sym)
    end

    def remove_quiz(quiz)
      @quizzes.delete(quiz.to_sym)
    end

    def remove_test(test)
      @tests.delete(test.to_sym)
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
    def test(name)
      @tests[name.to_sym]
    end

    def quiz(name)
      @quizzes[name.to_sym]
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

    def each_quiz(&block)
      @quizzes.each_value(&block)
    end

    def each_test(&block)
      @tests.each_value(&block)
    end
  end
end
