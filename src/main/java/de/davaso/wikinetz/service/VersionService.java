package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.model.Version;
import de.davaso.wikinetz.repository.VersionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public Version createInitialVersion(Article article, User creator) {
        Version v = new Version();
        v.setArticle(article);
        v.setVersionNumber(1);
        v.setCreatedAt(LocalDateTime.now());
        v.setTitle(article.getTitle());
        v.setContent(article.getContent());
        v.setCategory(article.getCategory());
        v.setEditorUsername(creator.getUsername());
        return versionRepository.save(v);
    }

    public Version snapshot(Article article, User editor, String note) {
        Optional<Version> latest = versionRepository.findTopByArticleOrderByVersionNumberDesc(article);
        int nextVersion = latest.map(v -> v.getVersionNumber() + 1).orElse(1);
        Version v = new Version();
        v.setArticle(article);
        v.setVersionNumber(nextVersion);
        v.setCreatedAt(LocalDateTime.now());
        v.setTitle(article.getTitle());
        v.setContent(article.getContent());
        v.setCategory(article.getCategory());
        v.setEditorUsername(editor != null ? editor.getUsername() : "SYSTEM");
        v.setNote(note);
        return versionRepository.save(v);
    }

    public List<Version> listVersions(Article article) {
        return versionRepository.findByArticleOrderByVersionNumberAsc(article);
    }
}

