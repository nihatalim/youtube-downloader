package tr.com.nihatalim.youtubepresenter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tr.com.nihatalim.youtubepresenter.dto.DownloadCompleted;
import tr.com.nihatalim.youtubepresenter.service.PresenterService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioPublisher {
    private final PresenterService presenterService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "publish_audio")
    public void publish(ConsumerRecord<String, String> request) {
        try {
            final DownloadCompleted audio = objectMapper.readValue(request.value(), DownloadCompleted.class);
            log.info("Kafka message incomed to topic: {} with data: {}", "publish_audio", audio.getName());

            presenterService.saveAudioFile(audio);
        } catch (Exception e) {
            log.error("An exception occured while publishing audio", e);
        }
    }
}