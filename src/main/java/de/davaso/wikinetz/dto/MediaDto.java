package de.davaso.wikinetz.dto;

import de.davaso.wikinetz.model.MediaType;

public record MediaDto(int mediaId, int articleId, String filename, String filepath, MediaType type) {}
