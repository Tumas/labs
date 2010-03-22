module LearningSystem

require 'digest/sha1'
require 'yaml'

  class UserHandler

    # list of registered users
    USERS = File.join(File.dirname(__FILE__), '/../../users.yaml')
    @@users = YAML.load_file(USERS)

    def register(user)
      raise "Nothing to register" unless user
      raise "Username or password are not correct" unless user.valid?
      raise "User name #{user.name} is already taken" if user_name_taken?(user.name)

      @@users.push(User.new(user.name, Digest::SHA1.hexdigest(user.pass)))

      # save state after registration
      File.open(USERS, 'w') do |out|
        YAML.dump(@@users, out)
      end
      "User #{user.name} has been succesfully registered!"
    end

    def user_name_taken?(username)
      @@users.each {|user| return true if user.name == username }
      false
    end

    def self.delete_users
      @@users = []
    end

    def login(user)
      @@users.each do |r_user|
        if r_user.name == user.name && r_user.pass == Digest::SHA1.hexdigest(user.pass)
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
  end
end
