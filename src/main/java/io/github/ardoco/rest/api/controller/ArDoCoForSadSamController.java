package io.github.ardoco.rest.api.controller;

import com.github.jsonldjava.shaded.com.google.common.io.Files;
import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModelType;
import edu.kit.kastel.mcse.ardoco.tlr.execution.ArDoCoForSadSamTraceabilityLinkRecovery;
import io.github.ardoco.rest.api.api_response.ArdocoResultResponse;
import io.github.ardoco.rest.api.api_response.TraceLinkType;
import io.github.ardoco.rest.api.exception.*;
import io.github.ardoco.rest.api.service.AbstractRunnerTLRService;
import io.github.ardoco.rest.api.util.FileConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Tag(name = "Sad-Sam TraceLinkRecovery")
@RestController
public class ArDoCoForSadSamController extends AbstractController {

    private static final Logger logger = LogManager.getLogger(ArDoCoForSadSamController.class);


    public ArDoCoForSadSamController(@Qualifier("sadSamTLRService") AbstractRunnerTLRService service) {
        super(service, TraceLinkType.SAD_SAM);
    }

    @Operation(
            summary = "Starts the sad-sam processing pipeline",
            description = "Starts the sad-sam processing pipeline with the given project name, the type of the architecture model and files."
    )
    @PostMapping(value = "/api/sad-sam/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArdocoResultResponse> runPipeline(
            @Parameter(description = "The name of the project", required = true) @RequestParam("projectName") String projectName,
            @Parameter(description = "The textual documentation of the project", required = true) @RequestParam("inputText") MultipartFile inputText,
            @Parameter(description = "The architectureModel of the project", required = true) @RequestParam("inputArchitectureModel") MultipartFile inputArchitectureModel,
            @Parameter(description = "The type of architectureModel that is uploaded.", required = true)@RequestParam("architectureModelType") ArchitectureModelType modelType)
            throws FileNotFoundException, FileConversionException, HashingException {

        Map<String, File> inputFileMap = convertInputFilesHelper(inputText, inputArchitectureModel);
        List <File> inputFiles = new ArrayList<>(inputFileMap.values());

        String id = generateRequestId(inputFiles, projectName);
        ArDoCoForSadSamTraceabilityLinkRecovery runner = setUpRunner(inputFileMap, modelType, projectName);

        return handleRunPipeLineResult(runner, id, inputFiles);
    }

    @Operation(
            summary = "Starts the ardoco-pipeline to get a SadSamTraceLinks and waits until the result is obtained",
            description = "performs the SadSamTraceLinks link recovery of ArDoCo with the given project name and files and waits until the SadSamTraceLinks are obtained."
    )
    @PostMapping(value = "/api/sad-sam/start-and-wait", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArdocoResultResponse> runPipelineAndWaitForResult(
            @Parameter(description = "The name of the project", required = true) @RequestParam("projectName") String projectName,
            @Parameter(description = "The textual documentation of the project", required = true) @RequestParam("inputText") MultipartFile inputText,
            @Parameter(description = "The architectureModel of the project", required = true) @RequestParam("inputArchitectureModel") MultipartFile inputArchitectureModel,
            @Parameter(description = "The type of architectureModel that is uploaded.", required = true)@RequestParam("architectureModelType") ArchitectureModelType modelType)

            throws FileNotFoundException, FileConversionException, HashingException {

        Map<String, File> inputFileMap = convertInputFilesHelper(inputText, inputArchitectureModel);
        List <File> inputFiles = new ArrayList<>(inputFileMap.values());

        String id = generateRequestId(inputFiles, projectName);
        ArDoCoForSadSamTraceabilityLinkRecovery runner = setUpRunner(inputFileMap, modelType, projectName);

        return handleRunPipelineAndWaitForResult(runner, id, inputFiles);
    }


    @Operation(
            summary = "Queries the TraceLinks for a given resultID, and returns it if it is ready",
            description = "Queries whether the TraceLinks are ready using the id, which was returned by tue runPipeline method. " +
                    "In case the result is not yet ready, the user gets informed about it via an appropriate message"
    )
    @GetMapping("/api/sad-sam/{id}")
    public ResponseEntity<ArdocoResultResponse> getResult(
            @Parameter(description = "The ID of the result to query", required = true)  @PathVariable("id") String id)
            throws ArdocoException, IllegalArgumentException {
        return handleGetResult(id);
    }


    @Operation(
            summary = "Waits up to 60s for the TraceLinks and returns them when they are ready.",
            description = "Queries the TraceLinks and returns them when the previously started pipeline (using the runPipeline Method) has finished." +
                    "In case the result is not there within 60s of waiting, the user gets informed about it via an appropriate message"
    )
    @GetMapping("/api/sad-sam/wait/{id}")
    public ResponseEntity<ArdocoResultResponse> waitForResult(
            @Parameter(description = "The ID of the result to query", required = true) @PathVariable("id") String id)
            throws ArdocoException, InterruptedException, IllegalArgumentException, TimeoutException {
        return handleWaitForResult(id);
    }

    private Map<String, File> convertInputFilesHelper(MultipartFile inputText, MultipartFile inputArchitectureModel) {
        logger.log(Level.DEBUG, "Convert multipartFiles to files");
        Map<String, File> inputFiles = new HashMap<>();

        inputFiles.put("inputText", FileConverter.convertMultipartFileToFile(inputText));
        inputFiles.put("inputArchitectureModel", FileConverter.convertMultipartFileToFile(inputArchitectureModel));

        return inputFiles;
    }

    private ArDoCoForSadSamTraceabilityLinkRecovery setUpRunner(Map<String, File> inputFileMap, ArchitectureModelType modelType, String projectName) {
        SortedMap<String, String> additionalConfigs = new TreeMap<>(); // can be later added to api call as param if needed

        logger.log(Level.INFO, "Setting up Runner...");
        ArDoCoForSadSamTraceabilityLinkRecovery runner = new ArDoCoForSadSamTraceabilityLinkRecovery(projectName);
        runner.setUp(
                inputFileMap.get("inputText"),
                inputFileMap.get("inputArchitectureModel"),
                modelType,
                additionalConfigs,
                Files.createTempDir()
        );
        return runner;
    }
}
