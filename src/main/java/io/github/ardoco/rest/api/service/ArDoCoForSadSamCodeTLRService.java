package io.github.ardoco.rest.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.kastel.mcse.ardoco.core.api.models.tracelinks.SadCodeTraceLink;
import edu.kit.kastel.mcse.ardoco.core.api.output.ArDoCoResult;
import io.github.ardoco.rest.api.api_response.TraceLinkType;
import io.github.ardoco.rest.api.converter.TraceLinkConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sadSamCodeTLRService")
public class ArDoCoForSadSamCodeTLRService extends AbstractRunnerTLRService{

    public ArDoCoForSadSamCodeTLRService() {
        super(TraceLinkType.SAD_SAM_CODE);
    }

    @Override
    protected String convertResultToJsonString(ArDoCoResult result) throws JsonProcessingException {
        List<SadCodeTraceLink> sadCodeTraceLinks = result.getSadCodeTraceLinks();
        return TraceLinkConverter.convertListOfSadCodeTraceLinksToJsonString(sadCodeTraceLinks);
    }
}
