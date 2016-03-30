# OkLog 

Response logging interceptor for OkHttp. 
Logs a url link with url-encoded response for every OkHttp call.

[![Build Status](https://api.travis-ci.org/simonpercic/OkLog.svg?branch=master)](https://travis-ci.org/simonpercic/OkLog)
[ ![Download](https://api.bintray.com/packages/simonpercic/maven/oklog/images/download.svg) ](https://bintray.com/simonpercic/maven/oklog/_latestVersion)

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
compile 'com.github.simonpercic:oklog:0.2.0'
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
compile 'com.github.simonpercic:oklog3:0.2.0'
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
    Set the base url to prefix the logs with (if you're self-hosting [ResponseEcho](https://github.com/simonpercic/ResponseEcho)). 
    Defaults to an OpenShift hosted instance at: 'http://responseecho-simonpercic.rhcloud.com'
   
- `setLogInterceptor(LogInterceptor logInterceptor)` 
    Set custom log interceptor to do your own logging. See [LogInterceptor](core/src/main/java/com/github/simonpercic/oklog/core/LogInterceptor.java) for details.
    
- `useAndroidLog(boolean useAndroidLog)`
    Use 'true' to use Android's Log methods for logging, instead of Timber. 
    Since Timber is an optional dependency, OkLog will use it only if it's included it in your app's dependencies. If not, it will default to Android's Log methods either way.

## Known limitations
OkLog writes logs to Android's logging system, which has [a limited line length (~4000 chars)](http://stackoverflow.com/a/8899735). 

Even though the generated urls are gzipped and Base64 encoded, they **might still be longer than the log line limit** on very large http responses. 

Unfortunately, there is no workaround with the current system. Nevertheless, everything should work fine for the majority of cases.

This library optionally uses [Timber](https://github.com/JakeWharton/timber) for the actual logging, which splits lines that are too long, so you can see if a response was longer than the limit.


## Change Log
See [CHANGELOG.md](CHANGELOG.md)


## License

Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
