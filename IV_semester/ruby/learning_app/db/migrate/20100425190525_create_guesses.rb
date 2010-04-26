class CreateGuesses < ActiveRecord::Migration
  def self.up
    create_table :guesses do |t|
      t.string :value
      t.string :type

      t.timestamps
    end
  end

  def self.down
    drop_table :guesses
  end
end
