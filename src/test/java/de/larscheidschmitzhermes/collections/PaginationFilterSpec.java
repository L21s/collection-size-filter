package de.larscheidschmitzhermes.collections;

import de.larscheidschmitzhermes.collections.exceptions.PaginationParamException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaginationFilterSpec {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ContainerRequestContext requestCtx;
    @Mock
    private ContainerResponseContext responseCtx;

    private PaginationFilter filter = new PaginationFilter();

    private static List<String> COLLECTION = Arrays.asList("0", "1", "2", "3", "4", "5");

    @Before
    public void prepareQueryParams() {
        when(requestCtx.getUriInfo().getQueryParameters()).thenReturn(new MultivaluedHashMap<String, String>());
    }

    @Test
    public void shouldDoNothingWithoutParams() throws IOException{
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);

        filter.filter(requestCtx,responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION);
        assertThat(responseCtx.getHeaders().get("limit"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("offset"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldLimit() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("2");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(0, 2));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(2)));
        assertThat(responseCtx.getHeaders().get("offset"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldOffset() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("2");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(2, COLLECTION.size()));
        assertThat(responseCtx.getHeaders().get("limit"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(2)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldOffsetAndLimit() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("1");
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("2");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(2, 3));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(1)));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(2)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }


    @Test
    public void shouldDealWithNullEntity() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(null);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("1");
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("2");

        filter.filter(requestCtx, responseCtx);

        assertThat(responseCtx.getHeaders().get("limit"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("offset"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(nullValue()));
    }

    @Test
    public void shouldDealWithLimitBiggerThanSize() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("100");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION);
        assertThat(responseCtx.getHeaders().get("offset"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(100)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldDealWithOffsetBiggerThanSize() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("100");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(Collections.EMPTY_LIST);
        assertThat(responseCtx.getHeaders().get("limit"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(100)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldDealWithOffsetAndLimitTogetherBiggerThanSize() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("3");
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("5");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(5,COLLECTION.size()));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(3)));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(5)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldDealWithEmptyCollection() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(Collections.EMPTY_LIST);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("20");
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("100");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(Collections.EMPTY_LIST);
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(20)));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(100)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(0)));
    }

    @Test
    public void shouldDealWithNonCollectionEntity() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(new Object());
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("20");
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("100");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, never()).setEntity(any());
        assertThat(responseCtx.getHeaders().get("limit"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("offset"), is(nullValue()));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(nullValue()));
    }

    @Test
    public void shouldGetParamsFromQuery() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        requestCtx.getUriInfo().getQueryParameters().put("limit", Arrays.asList("1"));
        requestCtx.getUriInfo().getQueryParameters().put("offset", Arrays.asList("2"));

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(2, 3));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(1)));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(2)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test
    public void shouldGetParamsFromHeadersAndQuery() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        when(requestCtx.getHeaders().getFirst("limit")).thenReturn("20");
        requestCtx.getUriInfo().getQueryParameters().put("limit", Arrays.asList("1"));
        when(requestCtx.getHeaders().getFirst("offset")).thenReturn("2");

        filter.filter(requestCtx, responseCtx);

        verify(responseCtx, times(1)).setEntity(COLLECTION.subList(2, 3));
        assertThat(responseCtx.getHeaders().get("limit"), is(Arrays.asList(1)));
        assertThat(responseCtx.getHeaders().get("offset"), is(Arrays.asList(2)));
        assertThat(responseCtx.getHeaders().get("total-result-count"), is(Arrays.asList(6)));
    }

    @Test(expected = PaginationParamException.class)
    public void shouldFailOnInvalidParams() throws IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseCtx.getHeaders()).thenReturn(headers);
        when(responseCtx.getEntity()).thenReturn(COLLECTION);
        requestCtx.getUriInfo().getQueryParameters().put("limit", Arrays.asList("abc"));
        requestCtx.getUriInfo().getQueryParameters().put("offset", Arrays.asList("def"));

        filter.filter(requestCtx, responseCtx);
    }
}