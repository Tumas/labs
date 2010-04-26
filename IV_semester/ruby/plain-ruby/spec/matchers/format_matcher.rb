module LearningSystem
  module Matchers
    class ReturnValidFormat 
      def matches?(value)
        (value.to_s =~ /^[0-1]\.[0-9]([0-9])?$/) != nil
      end

      def failure_message_for_should
        "expected #{value} to floating point number between 0 and 1 with at most 2 decimal places"
      end

    end

    def return_valid_format
      ReturnValidFormat.new
    end
  end
end
