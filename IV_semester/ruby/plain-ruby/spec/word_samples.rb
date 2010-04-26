module LearningSystem
  module WordSamples
    def good_word_samples
        [
          { :value => "spouse", :translation => "a person's partner in marriage" },
          { :value => "audacity", :translation => "fearless daring" },
          { :value => "harness", :translation => "exploit the power of", :hint => "exploit something"}
        ]
    end

    def bad_word_samples
        [
          { :value => "", :translation => "" },
          { :value => "facet", :translation => nil, :hint => "" },
          { :translation => "a distinct feature" },
          { :hint => "unknown" }
        ]
    end
  end
end
