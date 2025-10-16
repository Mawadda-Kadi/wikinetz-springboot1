package de.davaso.wikinetz.repository;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByArticle(Article article);
}
