package com.kongzhong.basic.zipkin;

import com.kongzhong.basic.zipkin.util.AppConfiguration;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Span;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * TraceContext
 */
@Slf4j
@NoArgsConstructor
public class TraceContext {

    /**
     * The trace chain global id
     */
    private static ThreadLocal<Long> TRACE_ID = new InheritableThreadLocal<>();

    /**
     * The previous trace span's id, it will be next span's parent id if it isn't null
     */
    private static ThreadLocal<Long> SPAN_ID = new InheritableThreadLocal<>();

    /**
     * The current trace's span list
     */
    private static ThreadLocal<List<Span>> SPANS = new InheritableThreadLocal<>();

    /**
     * The current trace's root span
     */
    private static ThreadLocal<Span> ROOT_SPAN = new InheritableThreadLocal<>();

    public static void setRootSpan(Span rootSpan) {
        ROOT_SPAN.set(rootSpan);
    }

    public static Span getRootSpan() {
        return ROOT_SPAN.get();
    }

    public static void setTraceId(Long traceId) {
        TRACE_ID.set(traceId);
    }

    public static Long getTraceId() {
        return TRACE_ID.get();
    }

    public static Long getSpanId() {
        return SPAN_ID.get();
    }

    public static void setSpanId(Long spanId) {
        SPAN_ID.set(spanId);
    }

    public static void addSpan(Span span) {
        List<Span> spans = getSpans();
        int spanLimitSize = AppConfiguration.getSpanLimitSize();
        if (spans != null) {
            //大于的忽略
            if (spans.size() > spanLimitSize - 1) {
                return;
            }

            //等于加描述限制
            if (spans.size() == spanLimitSize - 1) {
                span.addToBinary_annotations(BinaryAnnotation.create(
                        "MoreThanSpanLimitSize", spanLimitSize + "", null
                ));
            }

            SPANS.get().add(span);
        }
    }

    public static void addSpanAndUpdate(Span span) {
        TRACE_ID.set(span.getTrace_id());
        SPAN_ID.set(span.getId());
        getSafelySpans();
        addSpan(span);
    }

    public static List<Span> getSpans() {
        return SPANS.get();
    }

    public static void clear() {
        TRACE_ID.remove();
        SPAN_ID.remove();
        SPANS.remove();
        ROOT_SPAN.remove();
    }

    public static void start() {
        SPANS.set(new ArrayList<>());
    }

    public static void print() {
        if (log.isDebugEnabled()) {
            log.debug("Current thread: [{}], trace context: traceId={}, spanId={}", Thread.currentThread().getName(),
                    getTraceId() == null ? null : Long.toHexString(getTraceId()),
                    getSpanId() == null ? null : Long.toHexString(getSpanId()));
        }
    }

    public static void addExtInfo(String key, String value) {
        Span lastSpan = getLastSpan();
        if (lastSpan != null) {
            lastSpan.addToBinary_annotations(BinaryAnnotation.create(key, value, null));
        }
    }

    private static List<Span> getSafelySpans() {
        List<Span> spans = SPANS.get();
        if (spans == null) {
            start();
            spans = SPANS.get();
        }
        return spans;
    }

    private static Span getLastSpan() {
        List<Span> spans = SPANS.get();
        if (spans == null || spans.isEmpty()) {
            return null;
        }
        return spans.get(spans.size() - 1);
    }
}
