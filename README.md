# OkLog 

Response logging interceptor for OkHttp. 
Logs a url link with url-encoded response for every OkHttp call.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-OkLog-green.svg?style=true)](https://android-arsenal.com/details/1/3513)
[![Build Status](https://api.travis-ci.org/simonpercic/OkLog.svg?branch=master)](https://travis-ci.org/simonpercic/OkLog)
[![CircleCI](https://circleci.com/gh/simonpercic/OkLog.svg?style=shield&circle-token=2b7aab79c2852149996b6abdd6be36fdcd70c5ba)](https://circleci.com/gh/simonpercic/OkLog)
[ ![Download](https://api.bintray.com/packages/simonpercic/maven/oklog3/images/download.svg) ](https://bintray.com/simonpercic/maven/oklog3/_latestVersion)


## Motivation

Debugging Android responses should be easier. Even with Retrofit logging enabled, copying multi-line responses from logcat is cumbersome and annoying.

OkLog writes a clickable link to the Android log with the OkHttp's response as a param. Clicking on the link in logcat opens your browser with the response string.

![Example](https://raw.githubusercontent.com/simonpercic/OkLog/master/art/oklog.gif)


## How does it work?

OkLog intercepts responses from OkHttp, it then gzips and Base64 encodes every response string and generates an url link with the encoded string as a param. It then logs the url using [Timber](https://github.com/JakeWharton/timber) (if you have it, or Android's built-in Log methods if you don't).

That url points to a hosted instance of the [ResponseEcho](https://github.com/simonpercic/ResponseEcho) web app that does the exact opposite, i.e. Base64 decodes and unpacks the url param and returns the response as a string for easier debugging. 


## Usage

### OkLog for OkHttp (use for Retrofit 1.x)

Add using Gradle:
```groovy
compile 'com.github.simonpercic:oklog:1.0.1'
```

```java
// create an instance of OkLogInterceptor using a builder()
OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

// create an instance of OkHttpClient
OkHttpClient okHttpClient = new OkHttpClient();

// add OkLogInterceptor to OkHttpClient interceptors
List<Interceptor> clientInterceptors = okHttpClient.interceptors();
Collections.addAll(clientInterceptors, okLogInterceptor);
```

```java
// use with Retrofit
Client okClient = new OkClient(okHttpClient);

new RestAdapter.Builder()
    .setEndpoint(endpoint)
    .setClient(okClient)
    ...
    .build();
```

### OkLog3 for OkHttp3 (use for Retrofit 2.x)

Add using Gradle:
```groovy
compile 'com.github.simonpercic:oklog3:1.0.1'
```

```java
// create an instance of OkLogInterceptor using a builder()
OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

// create an instance of OkHttpClient builder
OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

// add OkLogInterceptor to OkHttpClient's application interceptors
okHttpBuilder.addInterceptor(okLogInterceptor);

// build
OkHttpClient okHttpClient = okHttpBuilder.build();
```

```java
// use with Retrofit2
new Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    ...
    .build();
```

### Builder options

- `setBaseUrl(String url)`
    Set the base url to prefix the logs with. Useful if you're self-hosting [ResponseEcho](https://github.com/simonpercic/ResponseEcho). 
    Defaults to an OpenShift hosted instance at: 'http://responseecho-simonpercic.rhcloud.com'
   
- `setLogInterceptor(LogInterceptor logInterceptor)` 
    Set a custom log interceptor to do your own logging. See [LogInterceptor](core/src/main/java/com/github/simonpercic/oklog/core/LogInterceptor.java) for details.
    
- `useAndroidLog(boolean useAndroidLog)`
    Pass 'true' to use Android's Log methods for logging, instead of Timber. 
    Since Timber is an optional dependency, OkLog will use it only if it's included it in your app's dependencies. If not, it will fallback to using Android's built-in Log methods.

#### Additional log data options

 method                           | description                   | default value
----------------------------------|-------------------------------|------------------------------
withRequestBody(boolean)          |Include request body           |true
withRequestMethod(boolean)        |Include request method         |true 
withRequestUrl(boolean)           |Include request url            |true 
withProtocol(boolean)             |Include protocol               |false
withRequestContentType(boolean)   |Include request content type   |false
withRequestContentLength(boolean) |Include request content length |true
withRequestBodyState(boolean)     |Include request body state     |true
withRequestHeaders(boolean)       |Include request headers        |false
withRequestFailedState(boolean)   |Include request failed state   |true
withResponseCode(boolean)         |Include response code          |true
withResponseMessage(boolean)      |Include response message       |true
withResponseUrl(boolean)          |Include response url           |false
withResponseDuration(boolean)     |Include response duration      |true
withResponseSize(boolean)         |Include response size          |true
withResponseBodyState(boolean)    |Include response body state    |true
withResponseHeaders(boolean)      |Include response headers       |false

- `withNoLogData() `
    Don't include any additional log data from the options.
    
- `withAllLogData() `
    Include all additional log data from the options.

- `shortenInfoUrl(boolean) `
    Shorten info url on the server-side, defaults to false.


## Known limitations
OkLog writes logs to Android's logging system, which has [a limited line length (~4000 chars)](http://stackoverflow.com/a/8899735). 

Even though the generated urls are gzipped and Base64 encoded, they **might still be longer than the log line limit** on very large http responses. 

Unfortunately, there is no workaround with the current system. Nevertheless, everything should work fine for the majority of cases.

This library optionally uses [Timber](https://github.com/JakeWharton/timber) for the actual logging, which splits lines that are too long, so you can see if a response was longer than the limit.


## ProGuard

ProGuard configuration is already bundled with OkLog/3, so you can safely use it with ProGuard.


## Privacy
OkLog in combination with [ResponseEcho](https://github.com/simonpercic/ResponseEcho) are able to work by encoding request and response data in the URL path and query parameters.  
Consequently, this data might be intercepted on the network.  

The hosted instance of ResponseEcho that OkLog points to by default is accessible over plain HTTP (not HTTPS).  

If you're concerned about your request and response data being intercepted, I strongly suggest you self-host ResponseEcho and set OkLog to point to your hosted instance (either locally or on your own server). 

### Url shortening
When using the url-shortening option (either via an option in OkLog or by using the shorten button on the response info page), the response info is shortened using the [goo.gl](https://goo.gl) url shortener service via their REST API, see: [UrlShortenerManager.java](https://github.com/simonpercic/ResponseEcho/tree/master/src/main/java/com/github/simonpercic/responseecho/manager/urlshortener/UrlShortenerManager.java).

Since the request and response data is included in the URL itself, shortening it using an external service consequently means that data is stored by the url shortening service provider.  

If you're concerned about your request and response data being stored by the shortening service, I strongly suggest you don't shorten the url.

### Google Analytics
Google Analytics is used in ResponseEcho to track its popularity and usage.  
There are two analytics methods included:
- using the Google Analytics API, when showing the plain response data, see: [GoogleAnalyticsManager.java](https://github.com/simonpercic/ResponseEcho/tree/master/src/main/java/com/github/simonpercic/responseecho/manager/analytics/ga/GoogleAnalyticsManager.java).
- using the Google Analytics Web tracking via JavaScript, when showing the response info, see: [base_head.html](https://github.com/simonpercic/ResponseEcho/tree/master/src/main/resources/templates/base_head.html).

In either of these methods, NO response data is included in the analytics tracking.


## Change Log
See [CHANGELOG.md](CHANGELOG.md)


## License

Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
