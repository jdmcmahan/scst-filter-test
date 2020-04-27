package com.test.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FooEvent {
    private String id;
    private String fooId;
}
