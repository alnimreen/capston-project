
package com.example.collabcode.service.strategy;

import com.example.collabcode.model.Code;
import com.example.collabcode.model.ExecResult;
import com.example.collabcode.service.util.ContainerManager;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component("java")
public class JavaExecutor implements CodeExecutionStrategy {

    @Override
    public ExecResult execCode(Code code) {
        String containerName = code.getLang() + UUID.randomUUID();
        ExecResult result = new ExecResult();

        // Create a dedicated temp directory
       // Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "docker_code");
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "docker_code");
        File tempFile = null;
        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(tempDir);

            // Write the code to a Main.java file inside the directory
             tempFile = new File(tempDir.toFile(), "Main.java");
            FileWriter writer = new FileWriter(tempFile);
            writer.write(code.getCode());
            writer.close();

            String tempDirPath = tempDir.toAbsolutePath().toString();

            // Run the Docker container with the mounted directory
            ProcessBuilder pb = new ProcessBuilder()
                    .command("docker", "run", "--rm", "--network", "none",
                            "--memory", "150m","--cpus", "0.5", "-v", tempDirPath + ":/usr/src/app",
                            "java-container:dev", "sh", "-c",
                            "cd /usr/src/app && javac Main.java && timeout -s SIGKILL 10 java Main")
                    .redirectErrorStream(true);
         //   Files.deleteIfExists(tempFile.toPath());  // Clean up after execution

            return ContainerManager.initContainer(pb, containerName, result);

        } catch (IOException e) {
            result.setOut("Error writing code to file: " + e.getMessage());
            result.setTte(0.00F);
            return result;
//        } finally {
//            // Ensure the temporary file is cleaned up after execution
//            try {
//                Files.deleteIfExists(tempFile.toPath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
       }
    }
}