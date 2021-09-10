package tr.com.nihatalim.youtubeconverter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import tr.com.nihatalim.youtubeconverter.dto.DownloadCompleted;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioConverter {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "convert_audio")
    public void convert(ConsumerRecord<String, String> request) {
        CompletableFuture.runAsync(() -> {
            try {
                final DownloadCompleted value = objectMapper.readValue(request.value(), DownloadCompleted.class);
                start(value);
            } catch (Exception e) {
                log.error("Audio converting couldn't successful.", e);
                e.printStackTrace();
            }
        });
    }

    private void start(DownloadCompleted value) throws IOException {
        saveFile(value);
        convertFile(value);
    }

    private void convertFile(DownloadCompleted request) throws IOException {
        String fileName = getFileNameAsMp3(request.getName());

        final List<String> commands = Arrays.asList(
            "ffmpeg",
            "-i",
            request.getName(),
            "-acodec",
            "libmp3lame",
            "-ab",
            "256k",
            fileName
        );

        Process start = new ProcessBuilder().command(commands).start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));

        final List<String> outputs = bufferedReader.lines().collect(Collectors.toList());

        sendReadyToDownload(fileName);
    }

    private void sendReadyToDownload(String fileName) throws IOException {
        Path normalize = Paths.get(fileName).toAbsolutePath().normalize();
        byte[] bytes = Files.readAllBytes(normalize);

        DownloadCompleted message = new DownloadCompleted(fileName, "mp3", bytes);

        kafkaTemplate.send("publish_audio", objectMapper.writeValueAsString(message));
    }

    private String getFileNameAsMp3(String name) {
        return name.substring(0, name.length() - 3).concat("mp3");
    }

    private void saveFile(DownloadCompleted request) throws IOException {
        FileUtils.writeByteArrayToFile(new File(request.getName()), request.getFile());
    }
}
