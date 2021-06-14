


## 新增一个节点

#### 接口入参:

* entity: 不包含公共属性, eg: parentId、sortOrder、isDelete、isEnable
* parentId: 可选
* sortOrder: 排序号, 小于1的修改为1
    - null=自动取同级最大 或 1
    - 数字: 用户操作 - 同级向下插入、同级向上插入
* 函数 - sql 执行器 - 无返回结果, 入参为 string 或 [string, [args]]
* 函数 - 特殊 sql 执行器
    - 根据 id 查询其下级的最大 sortOrder
    - 根据 id 查询 parentId
    - 根据 sortOrder 保存 entity 并返回 id

#### 执行的 sql 语句

* 1、计算最终的 sortOrder - finalSortOrder
    - 入参 sortOrder 为 >= 1 时: finalSortOrder=入参sortOrder
    - 入参 sortOrder 为 < 1 时: finalSortOrder=1
    - 入参 sortOrder > 当前同级最大sortOrder+1 时: 暂不考虑
    - 入参 sortOrder 为 null 时: 查询当前最大的 sortOrder 并 + 1
        + `select MAX(sort_order) from ${tableName};`
* 2、更新同级 >= sortOrder 的所有节点, sortOrder+1
    - `update ${tableName} as tr, (select id from ${tableName} where sort_order > ${sortOrder}) as ref
      set tr.sort_order = tr.sort_order + 1
      where ref.id = tr.id`
* 2、保存 entity: 拿到 entityId  (用 sortOrder 换 entityId)
* 3、向 closure 表插入自己
    - `insert into ${closureTableName} (id_ancestor, id_descendant, distance) values (?, ?, ?)`, `[${entityId}, ${entityId}, 0]`
* 4、依次查询所有上级 id, 并插入到  closure
    - parentId 不存在时: 忽略该步骤
    - parentId 存在时: 执行该步

