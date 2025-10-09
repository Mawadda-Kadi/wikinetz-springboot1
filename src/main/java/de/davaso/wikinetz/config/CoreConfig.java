package de.davaso.wikinetz.config;

import de.davaso.wikinetz.api.IdGenerator;
import de.davaso.wikinetz.api.PasswordHasher;
import de.davaso.wikinetz.manager.ArticleManager;
import de.davaso.wikinetz.manager.MediaManager;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.manager.VersionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CoreConfig {
    @Bean public IdGenerator idGenerator() { return new AtomicIdGenerator(); }
    @Bean public PasswordHasher passwordHasher() { return new Sha256Hasher(); }

    @Bean public ArticleManager articleManager(IdGenerator idGen) {
        return new ArticleManager(idGen);
    }
    @Bean public MediaManager mediaManager(IdGenerator idGen) {
        return new MediaManager(idGen);
    }
    @Bean public VersionManager versionManager() { return new VersionManager(); }

    @Bean public UserStore userStore(PasswordHasher hasher) {
        // ensures we can create authors quickly in the controller
        var store = new UserStore(hasher);
        store.ensureAdmin("admin", "admin", "admin@example.com");
        return store;
    }
}
