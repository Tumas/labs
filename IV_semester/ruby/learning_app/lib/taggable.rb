module Taggable
  def add_tags(tags)
    tags.each do |t|
      unless t.nil? or t.empty?
        taggable_type = 'Word' 
        taggable_type = 'Exam' if self.class == Exam 

        tag = Tag.find_or_create_by_title(:title => t.strip, :taggable_id => self.id, :taggable_type => taggable_type) 
        tag.users << self.user 
        tag.save!
      end
    end
  end
end
