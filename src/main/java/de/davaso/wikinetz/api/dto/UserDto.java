package de.davaso.wikinetz.api.dto;

import de.davaso.wikinetz.model.Role;

public record UserDto(int userId, String username, String email, Role role, boolean enabled) {}

