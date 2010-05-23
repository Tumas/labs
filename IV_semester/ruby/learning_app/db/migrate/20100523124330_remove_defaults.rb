class RemoveDefaults < ActiveRecord::Migration
  def self.up
    change_table :users do |t|
      t.remove :name, :password
      t.string :name, :null => false, :default => "unknown"
      t.string :password, :null => false, :default => "unknown" 
    end
  end

  def self.down
    change_table :users do |t|
      t.remove :name, :password
      t.string :name, :null => false, :default => false
      t.string :password, :null => false, :default => false
    end
  end
end
