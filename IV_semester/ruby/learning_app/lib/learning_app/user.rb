module LearningSystem
  class User
    attr_reader :name, :pass

    def initialize(name, pass)
      @name = name
      @pass = pass
    end

    def valid?
      if @name.nil? || @pass.nil? || @name.length < 3 || @pass.length < 6 || @pass.length > 14
        false
      else
        true
      end
    end
  end
end
