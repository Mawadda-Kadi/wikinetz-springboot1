package de.davaso.wikinetz.repository;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long> {
    List<Version> findByArticleOrderByVersionNumberAsc(Article article);
    Optional<Version> findTopByArticleOrderByVersionNumberDesc(Article article);
}
