package de.davaso.wikinetz.repository;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByCreator(User creator);
    List<Article> findByTitleContainingIgnoreCase(String keyword);
}

