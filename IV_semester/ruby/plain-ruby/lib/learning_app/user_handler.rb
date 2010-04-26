module LearningSystem

require 'digest/sha1'

  class UserHandler
    attr_reader :users

    def initialize(users)
      # list of registered users
      @users = users
    end

    def register(user)
      raise "Nothing to register" unless user
      raise "Username or password are not correct" unless user.valid?
      raise "User name #{user.name} is already taken" if user_name_taken?(user.name)

      @users.push(User.new(user.name, Digest::SHA1.hexdigest(user.pass)))
      "User #{user.name} has been succesfully registered!"
    end

    def user_name_taken?(username)
      @users.each {|user| return true if user.name == username }
      false
    end

    def login(user)
      @users.each do |r_user|
        if r_user.name == user.name and r_user.pass == Digest::SHA1.hexdigest(user.pass)
          r_user.login
          return r_user 
        end
      end

      raise "Incorrect login!"
    end

    def logout(user)
      user.logout
      "User #{user.name} has been successfully logged out"
    end

    def delete(user)
     @users.each {|u| @users.delete(u) if u.name == user.name and user.pass == u.pass }
    end
  end
end
