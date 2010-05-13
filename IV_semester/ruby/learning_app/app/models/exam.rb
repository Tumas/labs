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

    score = score_format(local_score / self.words.size)
    # some exception handling might be needed here someday
    Score.new(:score => score, :user_id => self.user.id, :exam_id => self.id).save!
    score
  end

  def average_score
    # sum = self.scores.inject(0) {|sum, s| sum + s.score }
    sum = get_scores_sum
    count = get_scores_count
    return score_format(sum / count) if count != 0
    0
  end

  def hours_from_last_taking_to(time = Time.new)
    time = Time.parse(time) if time.class != Time
    minutes = (time - Time.parse(self.last_score.created_at)).to_i / 60
    raise "Given date is earlier than #{self.title} was last taken" if minutes < 0

    if minutes % 60 > 30
      minutes / 60 + 1
    else
      minutes / 60
    end
  end

  def reset_scores
    Score.delete_all("exam_id = '#{self.id}'")
  end

  def last_score
    Score.find(:first, :order => "created_at desc")
  end

  private
    def score_format(float)
      format("%0.2f", float).to_f
    end

    def get_scores_sum
      Score.sum("score", :conditions => "exam_id = '#{self.id}'")
    end

    def get_scores_count
      Score.count(:exam_id, :conditions => "exam_id = '#{self.id}'")
    end
end
