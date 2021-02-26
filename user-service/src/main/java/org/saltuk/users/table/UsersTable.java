/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.users.table;

import org.saltuk.core.db.annotations.Field;
import org.saltuk.core.db.annotations.Id;
import org.saltuk.core.db.annotations.Table;

/**
 * Users Table 
 * @author saltuk
 */
@Table("users")
public class UsersTable {

    @Id
    private long _id;
    @Field(name = "username" , size = 20)
    private String username;
    @Field(name = "password", hidden = true, size = 100)
    private String password;
    @Field(name = "email", size =40)
    private String email;
    @Field(name = "name", size=150)
    private String name;
    @Field(name = "age")
    private int age;
}
