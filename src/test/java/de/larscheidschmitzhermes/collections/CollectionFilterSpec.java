package de.larscheidschmitzhermes.collections;

import de.larscheidschmitzhermes.collections.interfaces.Collection;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionFilterSpec {
    @Mock
    private ContainerRequestContext requestCtx;
    @Mock
    private ContainerResponseContext responseCtx;

    private CollectionFilter filter = new CollectionFilter();

    private static String CUSTOM_HEADER_NAME = "custom-header";
    private static java.util.Collection<Object> COLLECTION_SIZE_42;
    private static Annotation[] ANNOTATION_WITH_CUSTOMER_HEADER_NAME;
    private static Annotation[] DEFAULT_ANNOTATION;

    @BeforeClass
    public static void setUp() {
        COLLECTION_SIZE_42 = collectionWithSize42();
        ANNOTATION_WITH_CUSTOMER_HEADER_NAME = new Annotation[]{annotationWithCustomHeaderName()};
        DEFAULT_ANNOTATION = new Annotation[]{defaultAnnotation()};
    }

    private static java.util.Collection collectionWithSize42() {
        java.util.Collection<Object> collection = mock(java.util.Collection.class);
        when(collection.size()).thenReturn(42);
        return collection;
    }

    private static Collection annotationWithCustomHeaderName() {
        Collection annotation = mock(Collection.class);
        when(annotation.headerName()).thenReturn(CUSTOM_HEADER_NAME);
        return annotation;
    }

    private static Collection defaultAnnotation(){
        Collection annotation = mock(Collection.class);
        when(annotation.headerName()).thenReturn(Collection.DEFAULT_HEADER);
        return annotation;
    }

    @Test
    public void shouldAppendHeaderBasedOnReturnedCollectionsSize() throws IOException {
        when(responseCtx.getEntity()).thenReturn(COLLECTION_SIZE_42);
        when(responseCtx.getEntityAnnotations()).thenReturn(DEFAULT_ANNOTATION);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);

        filter.filter(requestCtx, responseCtx);

        assertThat(headers.get(Collection.DEFAULT_HEADER), is(Arrays.asList(42)));
    }

    @Test
    public void shouldAllowForNonDefaultHeaderName() throws IOException {
        when(responseCtx.getEntity()).thenReturn(COLLECTION_SIZE_42);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntityAnnotations()).thenReturn(ANNOTATION_WITH_CUSTOMER_HEADER_NAME);

        filter.filter(requestCtx, responseCtx);

        assertThat(headers.get(Collection.DEFAULT_HEADER), is(nullValue()));
        assertThat(headers.get(CUSTOM_HEADER_NAME), is(Arrays.asList(42)));
    }

    @Test
    public void shouldGracefullyHandleNullEntity() throws IOException {
        when(responseCtx.getEntity()).thenReturn(null);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntityAnnotations()).thenReturn(DEFAULT_ANNOTATION);

        filter.filter(requestCtx,responseCtx);

        assertThat(headers.get(Collection.DEFAULT_HEADER), is(nullValue()));
    }

    @Test
    public void shouldCorrectlyHandleNonCollectionEntity() throws IOException{
        when(responseCtx.getEntity()).thenReturn(new Object());
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntityAnnotations()).thenReturn(DEFAULT_ANNOTATION);

        filter.filter(requestCtx,responseCtx);

        assertThat(headers.get(Collection.DEFAULT_HEADER),is(Arrays.asList(1)));
    }

}