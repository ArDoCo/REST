package edu.kit.kastel.mcse.ardoco.tlr.rest.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.kastel.mcse.ardoco.core.api.models.tracelinks.SamCodeTraceLink;
import edu.kit.kastel.mcse.ardoco.core.api.output.ArDoCoResult;
import edu.kit.kastel.mcse.ardoco.tlr.rest.api.api_response.TraceLinkType;
import edu.kit.kastel.mcse.ardoco.tlr.rest.api.converter.TraceLinkConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("samCodeTLRService")
public class ArDoCoForSamCodeTLRService extends AbstractRunnerTLRService {

    public ArDoCoForSamCodeTLRService() {
        super(TraceLinkType.SAM_CODE);
    }

    @Override
    protected String convertResultToJsonString(ArDoCoResult result) throws JsonProcessingException {
        List<SamCodeTraceLink> traceLinks = result.getSamCodeTraceLinks();
        return TraceLinkConverter.convertListOfSamCodeTraceLinksToJsonString(traceLinks);
    }
}
