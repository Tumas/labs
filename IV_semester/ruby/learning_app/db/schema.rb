# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20100523124330) do

  create_table "exams", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "public",     :default => true
    t.integer  "user_id"
    t.string   "title"
  end

  create_table "exams_words", :id => false, :force => true do |t|
    t.integer "exam_id"
    t.integer "word_id"
  end

  create_table "guesses", :force => true do |t|
    t.string   "value"
    t.string   "part"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "user_id"
    t.integer  "word_id"
    t.integer  "count",      :default => 0, :null => false
  end

  create_table "scores", :force => true do |t|
    t.float    "score",      :default => 0.0, :null => false
    t.integer  "user_id"
    t.integer  "exam_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "tags", :force => true do |t|
    t.string   "title",         :null => false
    t.integer  "taggable_id"
    t.string   "taggable_type"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "tags_users", :id => false, :force => true do |t|
    t.integer "tag_id"
    t.integer "user_id"
  end

  create_table "users", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "name",       :default => "unknown", :null => false
    t.string   "password",   :default => "unknown", :null => false
  end

  create_table "words", :force => true do |t|
    t.string   "value",                        :null => false
    t.string   "translation",                  :null => false
    t.text     "example",     :default => "?"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "user_id"
  end

end
