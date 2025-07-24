package com.sprout.api.gis.application.command.dto;

public record ShapefileDto(
    byte[] data,
    String filename
) {}