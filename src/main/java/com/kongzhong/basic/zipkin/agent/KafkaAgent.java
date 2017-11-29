package com.kongzhong.basic.zipkin.agent;

import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.kongzhong.basic.zipkin.SimpleMetricsHandler;
import com.kongzhong.basic.zipkin.collector.KafkaSpanCollector;

/**
 * KafkaAgent
 */
public class KafkaAgent extends AbstractAgent {

    public KafkaAgent(String server, String topic) {
        SpanCollectorMetricsHandler metrics = new SimpleMetricsHandler();
        KafkaSpanCollector.Config   config  = KafkaSpanCollector.Config.builder(server)
                .flushInterval(0)
                .topic(topic)
                .build();
        this.collector = new KafkaSpanCollector(config, metrics);
    }

}