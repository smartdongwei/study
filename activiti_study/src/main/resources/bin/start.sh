export JAVA_BIN=$JAVA_HOME/bin
export JAVA_LIB=$JAVA_HOME/lib
export CLASSPATH=.:$JAVA_LIB/tools.jar:$JAVA_LIB/dt.jar
export PATH=$JAVA_BIN:$PATH

BASE_DIR=$(cd `dirname $0`;pwd)
# jar包名称 每次修改了版本号都要修改这个
MAIN_JAR=activiti_study-V1.0.20220228.jar
MAIN_CLASS=com.wdw.study.ActivitiStudy

xpid=$(ps -ef | grep ${BASE_DIR}/${MAIN_JAR} | grep ${MAIN_CLASS} | awk '{print $2}')
if [ "${xpid}" != "" ]; then
        echo "warn: ${PNAME} is runing"
        exit 1
fi
#==========================================================================================
# JVM Configuration
# -Xmx256m:设置JVM最大可用内存为256m,根据项目实际情况而定，建议最小和最大设置成一样。
# -Xms256m:设置JVM初始内存。此值可以设置与-Xmx相同,以避免每次垃圾回收完成后JVM重新分配内存
# -Xmn512m:设置年轻代大小为512m。整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。
#          持久代一般固定大小为64m,所以增大年轻代,将会减小年老代大小。此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8
# -XX:MetaspaceSize=64m:存储class的内存大小,该值越大触发Metaspace GC的时机就越晚
# -XX:MaxMetaspaceSize=320m:限制Metaspace增长的上限，防止因为某些情况导致Metaspace无限的使用本地内存，影响到其他程序
# -XX:-OmitStackTraceInFastThrow:解决重复异常不打印堆栈信息问题
#==========================================================================================
JAVA_OPT="-server -Xms1284m -Xmx1284m -Xmn1384m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"

nohup $JAVA_HOME/bin/java ${JAVA_OPT} -jar ${MAIN_JAR} >/dev/null 2>&1 &

echo "start 工作流管理的测试程序"

