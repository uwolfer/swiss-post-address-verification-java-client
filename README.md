# swiss-post-address-verification-java-client

[![Build Status](https://travis-ci.org/uwolfer/swiss-post-address-verification-java-client.svg)](https://travis-ci.org/uwolfer/swiss-post-address-verification-java-client)
[![Release](https://jitpack.io/v/uwolfer/swiss-post-address-verification-java-client.svg)](https://jitpack.io/#uwolfer/swiss-post-address-verification-java-client)

A lightweight Java client implementation without application server dependencies of the free
[Swiss Post address verification API](https://www.post.ch/en/business/a-z-of-subjects/maintaining-addresses-and-using-geodata/address-verification).


## Getting Started

These instructions will get you ready to use this library.


### Prerequisites

This library depends on Java 7 or later. There are no more runtime dependencies.

You need a Swiss Post business account (can be registered for free) in order to
use the API. You can find contact information on the Swiss Post website in order
to request a full documentation of the API (you won't be able to use this
library without the official documentation!).


### Installing

You can add this library to your project as dependency with Gradle:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.uwolfer:swiss-post-address-verification-java-client:1.0'
}
```

Or with Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.uwolfer</groupId>
    <artifactId>swiss-post-address-verification-java-client</artifactId>
    <version>1.0</version>
</dependency>
```


### Using

```java
String endpointUrl = "https://webservices.post.ch/copy-path-from-documentation";
String userName = "TU_YOUR_ID";
String password = "your-password";
AddressVerificationClient addressVerificationClient = new AddressVerificationClient(endpointUrl, userName, password);

AddressVerificationRequest req = new AddressVerificationRequest();
req.setNames("First Last");
req.setStreet("Bahnhofstrasse");
req.setHouseNbr("1");
req.setZip("8001");
req.setTown("Zürich");

AddressVerificationResponse resp = addressVerificationClient.callWebService(req);
AddressVerificationResponse.Rows row = resp.getRows().get(0);
System.out.println(String.format("House Key: %s, Guaranteed Delivery: %s", row.getHouseKey(), row.getGuaranteedDelivery()));
// output: House Key: 57000040, Guaranteed Delivery: 2
```


## Versioning

This library uses [SemVer](http://semver.org/) for versioning.


## Authors

* **Urs Wolfer** - *Maintainer*


## License

This project is released under the Apache 2.0 license -
see the [LICENSE](LICENSE) file for details.
