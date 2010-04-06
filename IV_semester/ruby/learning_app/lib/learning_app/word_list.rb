module LearningSystem
  module WordList
    def words
      @words ||= {}
    end

    def add_word(word, overwrite = false)
      raise "word is already there!" if not overwrite and not words[word.to_sym].nil? 
      words[word.to_sym] = word
    end

    def remove_word(word)
      words.delete(word.to_sym)
    end

    def each_word(&block)
      words.each_value(&block)
    end

    def word(info)
      return nil if info[:value].nil? and info[:translation].nil?

      each_word do |w|
        return w if (info[:value].nil? or info[:value] == w.value) and (info[:translation].nil? or info[:translation] == w.translation)
      end

      nil
    end

  end
end
