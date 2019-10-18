# JStore_Android

## About

The Android app for JStore, a platform for buying and selling stuff on campus
of Jacobs University Bremen. The [Web version](jstore.xyz) is also under development by
my friend [Taiyr Begeyev](https://github.com/taiyrbegeyev). The iOS app 
will be developed later.

## Design

* The mockup of the Android app can be found on [Figma](https://www.figma.com/file/u1rvftO0KvVqqFpn2mYC1P/Android?node-id=0%3A1).
* The Cloud Firestore database [schema](DB_SCHEMA.md).

## Tech Stack

* Firebase (Auth, Cloud Firestore (NoSQL), Storage (Google Cloud Platform))
* Java
* Kotlin (In the future)
* Android Jetpack (Navigation Component, androidx, etc.)
* Glide
* FirebaseUI

## Enabled Features

* Email Link Sign-in with Dynamic Links
* Anonymous Sign-in
* Posting items with a picture (from gallery or taking a photo inside the app),
title, category, condition, description, price, and preferred payment options.
* Seeing the list of items posted in the descending order of creation time.
* Seeing the item's details, which also include two buttons (Email & WhatsApp) to contact the owner.

## Miscellaneous

1. The `app/google-services.json` file is removed from the repository since it contains sensitive information. You'll need to provide your own `app/google-services.json` file to build the app.
