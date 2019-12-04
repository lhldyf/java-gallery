package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseResult;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Hive SQL解析结果的数据结构
 * @author lhldyf
 * @date 2019-11-25 18:32
 */
@ToString
@Data
public class HivePermitParseResult implements SqlParseResult {

    private String sqlType;

    /**
     * 授权对象的角色列表，比如 to role role1中的role1
     */
    private List<String> permsRoleEntities;

    /**
     * 授权对象的用户列表，比如 to user user1中的user1
     */
    private List<String> permsUserEntities;

    /**
     * 授权对象的用户组列表，比如 to group group1中的group1
     */
    private List<String> permsGroupEntities;

    /**
     * 授权的类型，比如select/delete/insert/update
     */
    private List<String> permsCategorys;

    /**
     * 授权的载体类型，比如table/database/role
     */
    private String permsPayloadType;

    /**
     * 授权的载体，比如所有的表，payloadType是table,这里就是*，或者指定某个表
     */
    private List<String> permsPayloads;


}
