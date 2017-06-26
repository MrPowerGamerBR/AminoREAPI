# AminoREAPI
## **Amino** **R**eversed **E**nginnered **API**

### What is this?
This is a WIP library to connect and interact with communities in [Amino](http://aminoapps.com/). I created this for a few reasons:

1. Nobody has done this before.
2. I wanted to see if it was easy to at least interact with the Amino closed API (and it was! I wonder why they don't release an official API for Amino because the current closed API is very easy to understand! Congratz to the API developers!)
3. I want to integrate Amino-related commands to my Discord Bot: https://loritta.website/

### How to Use
This is very WIP, so there are some issues with it and you can't do a lot of things with it yet... one of the biggest problems is that you **need to have an Android device** since you need to dump a valid device ID, currently there isn't any known way to create a valid Narvii pseudo random device ID. (if it is an invalid API the Endpoint kicks you due to invalid machine and/or device)

(If you want to dump your device ID, download any packet capturer, logout from your Amino account, log in again and track down the device ID in the packet dump)

Also, only email accounts are implemented. (phones will be implemented soon(tm))

**All the examples are in Kotlin, the library wasn't tested with pure Java yet, but it should work**

## Almost all the functions doesn't return a "valid" response yet, most of them only outputs the Endpoint response to the console, soon(tm) they will return something useful.

#### Creating an account
(Doesn't return anything yet, only outputs in the console the JSON payload)
```
Amino.createAccount("email", "password", "device ID", "nickname");
```

#### Creating an AminoClient
This will create an AminoClient, allowing you to interact with the Amino Network.
```
var client = AminoClient("email", "password", "device ID");
```

After initializing the client, use `client.login` to get your account SID.

#### Get Community By ID
Gets an community by its ID, to get your community ID, open the invite link for it in your browser, open the page source code tool and search for "deeplink"
```
var community = client.getCommunityById("community ID");
```
#### Join Community
There is two ways to join an community, one of them is if the community is "searchable" and the other one is used if your community is private.

Trying to join a private community without an invite link causes an "Access Denied" Endpoint response.
```
community.join(); // If the community doesn't require an invite link

community.join("invite link"); // If the community requires an invite link
```
#### Leave Community
Leaves the community.
```
community.leave();
```

#### Get Community Blog Feed
Gets the community blog feed in a list form.
```
// start, size.
// You can skip some posts by editing the start variable
community.getBlogFeed(0, 25);
```

### Issues
* You can only use accounts registered by email (soon phone support!)
* You need to dump your device ID by... well, inspecting the packets sent when logging in via Amino, I will try to add a random device ID generator later.