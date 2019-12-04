package com.lhldyf.gallery.java.antlr.tw.hive;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限语句解析运行时实体对象
 * @author lhldyf
 * @date 2019-12-01 17:19
 */
@Data
public class HivePermitParseRuntimeEntity implements HiveSqlParseRuntimeEntity {

    /**
     * 授权 OR 收权
     */
    private String sqlType;

    /**
     * 授权对象的角色列表，比如 to role role1中的role1
     */
    private Set<String> roles = new HashSet<>();

    /**
     * 授权对象的用户列表，比如 to user user1中的user1
     */
    private Set<String> users = new HashSet<>();

    /**
     * 授权对象的用户组列表，比如 to group group1中的group1
     */
    private Set<String> groups = new HashSet<>();

    /**
     * 授权的类型，比如select/delete/insert/update
     */
    private Set<String> opTypes = new HashSet<>();

    /**
     * 授权的载体类型，比如table/database/role
     */
    private String payloadType;

    /**
     * 授权的载体，比如所有的表，payloadType是table,这里就是*，或者指定某个表
     */
    private Set<String> payloads = new HashSet<>();
}
