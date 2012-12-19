# Library for Authenticating Android Apps with AppEngine

## Background

In order to authenticate against AppEngine, this library uses the Google Play Services API and Eclipse Library Project as a base, and adds some utility classes and methods to help streamline the process.

The major differences are that this library handles the storing of auth tokens, and provides an easy-to-use API for checking to see if the user is logged in or not.  It also provides an implementation of an async task that will perform authenticated HTTP requests using the credentials/auth tokens provided by the Google Play Services API.

## Usage

The first step in using this project is to add it as a submodule to the repository that you want to be able to authenticate.  You will also need to add the project to Eclipse, and reference it as a library project dependency in your project's settings.

After that, start using the methods provided by the `AuthUtil` class for finding authenticated users, and subclass `AuthenticatedHttpRequest` to provide functionality to the specific HTTP requests you want to perform with an authenticated user.

More documentation will come at a later date.

## Resources

* https://developer.android.com/google/play-services/auth.html
* https://developer.android.com/reference/com/google/android/gms/auth/GoogleAuthUtil.html
