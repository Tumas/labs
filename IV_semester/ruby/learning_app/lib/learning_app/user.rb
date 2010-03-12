module LearningSystem
  class User
    attr_reader :name, :words
    attr_accessor :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
      @logged_in = false
      @words = []
    end

    def valid?
      if @name.nil? || @pass.nil? || @name.length < 3 || @pass.length < 6 || @pass.length > 14
        false
      else
        true
      end
    end

    def register_word(word)
      @words << word
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
  end
end
