package io.github.saktools.utils.sql;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TreeClosure<IdType> {

    private String tableName;
    private String tableClosureName;

    public TreeClosure(String tableName, String tableClosureName) {
        this.tableName = tableName;
        this.tableClosureName = tableClosureName;
    }


    private Integer calcFinalSortOrder(IdType parentId, Integer sortOrder, Function<String, Integer> getMaxSortOrder) {
        Integer finalSortOrder = 1;
        Integer maxSortOrder = 0;
        if (sortOrder >= 1) finalSortOrder = sortOrder;
        if (sortOrder < 1) finalSortOrder = 1;
        if (sortOrder != null) {
            // 获取当前最大的 sortOrder 值, 如无则使用 1
            String sqlMaxSortOrder;
            if (parentId != null) {
                sqlMaxSortOrder = "select max(sort_order) from " + this.tableName + " where parent_id = " + parentId;
            } else {
                sqlMaxSortOrder = "select max(sort_order) from " + this.tableName + " where parent_id is null";
            }
            maxSortOrder = getMaxSortOrder.apply(sqlMaxSortOrder);
            maxSortOrder = maxSortOrder == null ? maxSortOrder : 1;
        }

        return Math.min(finalSortOrder, maxSortOrder + 1);
    }

    /**
     * 自动插入一个节点, 并同步对应的 closure 表
     *
     * @param parentId 父Id, 可选, 为null则代表该节点为1级节点
     * @param sortOrder 排序号, 可选, 指定该节点在同级节点中的排序; 如果超过该层级节点的最大sortOrder+1, 则强制设置为 sortOrder+1
     * @param sqlExecuter sql 执行器, 无返回结果
     * @param getMaxSortOrder sql 执行器, 返回 maxSortOrder, 入参为
     * @param saveEntity sql 执行器, 返回 entityId
     * @param getParentId sql 执行器, 返回 parentId
     */
    public void insertNode(IdType parentId, Integer sortOrder, Consumer<String> sqlExecuter, Function<String, Integer> getMaxSortOrder, Function<Integer, IdType> saveEntity, Function<String, IdType> getParentId) {
        // step 1: 计算最终的 sortOrder
        Integer finalMaxSortOrder = calcFinalSortOrder(parentId, sortOrder, getMaxSortOrder);

        // step 2: 更新同级 >= sortOrder 的所有节点, sortOrder+1
        sqlExecuter.accept("update " +
                this.tableName + " as tr, " +
                "(select id from " + this.tableName + " where sort_order >= " + finalMaxSortOrder + " as ref " +
                "set tr.sort_order = tr.sort_order + 1 " +
                "where ref.id = tr.id");

        // step 3: 保存 entity 并返回 id
        IdType entityId = saveEntity.apply(finalMaxSortOrder);

        // step 4: 向 closure 表插入自己
        //      insert into ${closureTableName} (id_ancestor, id_descendant, distance) values (?, ?, ?)`, `[${entityId}, ${entityId}, 0]
        sqlExecuter.accept("insert into " + this.tableClosureName + " (id_ancestor, id_descendant, distance) values (" + entityId + ", " + entityId + ", 0)");

        // step 5: 依次查询所有上级 id, 并插入到  closure
        IdType pid = parentId;
        ArrayList list = new ArrayList();
        Integer distance = 1;
        while (pid != null) {
            sqlExecuter.accept("insert into " + this.tableClosureName + " (id_ancestor, id_descendant, distance) values (" + pid + ", " + entityId + ", " + distance + ")");
            pid = getParentId.apply("select parent_id from " + this.tableName + " where id = " + pid);
            distance++;
        }
    }

}
