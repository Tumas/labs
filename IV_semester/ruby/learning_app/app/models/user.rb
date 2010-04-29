class User < ActiveRecord::Base
  has_many :words
  has_many :exams
  has_many :guesses
  has_many :scores
  has_and_belongs_to_many :tags

  validates_presence_of :name, :password
  validates_length_of :name, :in => 3..20
  validates_length_of :password, :in => 6..50

  def add_word(word, opts = { })
    if self.words.exists?(:value => word.value, :translation => word.translation) and not opts[:overwrite]
      raise "Word #{word.value} with the same value is already there!"
    else
      # save word with correct association
      self.words << word
      word.save
    end
  end

  def add_exam(exam, opts = {})
    if self.exams.exists?(:title => exam.title) and not opts[:overwrite]
      raise "Exam with #{exam.title} is already there!"
    else
      self.exams << exam
      exam.save
    end
  end

  def remove_word(word)
    # This doesn't delete from the DB if :dependent => delete/destroy is not set
    # self.words.delete(word)
    Word.delete(word.id)
  end

  def remove_exam(exam) 
    Exam.delete(exam.id)
  end

  def word(opts = {})
    opts[:user_id] = self.id
    Word.find(:first, :conditions => opts)
  end

  def exam(opts = {})
    opts[:user_id] = self.id
    Exam.find(:first, :conditions => opts)
  end
end
