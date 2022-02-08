package com.wdw.study.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息的提取
 * @author wang
 */
public class ExceptionUtil {
    public static String getExceptionTrace(Exception e){
        StringWriter trace=new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        return trace.toString();
    }

    public static String getExceptionTrace(Throwable e){
        StringWriter trace=new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        return trace.toString();
    }
}
