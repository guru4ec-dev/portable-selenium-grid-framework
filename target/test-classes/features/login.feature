Feature: Login

  Scenario: Valid login
    Given user is on login page
    When user enters valid credentials
    Then user should see dashboard

  Scenario: Invalid login
    Given user is on login page
    When user enters invalid credentials
    Then error message should be shown
