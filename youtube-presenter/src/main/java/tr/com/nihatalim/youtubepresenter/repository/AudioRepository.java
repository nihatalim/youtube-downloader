package tr.com.nihatalim.youtubepresenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tr.com.nihatalim.youtubepresenter.model.Audio;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Integer> {
}
