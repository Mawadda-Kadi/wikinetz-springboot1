package de.davaso.wikinetz;

import de.davaso.wikinetz.api.*;
import de.davaso.wikinetz.manager.*;
import de.davaso.wikinetz.service.*;
import de.davaso.wikinetz.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {

        // Initialize abstractions
        PasswordHasher hasher = new BCryptPasswordHasher(10);
        IdGenerator articleIdGen = new SequentialIdGenerator();
        IdGenerator mediaIdGen = new SequentialIdGenerator();
        ArticleService articleService = new ArticleManager(articleIdGen);
        MediaService mediaService = new MediaManager(mediaIdGen);
        VersionService versionService = new VersionManager();
        UserRepository userRepo = new UserStore(hasher);
        AuthService auth = new AuthServiceImp(userRepo, hasher);


        // Ensure bootstrap admin (through repo)
        userRepo.register("admin", "admin123", "admin@example.com", de.davaso.wikinetz.model.Role.ADMIN);

        // UI: separate class that depends on interfaces only
        ConsoleUI ui = new ConsoleUI(auth, articleService, mediaService, versionService, userRepo);
        ui.start();
    }
}