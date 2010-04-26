class Word < ActiveRecord::Base
  belongs_to :user
  has_many :guesses
  has_many :tags, :as => :taggable
  has_and_belongs_to_many :exams

  validates_presence_of :value, :translation

  def times_guessed
    self.guesses.inject(0) {|sum, g| sum + g.count }
  end

  def times_answered
    sum = 0
    # perdaryt i sql
    self.guesses.each do |g|
      sum += g.count if (g.part == "translation" and g.value == self.translation) or (g.part == "value" and g.value == self.value)
    end
    sum
  end

  def guess(guess)
    guess.guessed

    if ((guess.part == "translation") and (guess.value == self.translation)) or ((guess.part == "value") and (guess.value == self.value))
      true
    else
      false
    end
  end
end
