Change Log
==========

Version 2.2.0 *(2017-09-28)*
----------------------------

 * New: Moved the hosted [ResponseEcho](https://github.com/simonpercic/ResponseEcho) service to [Heroku](https://www.heroku.com/).


Version 2.1.0 *(2016-12-14)*
----------------------------

 * New: Request method and url path in log (e.g. `OKLOG - GET /shows - http://responseecho...`).
 

Version 2.0.0 *(2016-11-10)*
----------------------------

 * New: Collecting and packing detailed response info to show it on the ResponseEcho info page (incl. request body, url, method, content length, headers, response code, message, duration, size). 
 * New: Option to shorten info url with the goo.gl url shortening service.
 

Version 1.0.1 *(2016-08-17)*
----------------------------

 * **`OkLog3`** Fix: Using backward-compatible hasBody method from OkHttp:
    - HttpEngine.hasBody if using OkHttp <= 3.3.1 and
    - HttpHeaders.hasBody if using OkHttp >= 3.4.0-RC1
 * **`OkLog3`** New: Included ProGuard config.
 * **`OkLog & OkLog3`** New: OkLog / OkLog3 no longer directly depend on either OkHttp / OkHttp3 or Okio. 


Version 1.0.0 *(2016-07-27)*
----------------------------

 * New: Completely rewrote request and response data collection, based on OkHttp's [HttpLoggingInterceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor).
 * New: Removed previously deprecated fields and methods.
 * New: Added flavors to example app to show example usage of both OkLog and OkLog3.
 * New: Response echo endpoint now points to /v1/re/ at [ResponseEcho](https://github.com/simonpercic/ResponseEcho).


Version 0.2.1 *(2016-04-02)*
----------------------------

 * New: Timber is now an optional dependency.
 * Fix: Responses without a body are not logged anymore.


Version 0.2.0 *(2016-03-13)*
----------------------------

 * New: OkLog3 to support OkHttp3.
 * New: Added an option to Builder to use Android's Log methods instead of Timber.


Version 0.1.1 *(2016-03-05)*
----------------------------

 * New: Enabled setting a custom base logging url through `OkLogInterceptor.Builder`'s `setBaseUrl` method.
 * New: Moved the hosted [ResponseEcho](https://github.com/simonpercic/ResponseEcho) service to [Red Hat's OpenShift](https://www.openshift.com/).


Version 0.1.0 *(2015-10-05)*
----------------------------

Initial release.
