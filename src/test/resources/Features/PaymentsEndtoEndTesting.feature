Feature: Payments End to End Testing

  @firstVeng
  Scenario: Test the payment flow and the expected response
    Given variables with values:
      | $Var.requestId | $data.uuid |
    And I load service payment
    And I post API payment with body from paymentPayload.json
    Then I validate that response code is 201
    And I validate the response value with:
      | $Api.amount      | 100.0            |
      | $Api.currency    | USD              |
      | $Api.card.number | 4242424242424242 |


  @firstVeng
  Scenario: Test the payment flow and the expected response
    Given variables with values:
      | $Var.requestId | $data.uuid |
    And I load service payment
    And I post API payment with body from paymentPayload.json
    Then I validate that response code is 201
    And I validate the response value with:
      | $Api.amount      | 200.00           |
      | $Api.currency    | "EUR"            |
      | $Api.card.number | 4242424242424242 |

