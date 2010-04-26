class AddDbConstraintsToExam < ActiveRecord::Migration
  def self.up
    change_table :exams do |t|
      t.remove :title, :public
      t.string :title, :null => false
      t.boolean :public, :default => true
    end
  end

  def self.down
    t.remove :title, :public
    t.string :title
    t.boolean :public
  end
end
