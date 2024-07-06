package ma.codingart.testjava.utils;


import java.util.ArrayList;
import java.util.Objects;
import ma.codingart.testjava.dto.request.FilterRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.request.SortRequest;
import ma.codingart.testjava.enums.FieldType;
import ma.codingart.testjava.enums.Operator;
import ma.codingart.testjava.enums.SortDirection;

public class QueryParser {
    public static SearchRequest createSearchRequest(String query,int page, int size){
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(new ArrayList<>());
        searchRequest.setSorts(new ArrayList<>());
        searchRequest.setSize(size);
        searchRequest.setPage(page);
        if(Objects.isNull(query) ||query.isEmpty()) return searchRequest;
        String[] queries= query.split("&");
        for(String q : queries){
            if(q.startsWith("sort")){
                String[] parts = q.split("[=,]");
                searchRequest.getSorts().add(new SortRequest(parts[1], SortDirection.valueOf(parts[2])));
                continue;
            }
            String[] parts = q.split("[:<>]");
            String fieldName = parts[0];
            String fieldType = parts[1];
            String operator = parts[2];
            String[] values = parts[3].split(",");
            FilterRequest filterRequest= FilterRequest.builder()
                    .key(fieldName)
                    .fieldType(FieldType.valueOf(fieldType))
                    .operator(Operator.valueOf(operator))
                    .value(values[0])
                    .build();
            if(values.length>1)  filterRequest.setValueTo(values[1]);
            searchRequest.getFilters().add(filterRequest);

        }
        return searchRequest;

    }
}

