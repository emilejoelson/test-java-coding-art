package ma.codingart.testjava.dto.request;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import ma.codingart.testjava.enums.FieldType;
import ma.codingart.testjava.enums.Operator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest  {
    private String key;

    private Operator operator;

    private FieldType fieldType;

    private transient Object value;

    private transient Object valueTo;

    private transient List<Object> values;

}

