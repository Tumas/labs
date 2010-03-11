module LearningSystem
  class UserHandler

   @@users = [] 

    def register(user)
      raise "Nothing to register" unless user
      raise "Username or password are not correct" unless user.valid?
      raise "User name #{user.name} is already taken" if user_name_taken?(user.name)

      @@users.push(user)
      "User #{user.name} has been succesfully registered!"
    end

    def user_name_taken?(username)
      @@users.each {|user| return true if user.name == username }
      false
    end

    #def self.registered?(user)
    #  @@users.include?(user)
    #end

    def self.delete_users
      @@users = []
    end
  end
end
