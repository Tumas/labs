class Guess < ActiveRecord::Base
  belongs_to :user
  belongs_to :word

  validates_presence_of :value 
  validates_inclusion_of :part, :in => %w(value translation)

  def guessed
    g = Guess.find(:first, :conditions => { :value => self.value, :part => self.part, :user_id => self.user_id, :word_id => self.word.id })  

    g.count += 1
    g.save!
  end
end
