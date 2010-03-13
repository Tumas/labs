module LearningSystem
  module UserSamples
    def good_users
        [
          User.new("tom", "b4dp4ss"),
          User.new('Tomm', 'NotS3cure'),
        ]
    end

    def bad_users
        [
          User.new('Pete', ''),
          User.new('', ''),
          User.new('', 'b43'),
          User.new('Timothy', 'Withwaytolooooooooooongpassword'),
        ]
    end
  end
end
