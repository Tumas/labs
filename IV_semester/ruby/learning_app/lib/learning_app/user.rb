module LearningSystem
  class User
    attr_reader :name, :words, :tests
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false

      @words = {}
      @tests = {}
    end

    def valid?
      if @name.nil? || @pass.nil? || @name.length < 3 || @pass.length < 6 || @pass.length > 14
        false
      else
        true
      end
    end

    def add_word(word)
      @words[word.to_sym] = word
    end

    def add_test(test)
      @tests[test.to_sym] = test
    end

    def remove_word(word)
      @words.delete(word.to_sym)
    end

    def remove_test(test)
      @tests.delete(test.to_sym)
    end

    def login
      @logged_in = true
    end

    def logout
      @logged_in = false
    end

    def logged_in?
      @logged_in
    end

    def test(name)
      @tests[name.to_sym]
    end

    def each_word(&block)
      @words.each_value(&block)
    end

    #def each_test(&block)
    #  @tests.each_value(&block)
    #end

    def reset_tests
      @tests = {}
    end

    def take_test(test)
      test.take
    end

  end
end
