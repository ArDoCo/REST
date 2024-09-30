package io.github.ardoco.rest.api.util;

import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModelType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArchitectureModelTypeConverter implements Converter<String, ArchitectureModelType> {

    @Override
    public ArchitectureModelType convert(String source) {
        try {
            return ArchitectureModelType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid architecture model type: " + source);
        }
    }
}
