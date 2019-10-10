package com.ddbank.tradeapi;

public interface Configuration {


    String API_KEY = "<Your ApiKey>";

    String API_SECRET = "<Your ApiSecret>";

    String CUSTOM_CODE = "<Your CustomCode>";

    String API_ENDPOINT = "<Your Target Domain>";



    String HEADER_KEY = "X-CA-ACCESSKEY";

    String HEADER_TS = "X-CA-TIMESTAMP";

    String HEADER_NONCE = "X-CA-NONCE";

    String HEADER_SIGN = "X-CA-SIGNATURE";

    String MEDIA_TYPE_JSON = "application/json; charset=UTF-8";

    String URI_STOCK_QUERY = "/api/v1/stock/list";

    String URI_STOCK_SUBMIT = "/api/v1/stock/placeOrder";

}
