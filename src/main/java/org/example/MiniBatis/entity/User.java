package org.example.MiniBatis.entity;

import lombok.Data;
import org.example.MiniBatis.annotation.Table;

@Data
@Table(tableName = "user")
public class User {
    private Integer id;
    private String name;
    private Integer age;
}
