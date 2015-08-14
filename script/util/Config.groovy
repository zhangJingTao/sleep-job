package util

/**
 * 数据库配置
 */
interface Config {
    String conn   = ""
    String user   = ""
    String pass   = ""
    String driver = "com.mysql.jdbc.Driver"
    int poolSize = 20
    int threadSize = 5
}
