Feature: User registration
  As an aspiring learner
  I want to register on the system
  So that I could start using the system

  Scenario: Invoking registration  
    Given I am not yet registered
    When I press the "Register" button
    Then registration form should appear

  Scenario: Completing registration
    Given I have pressed the "Register" button
    And I have filled all the required fields
    When I press press the "Complete registration" button
    Then confirmation dialog should appear
    And it should say "You have been succesfully registered!"
