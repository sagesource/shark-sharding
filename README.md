# shark-shrding
Shark Sharding 是基于 Spring MyBatis 的分库分表 读写分离插件

[
    {
        "groupName": "数据源组名称，分库时为分库shardkey",
        "loadBalance": "读写分离 LB 策略",
        "atoms": [
            {
                "atomName":"子数据源的名称，可以重复，主要用在针对不同数据库的连接池配置",
                "host": "数据库IP",
                "port": "数据库PORT",
                "dbName": "数据库名称",
                "username": "数据库用户名",
                "password": "数据库密码",
                "params": "数据库连接参数",
                "isMaster": true
            },
            {
                "atomName":"子数据源的名称，可以重复，主要用在针对不同数据库的连接池配置",
                "host": "数据库IP",
                "port": "数据库PORT",
                "dbName": "数据库名称",
                "username": "",
                "password": "",
                "params": "",
                "isMaster": false
            }
        ]
    }
]

相关概念：
matrixDataSource：复合数据源，逻辑概念，包括所有实际的数据源信息。
matrixName：复合数据源名称
groupName：数据源组的名称，里面包含了一个数据库的数据源信息。只有一个的时候，默认为 rwds，对应单库的读写分离。如果有多个，代表分库场景，分库时为分库shardkey。
atom：实际的数据源信息。创建数据源对象是，Bean Name：matrixName+"_" + groupName + MASTER/SLAVE(index)