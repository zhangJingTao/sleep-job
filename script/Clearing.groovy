import com.mchange.v2.c3p0.ComboPooledDataSource
import util.Config

def step = ''
ComboPooledDataSource cpds = null;

try {

    cpds = new ComboPooledDataSource();
    cpds.setDriverClass(Config.driver); //loads the jdbc driver
    cpds.setJdbcUrl(Config.conn);
    cpds.setUser(Config.user);
    cpds.setPassword(Config.pass);
    cpds.setMaxPoolSize(Config.poolSize)

    Binding binding = new Binding();
    binding.setVariable("log", log)
    binding.setVariable("ds", cpds)

    TiebaJob tieba = new TiebaJob(binding)
    tieba.run()

} catch (Exception e) {
    log.warn 'Job error', e
}
//if (cpds) {
//    try {
//        cpds.close()
//    } catch (Exception e) {
//        log.warn("close ds error", e)
//    }
//}