Feature: Login functionality

  Scenario Outline: Login - <testCaseId>
    Given user is on login page
    When user logs in with test case "<testCaseId>"
    Then login result should be "<expectedResult>"

    Examples:
      | testCaseId      | expectedResult |
      | ValidLogin      | success        |
      | InvalidPassword | failure        |
      | InvalidUsername | failure        |