class FixExam < ActiveRecord::Migration
  def self.up
    change_table :exams do |t|
      t.remove :title
      t.string :title
    end
  end

  def self.down
    change_table :exams do |t|
      t.remove :title
      t.string :title
    end
  end
end
