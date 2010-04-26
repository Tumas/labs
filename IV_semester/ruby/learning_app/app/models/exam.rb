class Exam < ActiveRecord::Base
  has_and_belongs_to_many :words
  belongs_to :user
  has_many :scores
  has_many :tags, :as => :taggable

  validates_presence_of :title

  def take(predicate)
    local_score = 0

    if block_given?
      self.words.each do |w|
        answer = yield w
        local_score += 1.0 if predicate.call(w, answer)
      end
    end

    score = inner_format(local_score / self.words.size)
    Score.new(:score => score, :user_id => self.user.id, :exam_id => self.id).save!
    score
  end

  def average_score
    sum = self.scores.inject(0) {|sum, s| sum + s.score }
    inner_format(sum / self.scores.size)
  end

  private
    def inner_format(float)
      format("%0.2f", float).to_f
    end
end
