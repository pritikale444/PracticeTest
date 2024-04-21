Feature: API test for USD rates

  @test
  Scenario: Verify all the acceptance criteria for USD exchange rate api
  Given Hit api
  And verify status code of api is 200
  And API should contain USD exchange rate
  And API should return valid price
  And API response status should be "success"
  And Check USD price against AED and validate price range
  And Verify currency pairs send by API is 162
  Then verify json schema with API response


