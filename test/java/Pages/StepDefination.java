package Pages;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import io.restassured.path.json.JsonPath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import java.util.HashMap;
import java.util.Map;


public class StepDefination {

    private static Response response;
    SoftAssertions softassert = new SoftAssertions();


    @Given("^Hit api$")
    public void hit_api()  {
         RestAssured.baseURI = "https://open.er-api.com/";
         RestAssured.basePath = "v6/latest/USD";

         //Hit api to verify response
         response  =  RestAssured.given().headers("Content-Type",  "application/json").get();

}

    @And("^verify status code of api is (\\d+)$")
    public void verify_status_code_of_api_is(int code){

       softassert.assertThat(code).as("Status Code is other than 200").isEqualTo(response.getStatusCode());

    }

    @And("^API should contain USD exchange rate$")
    public void api_should_contain_USD_exchange_rate()  {

      String actual_basecode = response.jsonPath().getString("base_code");
      softassert.assertThat("USD").as("Base currency is other than USD").isEqualTo(actual_basecode);

    }

    @And("^API should return valid price$")
    public void api_should_return_valid_price() throws Throwable {
        // Extract JSON object from JSONPath
        JsonPath jsonPath = response.jsonPath();
        Object jsonObject = jsonPath.get("rates");

        // Convert JSON object to HashMap
        HashMap<String, Number> hashMap = jsonToHashMap(jsonObject);

        boolean hasValuesGreaterThanZero = checkValuesGreaterThanZero(hashMap);

        // Validate prices are non zero
        if (hasValuesGreaterThanZero) {
            System.out.println("Price values greater than 0.");
        } else {
            System.out.println("Price does not have values greater than 0.");
        }

        softassert.assertThat(true).as("Price value other than non zero value").isEqualTo(hasValuesGreaterThanZero);

    }

    @And("^API response status should be \"([^\"]*)\"$")
    public void api_response_status_should_be(String status)  {

        String actual_status = response.jsonPath().getString("result");
        softassert.assertThat(status).as("Status is other than success").isEqualTo(actual_status);

    }

    @And("^Check USD price against AED and validate price range$")
    public void check_USD_price_against_AED_and_validate_price_range() {

        Double value = response.jsonPath().getDouble("rates.AED");
        softassert.assertThat(value).as("AED currency value is not between defined range").isBetween(3.6,3.7);

    }

    @And("^Verify currency pairs send by API is (\\d+)$")
    public void verify_currency_pairs_send_by_API_is(int count)  {

        int actual_count = response.jsonPath().getInt("rates.size()");
        softassert.assertThat(count).as("Rates count is other than 162").isEqualTo(actual_count);
        softassert.assertAll();

    }

    @Then("^verify json schema with API response$")
    public void verify_json_schema_with_API_response(){

        response.then().assertThat().body(matchesJsonSchema(getClass().getClassLoader().getResourceAsStream("jsonschema.json")));

    }


    // Method to extract JSON object from JSONPath
    public static HashMap<String, Number> jsonToHashMap(Object jsonObject) {
        if (jsonObject instanceof HashMap) {
            return (HashMap<String, Number>) jsonObject;
        } else {
            throw new IllegalArgumentException("Object is not a JSON object");
        }
    }

   //Method to check hashmap values are greater than 0
    public static boolean checkValuesGreaterThanZero(Map<String, Number> map) {
        for (Number value : map.values()) {
            double doubleValue = value.doubleValue();
            if (doubleValue > 0) {
                return true;
            }
        }
        return false;
    }

}
