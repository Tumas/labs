class Word < ActiveRecord::Base
  include Synonymable
  include Taggable

  belongs_to :user
  has_many :guesses
  has_many :tags, :as => :taggable
  has_and_belongs_to_many :exams

  validates_presence_of :value, :translation

  def times_guessed
    Guess.sum("count")
  end

  def times_answered
    Guess.sum("count", :conditions => "(part = 'translation' and value = '#{self.translation}') or (part = 'value' and value = '#{self.value}')")
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
