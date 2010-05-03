require 'spec_helper'

describe Score do
  describe Score, " validations/associations" do
    it { should belong_to :user }
    it { should belong_to :exam }
  end
end
