module LearningSystem
  module Taggable
    def tags
      @tags ||= {}
    end

    def add_tags(tags_to_add)
      tags_to_add.each {|t| tags[t.strip.to_sym] = t.strip if not t.nil? and not t.strip.empty? }
    end

    def remove_tag(tag)
      tags.delete(tag.strip.to_sym)
    end

    def tag(tag)
      tags[tag.strip.to_sym]
    end

    def each_tag(&block)
      tags.each_value(&block)
    end
  end
end
