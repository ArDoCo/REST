package io.github.ardoco.rest.api.controller;

import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModelType;
import io.github.ardoco.rest.api.api_response.TraceLinkType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ArDoCoForSadSamCodeControllerTest extends AbstractControllerTest {

    public ArDoCoForSadSamCodeControllerTest() {
        super(TraceLinkType.SAD_SAM_CODE);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void TestRunPipelineAndGetResult_umlModel() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = setUpRequestParamToStartPipelineBBB("bigBlueButtonUML", ArchitectureModelType.UML);
        runPipeline_start_and_getResult(requestEntity);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void TestRunPipelineAndGetResult_pcmModel() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = setUpRequestParamToStartPipelineBBB("bigBlueButtonPCM", ArchitectureModelType.PCM);
        runPipeline_start_and_getResult(requestEntity);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void TestRunPipelineAndWaitForResult_pcmModel() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = setUpRequestParamToStartPipelineBBB("bigBlueButton2", ArchitectureModelType.PCM);
        test_runPipelineAndWaitForResult_helper(requestEntity);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void TestRunPipelineAndWaitForResult_umlModel() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = setUpRequestParamToStartPipelineBBB("bigBlueButton2", ArchitectureModelType.UML);
        test_runPipelineAndWaitForResult_helper(requestEntity);
    }

    @Override
    protected HttpEntity<MultiValueMap<String, Object>> prepareRequestEntityForEmptyFileTest(String projectName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("projectName", projectName);
        parameters.add("inputText", new ClassPathResource("emptyFile.txt"));
        parameters.add("inputCode", new ClassPathResource("bigBlueButton/codeModel.acm"));
        parameters.add("inputArchitectureModel",  new ClassPathResource("bigBlueButton/bbb.repository"));
        parameters.add("architectureModelType", ArchitectureModelType.PCM.toString());

        return new HttpEntity<>(parameters, headers);
    }

    // Utility method to build request entity for multipart files
    private HttpEntity<MultiValueMap<String, Object>> setUpRequestParamToStartPipelineBBB(String projectName, ArchitectureModelType modelType) {
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add("projectName", projectName);
        parameters.add("inputText", new ClassPathResource("bigBlueButton/bigbluebutton.txt"));
        parameters.add("inputCode", new ClassPathResource("bigBlueButton/codeModel.acm"));
        parameters.add("architectureModelType", modelType.toString());
        if (modelType == ArchitectureModelType.PCM) {
            parameters.add("inputArchitectureModel", new ClassPathResource("bigBlueButton/bbb.repository"));
        } else if (modelType == ArchitectureModelType.UML) {
            parameters.add("inputArchitectureModel", new ClassPathResource("bigBlueButton/bbb.uml"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(parameters, headers);
    }
}
