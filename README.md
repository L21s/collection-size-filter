[![Build Status](https://travis-ci.org/larscheid-schmitzhermes/collection-size-filter.svg?branch=master)](https://travis-ci.org/larscheid-schmitzhermes/collection-size-filter)
[![codecov](https://codecov.io/gh/larscheid-schmitzhermes/collection-size-filter/branch/master/graph/badge.svg)](https://codecov.io/gh/larscheid-schmitzhermes/collection-size-filter)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1543b17855c146ad9d6881585d88220a)](https://www.codacy.com/app/tobilarscheid/collection-size-filter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=larscheid-schmitzhermes/collection-size-filter&amp;utm_campaign=Badge_Grade)

# JAX-RS Collection Size Filter

Just a very small helper for making your RESTful APIs even more beautiful: When returning collections, add a header stating the number of elements. Clients can then for example HEAD the resource and simply get a count.

To get started, get the latest version from jitpack:

https://jitpack.io/#larscheid-schmitzhermes/collection-size-filter/

Then, simply annotate your service Method with `@Collection`.

```java
@Collection
public Response getMyEntities(){
    Collection<String> entities = Arrays.asList("A", "B", "C");
    return Response.ok(entities).build();
}
```

```HTTP
HTTP/1.1 200
result-count: 3
...
```

The `result-count` header is automatically added to your Response. You can customize the header name by specifiying it in the annotation like so: `@Collection(headerName="my-fancy-header-name")`

## Hints
  - The functionality is generally applied for every status code and request type. When the returned entity is no collection, the filter will set `result-count: 1`.
  - If you don't use class path scanning (hint: [you shouldn't]), you need to list `de.larscheidschmitzhermes.collections.CollectionSizeFilter` in your `javax.ws.rs.core.Application`. (Or whereever else you register your resource classes)

License
----

MIT

   [you shouldn't]: <https://blogs.oracle.com/japod/entry/when_to_use_jax_rs>
