package com.strout.api.gis.application.command.dto;

public record ShapefileDto(
    byte[] data,
    String filename
) {}