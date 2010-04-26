class CreateWords < ActiveRecord::Migration
  def self.up
    create_table :words do |t|
      t.string :value, :null => false
      t.string :translation, :null => false
      t.text :example, :default => '?'

      t.timestamps
    end
  end

  def self.down
    drop_table :words
  end
end
