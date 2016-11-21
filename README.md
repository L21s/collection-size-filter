[![Build Status](https://travis-ci.org/larscheid-schmitzhermes/collection-size-filter.svg?branch=master)](https://travis-ci.org/larscheid-schmitzhermes/collection-size-filter)
[![codecov](https://codecov.io/gh/larscheid-schmitzhermes/collection-size-filter/branch/master/graph/badge.svg)](https://codecov.io/gh/larscheid-schmitzhermes/collection-size-filter)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1543b17855c146ad9d6881585d88220a)](https://www.codacy.com/app/tobilarscheid/collection-size-filter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=larscheid-schmitzhermes/collection-size-filter&amp;utm_campaign=Badge_Grade)

# JAX-RS Collection Filter

Just a small helper for making collections in your RESTful APIs even more beautiful:
  - When returning collections, add a header stating the number of elements. Clients can then for example HEAD the resource and simply get a count.
  - Automatic pagination of your collections

To get started, get the latest version from jitpack:

https://jitpack.io/#larscheid-schmitzhermes/collection-size-filter/

## Result Count (Collection Size)

For the result count simply annotate your service method with `@CollectionSize`.

```java
@Path("/entities")
@CollectionSize
public Response getMyEntities(){
    Collection<String> entities = Arrays.asList("A", "B", "C");
    return Response.ok(entities).build();
}
```
The answer will look like this:
```HTTP
HTTP/1.1 200
result-count: 3
Content-Type: application/json
[
  "A", "B", "C"
]
```
The `result-count` header is automatically added to your Response. You can customize the header name by specifiying it in the annotation like so: `@Collection(headerName="my-fancy-header-name")`
## Fully Automatic Pagination

For automatic pagination, annotate your service method with `@Paginated`.

```java
@Path("/entities")
@Paginated
@CollectionSize
@GET
public Response getMyEntities(){
    Collection<String> entities = Arrays.asList("A", "B", "C", "D", "E");
    return Response.ok(entities).build();
}
```
Suppose a request like this:
```HTTP
GET /entities HTTP/1.1
offset: 1
limit: 2
```
or alternatively like this:
```HTTP
GET /entities?offset=1&limit=2 HTTP/1.1
````

(Even a mix will work, query params always take precedence)

The answer will look like this:
```HTTP
HTTP/1.1 200
total-result-count: 5
offset: 1
limit: 2
result-count: 2
Content-Type: application/json
[
  "B", "C"
]
```
Notice that I annotated `@CollectionSize` once again to get my resulting collection's size into the headers! This will usually be the same as limit, only for the last piece of a collection it might be smaller.

## Hints
  - The functionality is generally applied for every status code and request type. When the returned entity is no collection, `@CollectionSize` will set `result-count: 1`. `@Paginated` ignores non-collection type entities. 
  - If you don't use class path scanning (hint: [you shouldn't]), you need to list `de.larscheidschmitzhermes.collections.CollectionFilter` and/or `de.larscheidschmitzhermes.collections.PaginationFilter` in your `javax.ws.rs.core.Application`. (Or whereever else you register your resource classes)

License
----

MIT

   [you shouldn't]: <https://blogs.oracle.com/japod/entry/when_to_use_jax_rs>
