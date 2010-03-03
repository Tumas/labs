module LearningSystem
  class Application
    def initialize(us)
      @user_handler = us
    end

    def register
      @user_handler.register
    end

    def complete_registration(user)
      @user_handler.complete_registration(user)
    end
  end
end
