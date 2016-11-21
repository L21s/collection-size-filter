package de.larscheidschmitzhermes.collections;

import de.larscheidschmitzhermes.collections.interfaces.CollectionSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;

@Provider
@CollectionSize
public class CollectionSizeFilter implements ContainerResponseFilter {

    private Logger logger = LoggerFactory.getLogger(CollectionSizeFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getEntity() == null) {
            logger.debug("Entity is null, not appending anything!");
            return;
        }
        responseContext.getHeaders().put(extractHeaderNameFromAnnotation(responseContext.getEntityAnnotations()), Arrays.asList(extractSizeFromEntity(responseContext.getEntity())));
    }

    private String extractHeaderNameFromAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof CollectionSize) {
                return ((CollectionSize) annotation).headerName();
            }
        }
        //this point is technically unreachable (otherwise there is a problem with jax-rs)
        //still, this exception is needed for the compiler to be happy
        throw new IllegalStateException("Missing required @CollectionSize annotation - this should not be possible.");
    }

    private Integer extractSizeFromEntity(Object entity) {
        if (entity instanceof java.util.Collection) {
            return ((java.util.Collection) entity).size();
        } else {
            logger.debug("Entity is {} and no collection. Returning size 1.", entity);
            return 1;
        }
    }
}
