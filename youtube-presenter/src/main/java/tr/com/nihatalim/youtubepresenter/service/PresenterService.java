package tr.com.nihatalim.youtubepresenter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import tr.com.nihatalim.youtubepresenter.dto.DownloadCompleted;
import tr.com.nihatalim.youtubepresenter.model.Audio;
import tr.com.nihatalim.youtubepresenter.repository.AudioRepository;

import java.io.File;

import java.net.InetAddress;

import java.nio.charset.StandardCharsets;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenterService {
    private final AudioRepository audioRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Environment environment;

    @Value("${audio.location}")
    private String audioLocation;

    public void download(String url) {
        kafkaTemplate.send("download_audio", url);
    }

    public List<Audio> getAllAudios() {
        return audioRepository.findAll();
    }

    public void saveAudio(String fileName, String url) {
        Audio audio = new Audio();
        audio.setName(fileName);
        audio.setUrl(url);
        audioRepository.save(audio);
    }

    public void saveAudioFile(DownloadCompleted audio) {
        try {
            File file = new File(audioLocation + audio.getName());

            if (!file.exists()) {
                FileUtils.writeByteArrayToFile(file, audio.getFile());
                saveAudio(audio.getName(), getPublishedUrl(audio.getName()));
            }
        } catch (Exception e) {
            log.error("Audio file cannot be saved to location with name: {}", audio.getName());
        }
    }

    public byte[] getAudio(String name) {
        try {
            File file = new File(audioLocation + name);

            if (file.exists()) {
                return FileUtils.readFileToByteArray(file);
            }

        } catch (Exception e) {
            log.error("No audio exists with name: {}", name);
        }

        return null;
    }

    private String getPublishedUrl(String fileName) {
        final String port = environment.getProperty("server.port");
        final String hostAddress = InetAddress.getLoopbackAddress().getHostAddress();
        final String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        return String.format("http://%s:%s/api/audio/%s", hostAddress, port, encodedFileName);
    }
}
