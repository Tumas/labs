class FixAssocationsForGuess < ActiveRecord::Migration
  def self.up
    change_table :guesses do |t|
      t.integer :user_id
      t.integer :word_id
    end
  end

  def self.down
  end
end
