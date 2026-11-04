@public
Feature: Amazon public shopping journeys

  Background:
    Given I open the Amazon home page

  @deals @smoke
  Scenario: Open the third deal, add the minimum quantity and verify the cart
    When I open Today's Deals
    And I open the third displayed deal
    And I add the minimum available quantity of the item to the cart
    Then the cart should contain the expected quantity

  @search @regression
  Scenario: Search for mobiles and capture the last displayed result
    When I search for "Mobiles"
    And I scroll to the last displayed search result
    Then the last displayed product details should be captured

  @navigation @regression
  Scenario: Navigate through the left menu to mobile phones and return home
    When I open the left navigation menu
    And I navigate to Mobiles and Mobile Phones
    Then I should be able to return to the Amazon home page
