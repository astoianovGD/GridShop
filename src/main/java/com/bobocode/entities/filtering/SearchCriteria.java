package com.bobocode.entities.filtering;

import com.bobocode.enums.SearchOperations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperations searchOperation;
    private Object value;
}
