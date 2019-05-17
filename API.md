
# Amino Reverse Engineered API

## What is this?

This is a work in progress documentation of the [Amino](https://aminoapps.com/) private API, making it easier for other developers to implement libraries on the Amino network.

## Attention!

Decompiling and reverse engineering Amino is against Narvii's Terms of Service, we aren't responsible if your account or device gets suspended from Amino!

We are reverse engineering Amino since 2017, and as of now nobody did get suspended from the service itself, so just behave, do the right thing, don't spam the API and respect the official app rules and limitations and you will (I hope) be fine!

## API Implementations
|Name|Author|Language|
|--|--|--|
|[AminoREAPI](https://github.com/MrPowerGamerBR/AminoREAPI)|[@MrPowerGamerBR](https://github.com/MrPowerGamerBR)|Kotlin
|[Amino.JS](https://github.com/AminoJS/Amino.JS)|[@AminoJS](https://github.com/AminoJS)|JavaScript
|[AminoAcids](https://github.com/basswaver/AminoAcids)|[@basswaver](https://github.com/basswaver)|Python

## Getting Started

### Acquiring Device IDs

Currently Narvii's encryption isn't reverse engineered, so generating device IDs programatically is impossible.

#### Getting Device ID from Amino (Web Client)

This is the easiest way to acquire a valid device ID, login in your account @ [aminoapps.com](https://aminoapps.com) and open your browser developer tools tab, go to "Network", check any requests to `aminoapps.com` and check the cookies header, copy the `device_id` cookie and change everything to uppercase, that's it!

#### Getting Device ID from Amino (App)

You will need to intercept connections from your phone.

### API Info
All APIs are prefixed with `http://service.narvii.com/api/v1` unless noted otherwise.

All APIs has the `NDCDEVICEID` and `NDCAUTH` header unless noted otherwise.

All payloads are JSON payloads unless noted otherwise.

#### Headers
`NDCDEVICEID` is your device ID
`NDCAUTH` is your session ID in the following format: `sid=YourSessionIdHere`

### Connecting to Amino
`POST /g/s/auth/login`

#### Headers
`NDCDEVICEID` 

#### Payload
|Name|Value Type|Description|
|--|--|--|
|`deviceID`|String|
|`clientType`|Integer|Unknown, always 100
|`action`|String|Unknown, always "normal"
|`timestamp`|Long|
|`email`|String|Only if logging in via email
|`secret`|String|Prefixed with `0 `, example: `0 YourPass`

#### Response
