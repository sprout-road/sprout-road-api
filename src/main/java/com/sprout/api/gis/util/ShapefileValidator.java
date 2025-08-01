package com.sprout.api.gis.util;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.gis.application.command.ShapefileUploadCommand;
import com.sprout.api.gis.application.command.dto.ShapefileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ShapefileValidator {

    public void validate(ShapefileUploadCommand command) {
        validateShp(command.shp());
        validateShx(command.shx());
        validatePrj(command.prj());
        validateDbf(command.dbf());
    }

    private void validateShp(ShapefileDto shp) {
        if (!StringUtils.hasText(shp.filename()) || !shp.filename().toLowerCase().endsWith(".shp")) {
            throw new BusinessException(400, "잘못된 지도 정보");
        }
    }

    private void validatePrj(ShapefileDto prj) {
        if (!StringUtils.hasText(prj.filename()) || !prj.filename().toLowerCase().endsWith(".prj")) {
            throw new BusinessException(400, "잘못된 지도 정보");
        }
    }

    private void validateShx(ShapefileDto shx) {
        if (!StringUtils.hasText(shx.filename()) || !shx.filename().toLowerCase().endsWith(".shx")) {
            throw new BusinessException(400, "잘못된 지도 정보");
        }
    }

    private void validateDbf(ShapefileDto dbf) {
        if (!StringUtils.hasText(dbf.filename()) || !dbf.filename().toLowerCase().endsWith(".dbf")) {
            throw new BusinessException(400, "잘못된 지도 정보");
        }
    }
}
