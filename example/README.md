# OkLog example app

Usage example of OkLog and OkLog3. 

Sample app contains two flavors: oklog and oklog3. 

- oklog: uses Retrofit with OkHttp(2) to demonstrate how OkLog can be used in conjunction with Retrofit's logging.
- oklog3: uses Retrofit2 with OkHttp3 to demonstrate how OkLog3 can be used in conjunction with [OkHttp's HttpLoggingInterceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor).

This app also serves as my testing playground during development.

## Disclaimer

You should also keep in mind to not include any logging in your release builds.
