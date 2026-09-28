Feature: Pet CRUD API behavior
  As an API consumer
  I want to manage pets through the Petstore API
  So that the resource lifecycle and API behavior are verified

  Scenario: Create a pet successfully
    Given I have a unique pet named "feature-create-pet" with status "available"
    When I create the pet
    Then the response status should be 200
    And the response content type should be JSON
    And the created pet should contain its request values

  Scenario: Read an existing pet successfully
    Given I have a unique pet named "feature-read-pet" with status "available"
    When I create the pet
    And I read the pet
    Then the response status should be 200
    And the response content type should be JSON
    And the pet should contain its request values

  Scenario: Update an existing pet successfully
    Given I have a unique pet named "feature-update-pet" with status "available"
    When I create the pet
    And I update the pet
    Then the response status should be 200
    And the response content type should be JSON
    And the pet should contain the updated values

  Scenario: Delete an existing pet successfully
    Given I have a unique pet named "feature-delete-pet" with status "available"
    When I create the pet
    And I delete the pet
    Then the response status should be 200
    And the response content type should be JSON
    And the response should confirm deletion of the pet

  Scenario: A deleted pet cannot be read
    Given I have a unique pet named "feature-read-deleted-pet" with status "available"
    When I create the pet
    And I delete the pet
    And I read the pet
    Then the response status should be 404
    And the response content type should be JSON
    And the response should report that the pet was not found

  Scenario: Reading a never-used ID returns not found
    When I read the pet using a never-used ID
    Then the response status should be 404
    And the response content type should be JSON
    And the response should report that the pet was not found

  Scenario: Deleting a never-used ID returns not found
    When I delete a never-used ID
    Then the response status should be 404

  Scenario: Find pets by status successfully
    Given I have a unique pet named "feature-status-pet" with status "pending"
    When I create the pet
    And I find pets by the status "pending"
    Then the response status should be 200
    And the response content type should be JSON
    And the response should contain pets with status "pending"
    And the created pet should be included in the response

  Scenario: Malformed JSON is rejected when creating a pet
    When I create the pet with malformed JSON
    Then the response status should be 400
    And the response content type should be JSON
    And the response should report malformed input

  @known-issue
  Scenario: Live API accepts a pet without the name field
    When I create the pet with a missing name
    Then the response status should be 200
    And the response content type should be JSON
    And the response should omit the missing name field

  @known-issue
  Scenario: Live API creates a pet when updating a never-used ID
    When I update a never-used ID with a valid pet
    Then the response status should be 200
    And the response content type should be JSON
    And the response should show that the update created the pet

  @known-issue
  Scenario: Live API returns a JSON parsing error for a non-numeric ID
    When I read the pet using a non-numeric ID
    Then the response status should be 404
    And the response content type should be JSON
    And the response should describe a numeric ID parsing error

  @known-issue
  Scenario: Live API returns an empty result for an unsupported status filter
    When I find pets by the unsupported status "bogus"
    Then the response status should be 200
    And the response content type should be JSON
    And the response should contain an empty JSON array