package util

/**
 * 数据库配置
 */
interface Config {
    String conn   = "jdbc:mysql://120.25.248.250:3306/sleep?useUnicode=true&characterEncoding=utf-8"
    String user   = "dev"
    String pass   = "123qwe"
    String driver = "com.mysql.jdbc.Driver"
    int poolSize = 20
    int threadSize = 6
}
