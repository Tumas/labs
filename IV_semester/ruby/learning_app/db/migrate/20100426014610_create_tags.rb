class CreateTags < ActiveRecord::Migration
  def self.up
    create_table :tags do |t|
      t.string :title, :null => false

      # polymorphic association with exams and words
      t.references :taggable, :polymorphic => true

      t.timestamps
    end

    # many to many with user
    create_table :tags_users, :id => false do |t|
      t.integer :tag_id
      t.integer :user_id
    end

  end

  def self.down
    drop_table :tags
    drop_table :tags_users
  end
end
