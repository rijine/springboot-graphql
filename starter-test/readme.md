## 1. 示例的使用方法

该分支使用mybatis-plus增强，通过扩展SqlInjector，实现了类元数据查询能力增强。

### 1.1 首先，需要配置数据库连接。

sql在resource/sql目录下。

数据库连接配置详见application.properties。

请自行配置

启动Application后，打开浏览器访问：

```
http://localhost:8080/onlineiql
```

### 1.2 接口分类

graphql接口默认分为两类：

- 用于线上运行的graphql服务接口
- 用于前后端开发，查看生成sql情况的调试接口。

每种接口都包含两个不同的url：graphql服务接口，以及在线调试工具graphiql接口

如果本机安装了graphql playground等工具，可以直接使用graphql服务接口进行查看。

如果本地未安装任何graphql调试工具，可以打开graphiql的地址，直接在浏览器里进行调试。

#### 1.2.1 线上服务接口

- 线上调试接口：http://localhost:8080/onlineiql
- 测试调试接口：http://localhost:8080/testiql

#### 1.2.2 前后端开发调试接口

- 线上接口：http://localhost:8080/onlineql
- 测试接口：http://localhost:8080/testql

### 1.2 界面

界面如下，以graphql服务接口为例：

![示例.png](https://i.loli.net/2019/11/08/JksDrut61OZ9wXW.png)
或打开graphql playground工具，输入:
```
http://localhost:8080/graphql
```

界面如下：

![graphql_playground.png](https://i.loli.net/2019/11/08/pyNDzUm6fbjFq4B.png)
### 1.3 测试用例

#### 1.3.1 线上调试接口

实际前后端交互的接口，才此处可以进行调用

在左上方输入：

```
# 测试主子表联合查询（Many、One注解）
query relative($condition1: EntityConditionInput) {
  einvoiceHisRelative(dynamicCond: $condition1) {
    id
    fpqqlsh
    tenantid
    kplx
    einvoiceHisBVOList {
      id
      hid
      xmmc
      kce
    }
  }
}

# 测试分页查询。包含分页查询总数+当前分页查询结果
query page($condition: EntityConditionInput) {
  einvoiceCount: einvoiceHisCountAllDynamic(dynamicCond: $condition)
  einvoiceList: einvoiceHisDynamic(dynamicCond: $condition) {
    id
    fpqqlsh
    artificialName
    tenantid
    bvoList {
      id
      xmmc
      hid
      kce
    }
  }
}

# 测试分页查询。包含带有权限的分页查询
query pagePermission($condition: EntityConditionInput) {
  einvoiceCount: einvoiceHisPermissionCountDynamic(dynamicCond: $condition)
  einvoiceHisPermissionDynamic(dynamicCond: $condition) {
    id
    fpqqlsh
    tenantid
    artificialName
    bvoList {
      id
      xmmc
      hid
      kce
    }
  }
}
# 测试分页查询中，内层查询抛异常
query pageException($condition: EntityConditionInput) {
  einvoiceHisDynamic(dynamicCond: $condition) {
    artificialNameException
  }
}

# 测试外层查询抛异常
query queryException {
  testException {
    id
  }
}

# 测试无权限控制下的批量新增
mutation saveList($einvoiceList: [EinvoiceHisVOInput]) {
  saveEinvoiceHisList(einvoiceHisList: $einvoiceList)
}

# 测试有权限控制下的批量新增
mutation saveListWithPermission($einvoiceList: [EinvoiceHisVOInput]) {
  saveEinvoiceHisListWithPermission(einvoiceHisList: $einvoiceList)
}

# 测试无权限控制下的批量删除（全清空）
mutation deleteList {
  deleteEinvoiceHisList
}
# 测试有权限控制下的批量删除（不清空）
mutation deleteListWithPermission {
  deleteEinvoiceHisListWithPermission
}

```

在左下方输入：

```
{
  "condition1": {
    "joins": [
      {
        "jointype": "INNERJOIN",
        "target": "einvoice_his_b",
        "alias": "t1",
        "on": {
          "sourceAlias1": "t0",
          "field1": "id",
          "sourceAlias2": "t1",
          "field2": "hid"
        }
      }
    ],
    "limit": {
      "pageIndex": 1,
      "size": 5
    }
  },
  "condition": {
    "conditions": {
      "conditionList": [
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "id"
          },
          "operator": "LESSEQUAL",
          "v1": {
            "val": "1300"
          }
        },
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "tenantid"
          },
          "operator": "NOTEQUAL",
          "v1": {
            "val": "1234"
          }
        },
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "id"
          },
          "operator": "IN",
          "v1": {
            "source": {
              "fields": {
                "fieldList": [
                  {
                    "sourceAlias": "t1",
                    "field": "hid"
                  }
                ]
              },
              "entity": {
                "source": "einvoice_his_b",
                "alias": "t1"
              }
            }
          }
        }
      ]
    },
    "orderby": {
      "orderbyitems": [
        {
          "sourceAlias": "t0",
          "field": "id",
          "direction": "DESC"
        }
      ]
    },
    "limit": {
      "pageIndex": 1,
      "size": 2
    }
  },
  "einvoiceList": [
    {
      "fpqqlsh": "1232123212321",
      "tenantid": "123123",
      "kplx": 1,
      "einvoiceHisBVOList": [
        {
          "xmmc": "用友网络11",
          "kce": "11.11"
        },
        {
          "xmmc": "用友网络12",
          "kce": "12.12"
        }
      ]
    },
    {
      "fpqqlsh": "1232123212321",
      "tenantid": "123123",
      "kplx": 1,
      "einvoiceHisBVOList": [
        {
          "xmmc": "用友网络21",
          "kce": "21.21"
        },
        {
          "xmmc": "用友网络22",
          "kce": "22.22"
        }
      ]
    },
    {
      "fpqqlsh": "1232123212321",
      "tenantid": "123123",
      "kplx": 1,
      "einvoiceHisBVOList": [
        {
          "xmmc": "用友网络31",
          "kce": "31.31"
        },
        {
          "xmmc": "用友网络32",
          "kce": "32.33"
        }
      ]
    },
    {
      "fpqqlsh": "1232123212321",
      "tenantid": "123123",
      "kplx": 1,
      "einvoiceHisBVOList": [
        {
          "xmmc": "用友网络21",
          "kce": "41.41"
        },
        {
          "xmmc": "用友网络22",
          "kce": "42.42"
        }
      ]
    }
  ]
}
```
然后点击运行，即可查看到数据。

#### 1.3.2 测试调试接口

前后端可以根据该测试调试接口，查看请求到后端所实际翻译的sql。仅用于调试，不用于线上运行。

在左上方输入：

```
query testSqlPrint($condition: EntityConditionInput) {
  selectDynamic(selectFields: ["id"], tableName: "einvoice_his", sourceCondition: $condition) {
    sql
    paramMap
  }
  selectCountAllDynamic(tableName: "einvoice_his", sourceCondition: $condition) {
    sql
    paramMap
  }
  selectDynamicWithPermission(selectFields: ["id"], tableName: "einvoice_his", sourceCondition: $condition) {
    sql
    paramMap
  }
  selectCountAllDynamicWithPermission(tableName: "einvoice_his", sourceCondition: $condition) {
    sql
    paramMap
  }
}

```

在左下方输入：

```
{
  "condition": {
    "conditions": {
      "conditionList": [
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "id"
          },
          "operator": "LESSEQUAL",
          "v1": {
            "val": "1300"
          }
        },
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "tenantid"
          },
          "operator": "NOTEQUAL",
          "v1": {
            "val": "1234"
          }
        },
        {
          "sourceField": {
            "sourceAlias": "t0",
            "field": "id"
          },
          "operator": "IN",
          "v1": {
            "source": {
              "fields": {
                "fieldList": [
                  {
                    "sourceAlias": "t1",
                    "field": "hid"
                  }
                ]
              },
              "entity": {
                "source": "einvoice_his_b",
                "alias": "t1"
              }
            }
          }
        }
      ]
    },
    "orderby": {
      "orderbyitems": [
        {
          "sourceAlias": "t0",
          "field": "id",
          "direction": "DESC"
        }
      ]
    },
    "limit": {
      "pageIndex": 1,
      "size": 2
    }
  }
}
```
然后点击运行，即可查看到数据。

## 2. 使用框架的开发方式。

### 2.1 代码生成。根目录下的generator-app.zip，可解压缩，运行run.bat生成代码。

运行环境：windows环境+jdk1.8以上。对于linux/mac环境，理论上只需要编写run.bat同等功能的shell即可。

根据数据库表生成VO/Service，需要调整如下几个位置：

#### 2.1.1 data/prop/base/test.json

- rootPackage：对于SpringBoot项目，该路径可设置为Application类所在包。生成的所有代码，默认放在rootpackage.module下。
- module：模块名。
- rootPath：需要指定，所需生成的工程源码根目录。如：src的上级目录，必须使用绝对路径!。
- author：作者信息

#### 2.1.2 data/prop/sql.sql

将所需生成的代码的建表语句（目前支持mysql），替换进去。

#### 2.1.3 data/prop/assign/assign.json

默认情况下，生成的实体/service前缀，为数据库表名驼峰转换+首字母大写。

但是，加入需要对表明:tax_invoice_apply，生成实体：InvoiceApply，则需要修改该文件的entity属性，将中括号中的下表位置进行修改。例如，改为如下即可
```
"entity": "`l_u.UC|`split|`${sql.sql.dbTable}[1,]|split`|l_u.UC`",
```
进一步，如果需要在实体后加入DO后缀，则修改如下：
```
"entity": "`l_u.UC|`split|`${sql.sql.dbTable}[1,]|split`|l_u.UC`VO",
```

以上，修改完之后，运行run.bat，即可在工程目录下看到相应文件。

### 2.2 生成文件结构。

主要会生成四部分文件：

- entity
- permission
- repository
- service

#### 2.2.1 entity

entity包下为生成的实体类。metadata框架采用mybatis + mybatis-plus作为orm持久层，并加入大量扩展。

#### 2.2.2 permission

用于指定如需添加权限（数据权限），通过重写getPermissionConditionMap进行指定。那么针对该张表，需要添加的数据权限对应取值该如何获取。

例如：

```java
  @Override
  public Map<String, Object[][]> getPermissionConditionMap() {
    Map<String, Object[][]> map = new TreeMap<>();
    map.put("einvoice_his", new Object[][]{{"tenantid", "jowol828"}});
    return map;
  }
```

那么，运行权限查询或变更时，所有与einvoice_his相关的增删改查，都会强制添加tenantid:jowol828。注意：此处的权限不必写成常量，也可以写成从上下文中获取，或从其他service获取，或从第三方接口获取。

该方法只会在运行期执行，每次根据上下文不同动态变化。

另外，设计为new Object[][]结构的原因为：

1. 若key为常量，value为List，那么针对查询语句，我们会拼接in子句，对数据权限进行限制。对于增删改无效果。
2. 若key为常量，value为null，那么针对查询语句，我们会强行加入1<>1语句，避免sql注入攻击。对于增删改，无效果。
3. 若key为常量，value为基础类型，如：boolean/int/double/string等。则对于查询，强行加入key = value语句。对于新增，强行在每个实体中该key对应的java属性中进行赋值。对于删改查，强行在条件中拼接key = value语句

#### 2.2.3 repository

可以发现，我们生成的mybatis的repository类是空的。这和使用mybatis-plus有关。具体mybatis-plus的使用，详情百度，此处不赘述。

#### 2.2.4 service

针对同一个service接口，会在impl下生成两个不同的service：permissionService和service。

- permissionService：通过该service进行调用，那么其中的任何方法都会强制加上权限过滤！
- service：通过该service进行调用，那么不会有任何权限过滤！

### 2.3 暴露graphql服务

需要有三个步骤：

1. 编写单独的service或component，实现IGraphQLService接口。
2. 在想要暴露给外部的接口方法写进去。将不同领域实体之间的关系，通过方法+方法注解的方式描述清楚。
3. 该service一定不能使用AOP！切记！

### 2.4 @GraphQLQuery注解或@GraphQLMutation注解

使用该注解，可以将现有的service转化为GraphQL查询或变更。

但是需要注意两个问题：

#### 2.4.1 service参数加上@GraphQLArgument注解，并指定参数名称。

若不指定的话，在某些环境下，maven编译会抹掉参数名称信息。造成在不同的环境上编译运行，生成的schema不一致。

#### 2.4.2 @GraphQLQuery或@GraphQLMutation不能在AOP动态代理类中使用。

@GraphQLQuery或@GraphQLMutation注解的方法所在类，一定不能使用AOP切面。
 
例如，若有方法加入了@Transactional注解，则整个类都不能使用GraphQL注解。


不能使用AOP注解的原因在于：SPQR框架根据类上的注解信息自动生成GraphQL的schema。AOP生成的新的动态代理类，会抹掉@GraphQLQuery等注解信息，SPQR根本无法获知哪些方法应该添加到GraphQL框架上。
造成GraphQL的schema无法生效。

如需将service类方法放入GraphQL体系，需要另写一个新的service，在新的service调用代理service的方法。即可。

### 2.5 常用注解

#### 2.5.1 @GraphQLEnvironment注解

该注解，可以将当前查询中所查询的字段全部注入进来。

根据这些字段，我们可以很方便的知道需要从数据库中查询哪些字段的数据，进而提升数据库查询优化的性能。

#### 2.5.2 @GraphQLContext注解

@GraphQLContext注解，可用于指定实体之间的关联关系。例如：InvApplyVO和InvApplyBVO，这两种不同的实体间的关系，可以通过@GraphQLContext指定。

示例详见：com.yonyou.einvoice.invapply.service.InvApplyVOService.getEachInvApplyBVOList。

使用该方式配置之后，在GraphQL查询语句时，可以发现在InvApplyVO实体下面，增加了eachInvApplyBVOList字段。

若在查询语句中指定需要获取该字段，则在主对象数据查询结束之后，出发该注解注释的方法，进行调用。

注意：此示例展示的是@GraphQLContext注解单个对象的场景（@GraphQLContext注解的是单个对象，不是List！）。

例如，若某次查询，主对象数据查询1次查出10条，则子对象查询会对应调用10次，一共查11次，造成N+1性能问题。因此，建议该注解和@Batch注解配合使用。

#### 2.5.3 @Batch注解

@Batch注解和@GraphQLContext注解一起使用，会将该注解的方法，作为@GraphQLContext的上下文使用。

与单独使用@GraphQLContext不同的是，结合使用后，@GraphQLContext注解的是List，不是单对象！返回的也一定是与传入的list长度相等的list！

例如，对于某次查询，主对象数据查询1次查出10条，则子对象查询也只会对应调用1次，一共查2次，性能强悍。

使用@Batch注解和不适用@Batch注解的对比如下：

```java
  @Batched
  @GraphQLQuery
  public List<List<InvApplyBVO>> getInvApplyBVOList(
      @GraphQLArgument(name = "invapply") @GraphQLContext List<InvApplyVO> invApplyVOList,
      @GraphQLEnvironment Field field) {
  }
```

```java
  @Batched
  @GraphQLQuery
  public List<String> getArtificialName(@GraphQLArgument(name = "invapply") @GraphQLContext List<InvApplyVO> invApplyVOList) {
  }
```

```java
  @Batched
  @GraphQLQuery
public List<InvApplyBVO> getInvApplyBVOList(
      @GraphQLArgument(name = "invapply") @GraphQLContext InvApplyVO invApplyVO,
      @GraphQLEnvironment Field field) {
  }
```

例如，示例中，invApplyBVOList作为InvApplyVO的子对象。且可以实现批处理（批量根据主对象列表获取子对象列表）

另外，观察前两个@Batched实例，可以发现其返回值一个是`List<List>`，另一个是``List`。

`List<List>`表明，每个InvApplyVO对象，都有一个子`List<InvApplyBVO>`列表。该情况使用于主子查询、组合查询、聚合查询。

`List<String>`表明，每个InvApplyVO对象，都有一个String类型的字段artificialName。该情况使用于关联查询


