package com.lhldyf.gallery.java.antlr.tw.hive;

import com.lhldyf.gallery.java.antlr.tw.SqlParseConstant;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.Set;

import static com.lhldyf.gallery.java.antlr.tw.SqlParseConstant.*;

/**
 * Hive 权限类操作的解析器
 * @author lhldyf
 * @date 2019-12-01 18:57
 */
public class AbstractHivePermitSqlParser
        extends AbstractHiveSqlParser<HivePermitParseRuntimeEntity, HivePermitParseResult> {

    @Override
    protected HivePermitParseResult constructParseResult(HivePermitParseRuntimeEntity runtimeEntity) {
        HivePermitParseResult result = new HivePermitParseResult();
        result.setPermsCategorys(new ArrayList<>(runtimeEntity.getOpTypes()));
        result.setPermsGroupEntities(new ArrayList<>(runtimeEntity.getGroups()));
        result.setPermsPayloads(new ArrayList<>(runtimeEntity.getPayloads()));
        result.setPermsPayloadType(runtimeEntity.getPayloadType());
        result.setPermsRoleEntities(new ArrayList<>(runtimeEntity.getRoles()));
        result.setPermsUserEntities(new ArrayList<>(runtimeEntity.getUsers()));
        result.setSqlType(runtimeEntity.getSqlType());
        return result;
    }

    @Override
    protected void endParseCurrentNode(ASTNode ast, HivePermitParseRuntimeEntity runtimeEntity) {

    }

    @Override
    protected Set<String> parseCurrentNode(ASTNode ast, HivePermitParseRuntimeEntity runtimeEntity, Set<String> set) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_GRANT_ROLE:
                    runtimeEntity.setPayloadType(SqlParseConstant.PAYLOAD_TYPE_ROLE);
                    for (int i = 0; i < ast.getChildCount(); i++) {
                        ASTNode child = (ASTNode) ast.getChild(i);
                        if (HiveParser.Identifier == child.getType()) {
                            runtimeEntity.getPayloads().add(child.getText());
                        }
                    }
                    break;
                case HiveParser.TOK_PRIV_OBJECT:
                    ASTNode child = (ASTNode) ast.getChild(0);
                    if (HiveParser.TOK_DB_TYPE == child.getType()) {
                        runtimeEntity.setPayloadType(SqlParseConstant.PAYLOAD_TYPE_DB);
                        runtimeEntity.getPayloads().add((child.getChild(0)).getText());
                    } else if (HiveParser.TOK_TABLE_TYPE == child.getType()) {
                        runtimeEntity.setPayloadType(SqlParseConstant.PAYLOAD_TYPE_TABLE);
                        ASTNode grandson = (ASTNode) child.getChild(0);
                        if (grandson.getChildCount() == 1) {
                            runtimeEntity.getPayloads().add(grandson.getChild(0).getText());
                        } else if (grandson.getChildCount() == 2) {
                            runtimeEntity.getPayloads()
                                         .add(grandson.getChild(0).getText() + "." + grandson.getChild(1).getText());
                        }
                    } else {
                        break;
                    }

                    break;
                case HiveParser.TOK_ROLE:
                    runtimeEntity.getRoles().add(ast.getChild(0).getText());
                    break;
                case HiveParser.TOK_USER:
                    runtimeEntity.getUsers().add(ast.getChild(0).getText());
                    break;
                case HiveParser.TOK_GROUP:
                    runtimeEntity.getGroups().add(ast.getChild(0).getText());
                    break;
                case HiveParser.TOK_PRIV_CREATE:
                    runtimeEntity.getOpTypes().add(PERMIT_TYPE_CREATE);
                    break;
                case HiveParser.TOK_PRIV_ALL:
                    runtimeEntity.getOpTypes().add(PERMIT_TYPE_ALL);
                    break;
                case HiveParser.TOK_PRIV_DELETE:
                    runtimeEntity.getOpTypes().add(PERMIT_TYPE_DELETE);
                    break;
                case HiveParser.TOK_PRIV_SELECT:
                    runtimeEntity.getOpTypes().add(PERMIT_TYPE_SELECT);
                    break;
                case HiveParser.TOK_PRIV_ALTER_DATA:
                    runtimeEntity.getOpTypes().add(PERMIT_TYPE_UPDATE);
                    break;
                default:
                    break;
            }
        }
        return set;
    }

    @Override
    protected void prepareToParseCurrentNodeAndChildren(ASTNode ast, HivePermitParseRuntimeEntity runtimeEntity) {

    }

    @Override
    protected HivePermitParseRuntimeEntity constructRuntimeEntity(String sqlType) {
        HivePermitParseRuntimeEntity entity = new HivePermitParseRuntimeEntity();
        entity.setSqlType(sqlType);
        return entity;
    }
}
