# Specification Builder

A project that helps developers to build and combine complex specifications together with Spring Framework.

## How To Use

```
  Specification<Person> specification = GenericSpecificationBuilder.<Person>of(Person.class)
 				.join("accounts")
 				.joinFetch("notifications")
 				.equalsWithToOneRelation("company.name", companyName)
 				.equalsWithToManyRelation("addresses.type", addressType)
 				.greaterThanOrEqualTo("birthDate", minBirthDate)
 				.lessThanOrEqualTo("birthDate", maxBirthDate)
 				.like("about", keyword)
 				.in("city", cities)
 				.build();
```

