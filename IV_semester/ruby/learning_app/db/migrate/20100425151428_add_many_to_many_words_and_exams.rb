class AddManyToManyWordsAndExams < ActiveRecord::Migration
  def self.up
    create_table :exams_words, :id => false do |t|
      t.integer :exam_id
      t.integer :word_id
    end
  end

  def self.down
    drop_table :exams_words
  end
end
