package com.kongzhong.basic.zipkin.util;

import com.kongzhong.basic.zipkin.TraceContext;
import com.twitter.zipkin.gen.Span;

/**
 * @author biezhi
 * @date 2017/11/29
 */
public class TraceUtils {

    public static Span startTrace(long traceId) {
        Span span = new Span();
        long id   = Ids.get();
        span.setId(id);
        span.setTrace_id(traceId);

        TraceContext.addSpanAndUpdate(span);
        return span;
    }

    public static void endTrace() {

    }
}