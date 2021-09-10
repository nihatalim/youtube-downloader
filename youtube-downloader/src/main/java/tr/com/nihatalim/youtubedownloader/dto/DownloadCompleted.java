package tr.com.nihatalim.youtubedownloader.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DownloadCompleted {
    private String name;
    private String extension;
    private byte[] file;
}
