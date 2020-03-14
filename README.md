# OkLog 
Network logging interceptor for OkHttp. 
Logs an URL link with encoded network call data for every OkHttp call.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-OkLog-green.svg?style=true)](https://android-arsenal.com/details/1/3513)
[![Build Status](https://api.travis-ci.org/simonpercic/OkLog.svg?branch=master)](https://travis-ci.org/simonpercic/OkLog)
[ ![Download](https://api.bintray.com/packages/simonpercic/maven/oklog3/images/download.svg) ](https://bintray.com/simonpercic/maven/oklog3/_latestVersion)


## Motivation
Debugging Android responses should be easier. Even with Retrofit logging enabled, copying multi-line responses from logcat is cumbersome and annoying.

OkLog writes a clickable link to the (Android) log with the OkHttp's response info as params. Clicking on the link in logcat opens your browser with the [detailed response info](#additional-log-data-options).

![Example](https://raw.githubusercontent.com/simonpercic/OkLog/develop/art/oklog.gif)

### Example response info
See an example [in action](http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?qb=H4sIAAAAAAAAAKtWykvMTVWyUgopKk1VcEktSU0uySxLVdJRykstKc8vylayqlbKTFGysqjVUSoqzSvJBKk2M6gFAPS5LSY5AAAA&d=H4sIAAAAAAAAAJ2SQW_TMBiGyYoGWBwmH6CCokUgJITiNi7p1hQhEaVhm7QkVeuhHXESq7WX2pET0vXKv-In8K9wQOIw7cTB38WP3-_1-32gt7gi0Ns0TTUbjSrNW9owlJ1muYeatt6oXU0rPjSH6j3aqvxmmKvtqLt48bh7NcJD_PqYVlXJc9pwJUeiVvKjnW-orlnz6Yp8QdN3_tT6_OD8l7V8FGpmOhTXPw--xeIIHK6YbpmGh6HaZWov-gCESkqWd1IQ3DBWIVrylolj0L9GwV8fSyNR8i1v0GVXYQ-PXfEGDO4BlmxLueRybSDsCxs8NfoNkw0i-4rBo7vODfEyyHNW16gDtSpRUJZqh1LN11xC671IwOBeImbNRhU1HKYLcpEmK-csIs55FMydRboijgnamUeXEYkcsgzCyAnTJIlCIgbg2R29mN6iYM3gAXbFB_D837eIprKmf7JBFwXsT6Zjt_DZZIIz38vczHVxkbknY_EWPJybCOCrWEnHdk_tRLX22MUnNvZn3mTmYfssJib_3ldO4RMzQ7tl6--6sm7jH9Z_rcNvtxQDp0oCAAA=).

[![Example response info](https://raw.githubusercontent.com/simonpercic/OkLog/develop/art/oklog-response-thumb.png)](https://raw.githubusercontent.com/simonpercic/OkLog/develop/art/oklog-response.png)


## How does it work?
OkLog intercepts responses from OkHttp, it then gzips and Base64 encodes the network response & the detailed response info and generates an url link with the encoded data as params. It then logs the url using:
- [Timber](https://github.com/JakeWharton/timber) OR
- Android's built-in logger (if your project does not include Timber) OR
- Java's Logger (if used in a pure-Java/Kotlin project) OR
- your custom logger

That url points to a hosted instance of the [ResponseEcho](https://github.com/simonpercic/ResponseEcho) web app that does the exact opposite, i.e. Base64 decodes and unpacks the url params and displays the response info for easier debugging. 


## Usage

### OkLog for OkHttp (use for Retrofit 1.x)
Add using Gradle:
```groovy
compile 'com.github.simonpercic:oklog:2.3.0'
```

OR (for a pure-Java/Kotlin project, without dependencies on Android)

```groovy
compile 'com.github.simonpercic:oklog-java:2.3.0'
```

usage:

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
compile 'com.github.simonpercic:oklog3:2.3.0'
```

OR (for a pure-Java/Kotlin project, without dependencies on Android)

```groovy
compile 'com.github.simonpercic:oklog3-java:2.3.0'
```

usage:

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
    Defaults to a hosted Heroku instance at: 'http://oklog.responseecho.com'
   
- `setLogInterceptor(LogInterceptor logInterceptor)` 
    Set a custom log interceptor to do your own logging. See [LogInterceptor](core/src/main/java/com/github/simonpercic/oklog/core/LogInterceptor.java) for details.
    
- `setLogger(Logger logger)` 
    Set a custom Logger to do your own logging. See [Logger](core/src/main/java/com/github/simonpercic/oklog/core/Logger.java) for details.
    
- `ignoreTimber(boolean ignoreTimber)`
    Pass 'true' to ignore Timber for logging, even if it is present. 
    Since Timber is an optional dependency, OkLog will use it only if it's included it in your app's dependencies. If not, it will fallback to using Android's built-in Log methods.

#### Additional log data options
 method                           | description                   | included by default
----------------------------------|-------------------------------|------------------------------
withRequestBody(boolean)          |Include request body           |&#10003; true
withRequestMethod(boolean)        |Include request method         |&#10003; true
withRequestUrl(boolean)           |Include request url            |&#10003; true
withProtocol(boolean)             |Include protocol               |&#10007; false
withRequestContentType(boolean)   |Include request content type   |&#10007; false
withRequestContentLength(boolean) |Include request content length |&#10003; true
withRequestBodyState(boolean)     |Include request body state     |&#10003; true
withRequestHeaders(boolean)       |Include request headers        |&#10007; false
withRequestFailedState(boolean)   |Include request failed state   |&#10003; true
withResponseCode(boolean)         |Include response code          |&#10003; true
withResponseMessage(boolean)      |Include response message       |&#10003; true
withResponseUrl(boolean)          |Include response url           |&#10007; false
withResponseDuration(boolean)     |Include response duration      |&#10003; true
withResponseSize(boolean)         |Include response size          |&#10003; true
withResponseBodyState(boolean)    |Include response body state    |&#10003; true
withResponseHeaders(boolean)      |Include response headers       |&#10007; false

- `withNoLogData() `
    Don't include any additional log data from the options.
    
- `withAllLogData() `
    Include all additional log data from the options.

- `shortenInfoUrl(boolean) `
    Shorten info url on the server-side, defaults to false.


## Android and Java/Kotlin support
There are two variants of OkLog:
- OkLog & OkLog3: for Android projects
- OkLog-Java & OkLog3-Java: for pure Java/Kotlin projects (without Android dependencies)

### The full variants matrix:

 x                          | Android         | pure Java/Kotlin (no-Android)
----------------------------|-----------------|------------------------------
OkHttp (Retrofit 1.x)       |oklog            |oklog-java
OkHttp3 (Retrofit 2.x)      |oklog3           |oklog3-java


## Known limitations
OkLog for Android writes logs to Android's logging system, which has [a limited line length (~4000 chars)](http://stackoverflow.com/a/8899735). 

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


## iOS
If you'd want to use OkLog in an iOS project, check out [OkLog-iOS](https://github.com/diegotl/OkLog-iOS), implemented by [Diego Trevisan](https://github.com/diegotl).  


## Change Log
See [CHANGELOG.md](CHANGELOG.md)


## License
Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
