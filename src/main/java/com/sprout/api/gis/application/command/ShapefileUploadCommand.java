package com.sprout.api.gis.application.command;

import com.sprout.api.gis.application.command.dto.ShapefileDto;

public record ShapefileUploadCommand(
    ShapefileDto shp,
    ShapefileDto dbf,
    ShapefileDto shx,
    ShapefileDto prj
) {
}
