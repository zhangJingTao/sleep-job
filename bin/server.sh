#!/bin/sh


script_dir=`dirname $0`
APP_HOME=`cd "$script_dir/.."; pwd`
LOG_HOME=`cd "$APP_HOME/../logs/trans_server"; pwd`
echo "application home: $APP_HOME"
cd $APP_HOME

PIDFILE=$APP_HOME/server.pid
CLASSPATH=$APP_HOME/classes:$APP_HOME/classth:$APP_HOME/cfg
CLASSPATH=`echo $APP_HOME/lib/*.jar | tr ' ' ':'`:$CLASSPATH

JAVA_OPTS="-server -XX:MaxPermSize=400m"
DEPLOY_DIR="deploy"
ARGS_OPTS="-d $DEPLOY_DIR"
start() {
        echo $CLASSPATH
        rm -f $APP_HOME/$DEPLOY_DIR/shutdown.xml
        java $JAVA_OPTS -cp $CLASSPATH org.jpos.q2.Q2 $ARGS_OPTS > $LOG_HOME/console.log 2>&1 &
        echo $!>$PIDFILE
        echo "running pid: $!"
}
stop() {
        echo '<shutdown/>' > $APP_HOME/$DEPLOY_DIR/shutdown.xml
        sleep 2s
        dd=`date +%y%m%d%H%M%S`
        mv $LOG_HOME/console.log $LOG_HOME/console_$dd.log
        rm -f $PIDFILE
}

net() {
        netstat -anp | grep `cat $PIDFILE`
}

log() {
        tail -fn200 $LOG_HOME/console.log
}

lsof() {
    lsof -p `cat server.pid`
}

heap() {
    dd=`date +%m%d-%H%M`
    jmap -histo `cat $PIDFILE` > $LOG_HOME/heap/$dd.txt
    jmap -dump:format=b,file=$LOG_HOME/heap/$dd.bin `cat $PIDFILE`
}

case "$1" in
        net)
                net;;
        log)
                log;;
        start)
                start;;
        stop)
                stop;;
        lsof)
                lsof;;
        heap)
                heap;;
    restart)
        stop
        sleep 1s
        start;;
        *)
                echo "Usage: exchange {start|stop|restart|net|log|lsof|heap}"
                exit;
esac
exit 0;
