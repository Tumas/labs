module Synonymable
  require 'rubygems'
  require 'wordnik'

  def synonyms(count = 4)
    results = synonym_finder.related(self.value, count, :type => :synonym)

    synonyms = []
    results.first["wordstrings"].each {|s| synonyms << s} unless results.empty?
    synonyms
  end

  def similar_definitions(count = 4)
    results = self.synonyms(count)
    return [] if results.empty?

    definitions = []
    results.each do |word|
      defs = synonym_finder.define(word, :count => 1) 
      definitions << defs.first["text"] unless defs.empty?
    end
  end

  def synonym_finder
    @sf ||= Wordnik.new
  end

  def synonym_finder= (object)
    @sf = object
  end
end
