class FixGuessNaming < ActiveRecord::Migration
  def self.up
    change_table :guesses do |t|
      t.rename :type, :part
    end
  end

  def self.down
    change_table :guesses do |t|
      t.rename :part, :type
    end
  end
end
