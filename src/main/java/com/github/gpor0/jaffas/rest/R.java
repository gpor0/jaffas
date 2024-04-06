package com.github.gpor0.jaffas.rest;

import com.github.gpor0.jooreo.operations.DataOperation;
import com.github.gpor0.jooreo.operations.FilterOperation;
import com.github.gpor0.jooreo.operations.OrderByOperation;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: gpor0
 */
public class R {

    public static final Pattern FILTER_PATTERN = Pattern.compile("(\\[[^\\[]*\\]|[^,])+");

    public static final String TOTAL_COUNT_HEADER = "X-Total-Count";
    public static final String HAS_NEXT_HEADER = "X-Has-Next";
    public static final String CURSOR_REF_HEADER = "X-Cursor-Ref";


    public static URI buildCreatedLink(final UriInfo uri, final Object createdId) {

        return uri.getAbsolutePathBuilder().path(createdId.toString())
                .build();
    }

    public static Optional<FilterOperation> getFilterOperation(UriInfo uri, String fieldName) {
        return Stream.of(buildOperations(uri))
                .filter(op -> op instanceof FilterOperation)
                .map(op -> (FilterOperation) op).filter(op -> fieldName.equals(op.getField())).findFirst();
    }

    public static DataOperation[] buildOperations(UriInfo uri) {

        String order = uri.getQueryParameters(false).getFirst("order");

        Stream<DataOperation> orderByOpsStream;
        if (order != null && !order.isBlank()) {
            orderByOpsStream = Stream.of(order.split(",")).map(f -> URLDecoder.decode(f, StandardCharsets.UTF_8)).map(OrderByOperation::parse);
        } else {
            orderByOpsStream = Stream.empty();
        }

        String filter = uri.getQueryParameters(false).getFirst("filter");

        Stream<DataOperation> filterOpsStream;
        if (filter != null && !filter.isBlank()) {
            filterOpsStream = FILTER_PATTERN.matcher(filter).results().map(MatchResult::group).map(f -> URLDecoder.decode(f, StandardCharsets.UTF_8)).map(FilterOperation::parse);
        } else {
            filterOpsStream = Stream.empty();
        }

        return Stream.concat(filterOpsStream, orderByOpsStream).toArray(DataOperation[]::new);
    }

    public static String snakeToCamelCase(String s) {
        String[] parts = s.toLowerCase().split("_");
        StringBuffer camelCaseString = new StringBuffer();
        int i = 0;
        for (String part : parts) {
            if (i == 0) {
                camelCaseString.append(part);
            } else {
                camelCaseString.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }
            i++;
        }
        return camelCaseString.toString();
    }

    /**
     * Returns information whether str is null, empty or contains only blank spaces
     *
     * @param str
     * @return
     */
    public static boolean isBlank(final String str) {
        return str == null || str.isBlank();
    }

    /**
     * Returns all elements found in set1 and not found in set2
     *
     * @param set1
     * @param set2
     * @return
     */
    public static <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        return set1.stream().filter(Predicate.not(set2::contains)).collect(Collectors.toSet());
    }

}
