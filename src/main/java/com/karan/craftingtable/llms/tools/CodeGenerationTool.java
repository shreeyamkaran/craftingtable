package com.karan.craftingtable.llms.tools;

import com.karan.craftingtable.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTool {

    private final ProjectFileService projectFileService;
    private final Long projectId;

    @Tool(
            name = "read_files",
            description = "Read the contents of the files. Only input file names that are present in the FILE_TREE. Do not input any paths that are not under the FILE_TREE."
    )
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
            List<String> paths
    ) {
        List<String> result = new ArrayList<>();
        for(String path: paths) {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            log.info("Requested file: {}", cleanPath);
            String content = projectFileService.getFileContent(projectId, cleanPath).content();
            result.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE ---",
                    cleanPath, content
            ));
        }
        return result;
    }

}
