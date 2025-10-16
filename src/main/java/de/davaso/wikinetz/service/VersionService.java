package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.model.Version;
import de.davaso.wikinetz.repository.VersionRepository;
import de.davaso.wikinetz.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VersionService {

    private final VersionRepository versionRepository;
    private final ArticleRepository articleRepository;

    public VersionService(VersionRepository versionRepository, ArticleRepository articleRepository) {
        this.versionRepository = versionRepository;
        this.articleRepository = articleRepository;
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

    public boolean restoreVersion(Article article, int versionNumber) {
        return versionRepository.findByArticleOrderByVersionNumberAsc(article)
                .stream()
                .filter(v -> v.getVersionNumber() == versionNumber)
                .findFirst()
                .map(v -> {
                    // Apply version content to the current article
                    article.setTitle(v.getTitle());
                    article.setContent(v.getContent());
                    article.setCategory(v.getCategory());

                    // Save the updated article
                    articleRepository.save(article);

                    // Create a new snapshot documenting the restore
                    snapshot(article, null, "Restored from v" + versionNumber);

                    return true;
                })
                .orElse(false);
    }
}



