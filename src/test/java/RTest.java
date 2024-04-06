import com.github.gpor0.jaffas.rest.R;
import com.github.gpor0.jooreo.operations.DataOperation;
import com.github.gpor0.jooreo.operations.FilterOperation;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RTest {

    @Test
    public void shouldParseSingleFilterComma() {

        UriInfo uriInfo = buildUriInfo("company%3ALIKEIC%3A%25mal%2C%25");
        DataOperation[] dataOperations = R.buildOperations(uriInfo);
        assert dataOperations != null;
        assert dataOperations.length == 1;
        FilterOperation filterOperation = (FilterOperation) dataOperations[0];
        assert filterOperation.getField().equals("company");
        assert filterOperation.getOperation().equals("LIKEIC");
        assert filterOperation.getValue().equals("%mal,%");
    }

    @Test
    public void shouldParseDoubleFilterComma() {

        UriInfo uriInfo = buildUriInfo("company%3ALIKEIC%3A%25mal%2C%25,name%3ALIKE%3A%25mnl%2C%25");
        DataOperation[] dataOperations = R.buildOperations(uriInfo);
        assert dataOperations != null;
        assert dataOperations.length == 2;
        FilterOperation filterOperation = (FilterOperation) dataOperations[0];
        assert filterOperation.getField().equals("company");
        assert filterOperation.getOperation().equals("LIKEIC");
        assert filterOperation.getValue().equals("%mal,%");
        filterOperation = (FilterOperation) dataOperations[1];
        assert filterOperation.getField().equals("name");
        assert filterOperation.getOperation().equals("LIKE");
        assert filterOperation.getValue().equals("%mnl,%");
    }

    @Test
    public void shouldParseSingleFilter() {

        UriInfo uriInfo = buildUriInfo("company%3ALIKEIC%3A%25mal%25");
        DataOperation[] dataOperations = R.buildOperations(uriInfo);
        assert dataOperations != null;
        assert dataOperations.length == 1;
        FilterOperation filterOperation = (FilterOperation) dataOperations[0];
        assert filterOperation.getField().equals("company");
        assert filterOperation.getOperation().equals("LIKEIC");
        assert filterOperation.getValue().equals("%mal%");
    }

    @Test
    public void shouldParseDoubleFilter() {

        UriInfo uriInfo = buildUriInfo("company%3ALIKEIC%3A%25mal%25,name%3ALIKE%3A%25mnl%25");
        DataOperation[] dataOperations = R.buildOperations(uriInfo);
        assert dataOperations != null;
        assert dataOperations.length == 2;
        FilterOperation filterOperation = (FilterOperation) dataOperations[0];
        assert filterOperation.getField().equals("company");
        assert filterOperation.getOperation().equals("LIKEIC");
        assert filterOperation.getValue().equals("%mal%");
        filterOperation = (FilterOperation) dataOperations[1];
        assert filterOperation.getField().equals("name");
        assert filterOperation.getOperation().equals("LIKE");
        assert filterOperation.getValue().equals("%mnl%");
    }

    @Test
    public void shouldParseDoubleFilterBackwardsCompatible() {

        UriInfo uriInfo = buildUriInfo("company%3ALIKEIC%3A%25mal%25%2Cname%3ALIKE%3A%25mnl%25");
        DataOperation[] dataOperations = R.buildOperations(uriInfo);
        assert dataOperations != null;
        assert dataOperations.length == 2;
        FilterOperation filterOperation = (FilterOperation) dataOperations[0];
        assert filterOperation.getField().equals("company");
        assert filterOperation.getOperation().equals("LIKEIC");
        assert filterOperation.getValue().equals("%mal%");
        filterOperation = (FilterOperation) dataOperations[1];
        assert filterOperation.getField().equals("name");
        assert filterOperation.getOperation().equals("LIKE");
        assert filterOperation.getValue().equals("%mnl%");
    }

    private UriInfo buildUriInfo(String filterQp) {
        return new UriInfo() {
            @Override
            public String getPath() {
                return "/customers";
            }

            @Override
            public String getPath(boolean b) {
                return getPath();
            }

            @Override
            public List<PathSegment> getPathSegments() {
                return null;
            }

            @Override
            public List<PathSegment> getPathSegments(boolean b) {
                return getPathSegments();
            }

            @Override
            public URI getRequestUri() {
                try {
                    return new URI("http://localhost:8080/service/api/customers?rel=customerBranches&filter=company%3ALIKEIC%3A%25mal%2C%25");
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public UriBuilder getRequestUriBuilder() {
                return null;
            }

            @Override
            public URI getAbsolutePath() {
                try {
                    return new URI("http://localhost:8080/service/api/customers");
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public UriBuilder getAbsolutePathBuilder() {
                return null;
            }

            @Override
            public URI getBaseUri() {
                try {
                    return new URI("http://localhost:8080/service/api/");
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public UriBuilder getBaseUriBuilder() {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getPathParameters() {
                return new MultivaluedHashMap<>();
            }

            @Override
            public MultivaluedMap<String, String> getPathParameters(boolean b) {
                return getPathParameters();
            }

            @Override
            public MultivaluedMap<String, String> getQueryParameters() {
                return getQueryParameters(true);
            }

            @Override
            public MultivaluedMap<String, String> getQueryParameters(boolean b) {

                String qp = URLDecoder.decode(filterQp, StandardCharsets.UTF_8);
                String urlEscapedQp = filterQp;

                MultivaluedMap<String, String> map = new MultivaluedHashMap();
                map.put("rel", List.of("customerBranches"));
                map.put("filter", List.of(b ? qp : urlEscapedQp));

                return map;
            }

            @Override
            public List<String> getMatchedURIs() {
                return null;
            }

            @Override
            public List<String> getMatchedURIs(boolean b) {
                return null;
            }

            @Override
            public List<Object> getMatchedResources() {
                return null;
            }

            @Override
            public URI resolve(URI uri) {
                return null;
            }

            @Override
            public URI relativize(URI uri) {
                return null;
            }
        };
    }

}
