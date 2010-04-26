class AddCountToGuess < ActiveRecord::Migration
  def self.up
    change_table :guesses do |t|
      t.integer :count, :null => false, :default => 0
    end
  end

  def self.down
    change_table :guesses do |t|
      t.remove :count
    end
  end
end
