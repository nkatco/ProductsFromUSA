package com.example.productsfromusa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateData {
    private String user_id;
    private String state;
    private Object data;
}
