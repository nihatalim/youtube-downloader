package tr.com.nihatalim.youtubepresenter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadCompleted {
    private String name;
    private String extension;
    private byte[] file;
}
