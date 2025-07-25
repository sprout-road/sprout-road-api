package com.strout.api.gis.application;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;

public interface ShapefileParsingStrategy {

    void parse(ShapefileUploadCommand command);
    boolean supports(ShapefileType type);
}
