class Tag < ActiveRecord::Base
  belongs_to :taggable, :polymorphic => true
  has_and_belongs_to_many :users

  validates_presence_of :title
end
