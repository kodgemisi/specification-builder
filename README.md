# Specification Builder

A tiny library that helps developers to build and combine complex specifications together with Spring Data JPA.

## Quick Start
It's enough to add this dependency to use this library.
```xml
<dependencies>
  <dependency>
    <groupId>com.github.kodgemisi</groupId>
    <artifactId>specification-builder</artifactId>
    <version>${specification-builder.version}</version>
  </dependency>
</dependencies>

<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

## How To Use

```java
  Specification<Person> specification = GenericSpecificationBuilder.<Person>of(Person.class)
 				.join("accounts")
 				.joinFetch("notifications")
 				.equals("name", name)
 				.equals("company.name", companyName, RelationType.TO_ONE)
 				.equals("addresses.type", addressType, , RelationType.TO_MANY)
 				.greaterThan("wage.amount", minWageAmount, RelationType.TO_ONE)
 				.greaterThanOrEqualTo("birthDate", minBirthDate)
 				.lessThanOrEqualTo("birthDate", maxBirthDate)
 				.like("about", keyword)
 				.likeIgnoreCase("about", keyword)
 				.in("city", cities)
 				.custom(customSpecification)
 				.build();
```
There is just `GenericSpecificationBuilder` class to build your specification as you see above. 
You must define your entity in `of(Person.class)` method and then you can chain your query parameters.
When you are done with your query, just `build()` it. That's all.

- `join` and `joinFetch`: You can make join or join fetch if you need
- `equals(key, value)`: `key` paramater is the field of your entity and `value` is parameter
- `equals(key, value, relationType)`: You can also build a query with filtering relations of an entity.
There is `relationType` enum parameter which provided by the library.
It is used by `RelationType.TO_MANY` or `RelationType.TO_ONE` according to relation of your entities between.

**`RelationType` is also applicable to other methods like `greaterThan` or `like` etc..**

There is also a good feature which provides you to add a custom specifications that the library can't handle.
As you see in the example above, just add `custom(spec)` method to the chain and your specification will be added.
 
# LICENSE

 © Copyright 2018 Kod Gemisi Ltd.

 Mozilla Public License 2.0 (MPL-2.0)

 https://tldrlegal.com/license/mozilla-public-license-2.0-(mpl-2)

 MPL is a copyleft license that is easy to comply with. You must make the source code for any of your changes available under MPL, but you can combine the MPL software with proprietary code, as long as you keep the MPL code in separate files. Version 2.0 is, by default, compatible with LGPL and GPL version 2 or greater. You can distribute binaries under a proprietary license, as long as you make the source available under MPL.

 [See Full License Here](https://www.mozilla.org/en-US/MPL/2.0/)
