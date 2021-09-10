package tr.com.nihatalim.youtubeconverter.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${topic.partition}")
    private String topicPartition;

    @Bean
    public NewTopic convertAudio() {
        return TopicBuilder.name("convert_audio")
            .partitions(Integer.parseInt(topicPartition))
            .build();
    }
}
