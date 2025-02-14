package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.MoodContentRepository;

@Service
public class MoodContentService {
    MoodContentRepository moodContentRepository;

    public MoodContentService(MoodContentRepository moodContentRepository) {
        this.moodContentRepository = moodContentRepository;
    }

    public MoodContent findMoodContent(Mood mood) {
        return moodContentRepository.findAll().stream()
                .filter(moodContent -> moodContent.getMood().equals(mood))
                .findFirst()
                .orElse(null);
    }
}
