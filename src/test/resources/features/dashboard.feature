Feature: Dashboard functionality

  Scenario Outline: Dashboard - <testCaseId>
    Given user is on dashboard page
    When user performs dashboard action with test case "<testCaseId>"
    Then dashboard result should be "<expectedResult>"

    Examples:
      | testCaseId    | expectedResult |
      | TestCase001   | expected       |
