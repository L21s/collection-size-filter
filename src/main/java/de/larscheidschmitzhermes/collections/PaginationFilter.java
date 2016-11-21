package de.larscheidschmitzhermes.collections;

import de.larscheidschmitzhermes.collections.exceptions.PaginationParamException;
import de.larscheidschmitzhermes.collections.interfaces.Paginated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Provider
@Priority(2000)
@Paginated
public class PaginationFilter implements ContainerResponseFilter {

    private Logger logger = LoggerFactory.getLogger(PaginationFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getEntity()==null){
            logger.debug("Entity is null, ignoring.");
            return;
        }
        if (!(responseContext.getEntity() instanceof Collection)){
            logger.debug("Entity is not a collection, ignoring.");
            return;
        }
        Integer limit = extractLimit(requestContext);
        Integer offset = extractOffset(requestContext);
        Collection sublist = getSublistOfCollection((Collection) responseContext.getEntity(), offset, limit);
        responseContext.setEntity(sublist);
        setResponseHeaders(responseContext, limit, offset, ((Collection) responseContext.getEntity()).size());
    }

    private Integer extractLimit(ContainerRequestContext requestContext) {
        return extractParameterAsInteger("limit", requestContext);
    }

    private Integer extractOffset(ContainerRequestContext requestContext) {
        return extractParameterAsInteger("offset", requestContext);
    }

    private Integer extractParameterAsInteger(String paramName, ContainerRequestContext requestContext) {
        String param = requestContext.getUriInfo().getQueryParameters().getFirst(paramName);
        Integer value = convertParamStringToInt(param);
        if (value!=null){
            return value;
        }
        param = requestContext.getHeaders().getFirst(paramName);
        return convertParamStringToInt(param);
    }

    private Integer convertParamStringToInt(String param){
        if (param == null) {
            return null;
        }
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new PaginationParamException("Failed to parse integer from " + param, e);
        }
    }

    private <T> Collection<T> getSublistOfCollection(Collection<T> collection, Integer offset, Integer limit) {
        offset = calculateCorrectOffset(collection,offset);
        limit = calculateCorrectLimit(collection, offset, limit);
        List<T> list = new ArrayList<T>(collection);
        return list.subList(offset, limit);
    }

    private Integer calculateCorrectLimit(Collection collection, Integer offset, Integer limit) {
        if (limit == null){
            return collection.size();
        }
        if (limit > collection.size()){
            return collection.size();
        }
        if (limit + offset > collection.size()){
            return collection.size();
        }
        return limit+offset;
    }

    private Integer calculateCorrectOffset(Collection collection, Integer offset) {
        if (offset == null){
            return 0;
        }
        if (offset > collection.size()){
            return collection.size();
        }
        return offset;
    }

    private void setResponseHeaders(ContainerResponseContext responseContext, Integer limit, Integer offset, int totalSize) {
        if (limit != null) {
            responseContext.getHeaders().put("limit", Arrays.asList(limit));
        }
        if (offset != null) {
            responseContext.getHeaders().put("offset", Arrays.asList(offset));
        }
        responseContext.getHeaders().put("total-result-count", Arrays.asList(totalSize));
    }
}
