Change Log
==========

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
