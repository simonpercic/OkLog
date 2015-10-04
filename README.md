# OkLog 

Response logging interceptor for OkHttp. 
Logs a url link with url-encoded response for every OkHttp call.

## Motivation

Debugging Android responses should be easier. Even with Retrofit logging enabled, copying multi-line responses from logcat is cumbersome and annoying.

OkLog writes a clickable link to the Android log with the OkHttp's response as a param. Clicking on the link in logcat opens your browser with the response string.

## How does it work?

OkLog intercepts responses from OkHttp, it then gzips and Base64 encodes every response string and generates an url link with the encoded string as a param. It then logs the url using [Timber](https://github.com/JakeWharton/timber).

That url points to a hosted instance of the [ResponseEcho](https://github.com/simonpercic/ResponseEcho) web app that does the exact opposite, i.e. Base64 decodes and unpacks the url param and returns the response as a string for easier debugging. 

## Usage

Add using Gradle:
```groovy
compile 'TODO'
```

```java
// create an instance of OkLogInterceptor using a builder()
OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

OkHttpClient client = new OkHttpClient();
List<Interceptor> clientInterceptors = client.interceptors();

// add OkLogInterceptor to OkHttpClient interceptors
Collections.addAll(clientInterceptors, okLogInterceptor);
```

```java
// with Retrofit
Client okClient = new OkClient(client);

new RestAdapter.Builder()
    .setClient(okClient)
    .setEndpoint(endpoint)
    ...
    .build();
```

## Known limitations
OkLog writes logs to Android's logging system, which has [a limited line length (~4000 chars)](http://stackoverflow.com/a/8899735). 

Even though the generated urls are gzipped and Base64 encoded, they **might still be longer than the log line limit** on very large http responses. 

Unfortunately, there is no workaround with the current system. Nevertheless, everything should work fine for the majority of cases.

This library uses [Timber](https://github.com/JakeWharton/timber) for the actual logging, which splits lines that are too long, so you can see if a response was longer than the limit.


## License

Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
