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
  - All the functionality is only applied if your Response´s status code is `200`
  - If you don't use class path scanning (hint: [you shouldn't]), you need to list `de.larscheidschmitzhermes.collections.CollectionFilter` in your `javax.ws.rs.core.Application`. (Or whereever else you register your resource classes)

License
----

MIT

   [you shouldn't]: <https://blogs.oracle.com/japod/entry/when_to_use_jax_rs>
