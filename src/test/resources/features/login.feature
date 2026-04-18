Feature: Login functionality

  Scenario Outline: Successful login with valid credentials
    Given user is on login page
    When user enters "<username>" and "<password>"
    Then user should land on dashboard

    Examples:
      | username  | password             |
      | tomsmith  | SuperSecretPassword! |
      | tomsmith  | SuperSecretPassword! |
      | tomsmith  | SuperSecretPassword! |