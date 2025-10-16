package de.davaso.wikinetz.dto;

import de.davaso.wikinetz.model.Role;

public record UserDto(int userId, String username, String email, Role role, boolean enabled) {}

