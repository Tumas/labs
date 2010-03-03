module LearningSystem
  class UserHandler

    def initialize
      @users = []
    end

    def register(user)
      raise "Nothing to register" unless user

      unless user.valid?
        raise "Username or password are not correct"
      else
        return "User #{user.name} has been succesfully registered!"
      end
    end

  end
end
