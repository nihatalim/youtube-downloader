package tr.com.nihatalim.youtubedownloader.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import tr.com.nihatalim.youtubedownloader.dto.DownloadCompleted;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioDownloader {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "download_audio")
    public void download(ConsumerRecord<String, String> record) throws IOException {
        log.info("Kafka message incomed to topic: {} with data: {}", "download_audio", record.value());
        final String url = record.value();

        CompletableFuture.runAsync(() -> {
            try {
                start(url);
            } catch (Exception e) {
                log.error("Downloader has occured error:", e);
            }
        });
    }

    private void start(String url) throws IOException {
        final List<String> commands = Arrays.asList(
            "youtube-dl",
            "-f",
            "bestaudio[ext=m4a]",
            url,
            "-o",
            "%(title)s.%(ext)s");

        final Process start = new ProcessBuilder().command(commands).start();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));
        final List<String> outputs = bufferedReader.lines().collect(Collectors.toList());
        final String downloadedFileName = getDownloadedFileName(outputs);

        if (Objects.nonNull(downloadedFileName)) {
            sendDownloadedFileToConverter(downloadedFileName);
        } else {
            log.error("The url cannot be downloaded: {}", url);
        }
    }

    private String getDownloadedFileName(List<String> output) {
        String fileName = checkFileAlreadyExists(output);

        if (Objects.nonNull(fileName)) {
            return fileName;
        }

        final String expression = "Destination: ";

        return output.stream()
            .filter(item -> item.contains(expression))
            .findFirst()
            .map(item -> item.split(expression))
            .map(item -> item.length > 1 ? item[1] : null)
            .orElse(null);
    }

    private String checkFileAlreadyExists(List<String> output) {
        final String expression = " has already been downloaded";
        final String prefix = "[download] ";

        return output.stream()
            .filter(item -> item.contains(expression))
            .findFirst()
            .map(item -> item.split(expression))
            .map(item -> item.length > 1 ? item[0] : null)

            .map(item -> item.split(prefix))
            .map(item -> item.length > 1 ? item[1] : null)
            .orElse(null);
    }

    private void sendDownloadedFileToConverter(String downloadedFileName) throws IOException {
        Path normalize = Paths.get(downloadedFileName).toAbsolutePath().normalize();
        byte[] bytes = Files.readAllBytes(normalize);

        DownloadCompleted message = new DownloadCompleted(downloadedFileName, "m4a", bytes);
        kafkaTemplate.send("convert_audio", objectMapper.writeValueAsString(message));
    }
}
