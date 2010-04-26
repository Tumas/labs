class CreateScores < ActiveRecord::Migration
  def self.up
    create_table :scores do |t|
      t.float :score, :null => false, :default => 0.0
      t.integer :user_id
      t.integer :exam_id

      t.timestamps
    end
  end

  def self.down
    drop_table :scores
  end
end
