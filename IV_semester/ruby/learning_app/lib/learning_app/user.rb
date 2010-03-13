module LearningSystem
  class User
    attr_reader :name, :words
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false

      @words = []
      @tests = {}
    end

    def valid?
      if @name.nil? || @pass.nil? || @name.length < 3 || @pass.length < 6 || @pass.length > 14
        false
      else
        true
      end
    end

    # refactor those methods to two create(), delete() + method_missing()
    def create_word(value, translation, hint = nil)
      @words << Word.new(value, translation, hint) 
    end

    def delete_word(word)
      @words.delete(word)
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

    def create_test(name)
      @tests[name.to_sym] = Test.new(name)
    end

    def test(name)
      @tests[name.to_sym]
    end
  end
end
