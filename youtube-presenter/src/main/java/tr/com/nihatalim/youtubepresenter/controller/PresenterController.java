package tr.com.nihatalim.youtubepresenter.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tr.com.nihatalim.youtubepresenter.model.Audio;
import tr.com.nihatalim.youtubepresenter.service.PresenterService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PresenterController {
    private final PresenterService presenterService;

    @PostMapping("/download")
    public ResponseEntity<Void> download(@RequestBody String url) {
        presenterService.download(url);
        return ResponseEntity.of(Optional.empty());
    }

    @PostMapping("/downloads")
    public ResponseEntity<Void> downloadList(@RequestBody List<String> urls) {
        urls.forEach(presenterService::download);
        return ResponseEntity.of(Optional.empty());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Audio>> getAudios(){
        List<Audio> audios = presenterService.getAllAudios();
        return ResponseEntity.ok(audios);
    }

    @GetMapping(
        value = "/audio/{name}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getAudio(@PathVariable String name) {
        return presenterService.getAudio(name);
    }
}
