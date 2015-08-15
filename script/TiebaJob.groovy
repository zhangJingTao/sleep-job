import groovy.sql.Sql
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import util.Config
import util.JobExcutor
import util.Utils

println "*" * 100
println "开始抓取Tieba"
println "*" * 100

def sql = "select start,end,id from tieba_card_schedule schedule where schedule.exe_date is null order by id asc limit 6"

jobExcutor = new JobExcutor(Config.threadSize, 0)

new Sql(ds).eachRow(sql) { schedule ->
    def row = Utils.CloneResultSet(schedule)
    jobExcutor.run {
        def db = new Sql(ds)
        def start = row.start as Long
        def end = row.end as Long
        def id = row.id
        println("executing...start:${start},end:${end}")
        db.executeUpdate("update tieba_card_schedule set exe_date=NOW() where id="+id)
        def baseUrl = "http://tieba.baidu.com/p/"
        for (Long i = start; i < end; i++) {
            println("cur tieba_url:" + baseUrl + "${i}")
            def url = baseUrl + "${i}"
            def title = ""
            def author = ""
            def count = ""
            def source = ""
            def createDate = ""
            try {
                Document document = Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0").get()
                title = document.select(".core_title_txt")?.get(0)?.text()
                log.info("title:${title}")
                author = document.select(".p_author_name ")?.get(0)?.text()
                println("author:${author}")
                count = document.select(".l_reply_num")?.get(0)?.select("span")?.get(0)?.text()
                println("count:${count}")
                source += document.select(".tail-info")?.get(0)?.text() + "&&"
                source += document.select(".tail-info")?.get(1)?.text() + "&&"
                source += document.select(".tail-info")?.get(2)?.text() + "&&"
                if (document.select(".tail-info").size() > 3) {
                    source += document.select(".tail-info")?.get(3)?.text() + "&&"
                }
                source.split("&&").each { ele ->
                    if (ele.length() == 16) {
                        createDate = ele
                    }
                }
                println("source:${source}")
                def card = [
                        cur_num     : i,
                        title       : title,
                        author      : author,
                        url         : url,
                        status      : 200,
                        reply_count : count==""? 0:Integer.valueOf(count),
                        create_date : createDate,
                        date_created: new Date(),
                        source      : source,
                        version     : 0
                ]
                db.dataSet("tieba_card").add(card)
            } catch (Exception e) {
                println("${url}帖子可能不存在...")
                def card = [
                        cur_num     : i,
                        title       : title,
                        author      : author,
                        url         : url,
                        status      : 404,
                        reply_count : count==""? 0:Integer.valueOf(count),
                        create_date : createDate,
                        date_created: new Date(),
                        source      : source,
                        version     : 0
                ]
                try {
                    new Sql(ds).dataSet("tieba_card").add(card)
                }catch (Exception ex){

                }
                continue
            }
        }
    }
}
jobExcutor.await()
