/**
 * Class: Role.java
 *
 * Class associated with the roles a user can have (1-> customer, 2-> trainer, 3-> admin)
 *
 * Fields:
 * - id: role id (internal)
 * - roleName: name of the role
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.model;

public class Role {

    private int id;
    private String roleName;

    public Role(){

    }

    public Role(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
