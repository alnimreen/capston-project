//package com.example.collabcode.service.strategy;
//
//import com.example.collabcode.model.Code;
//import com.example.collabcode.model.ExecResult;
//import com.example.collabcode.service.util.ContainerManager;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component("python")
//public class PythonExecutor implements CodeExecutionStrategy {
//    @Override
//    public ExecResult execCode(Code code) {
//        String containerName = code.getLang() + UUID.randomUUID();
//        // Properly escaping the parentheses for the shell
//        String formattedCode = code.getCode().replace("'", "\\'").replace("\"", "\\\"").replace("(", "\\(").replace(")", "\\)");
//
//        // Use python3 and modify the command to be more shell-friendly
//        String dockerCommand = String.format("echo \"%s\" > a.py && timeout -s SIGKILL 10 python3 a.py ; exit", formattedCode);
//
//        ProcessBuilder pb = new ProcessBuilder()
//                .command("docker", "run", "--rm", "--name", containerName, "--network", "none",
//                        "--memory", "150m", "python-container:dev", "sh", "-c", dockerCommand)
//                .redirectErrorStream(true);
//
//        ExecResult result = new ExecResult();
//        return ContainerManager.initContainer(pb, containerName, result); // Pass the filename
//    }}
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

@Component("python")
public class PythonExecutor implements CodeExecutionStrategy {

    @Override
    public ExecResult execCode(Code code) {
        String containerName = code.getLang() + UUID.randomUUID();
        ExecResult result = new ExecResult();

        // Create a dedicated temp directory
      //  Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "docker_code");
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "docker_code");
        File tempFile = null;
        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(tempDir);

            // Write the code to a a.py file inside the directory
             tempFile = new File(tempDir.toFile(), "a.py");
            FileWriter writer = new FileWriter(tempFile);
            writer.write(code.getCode());
            writer.close();

            String tempDirPath = tempDir.toAbsolutePath().toString();

            // Run the Docker container with the mounted directory
            ProcessBuilder pb = new ProcessBuilder()
                    .command("docker", "run", "--rm", "--network", "none",
                            "--memory", "150m","--cpus", "0.5", "-v", tempDirPath + ":/usr/src/app",
                            "python-container:dev", "sh", "-c",
                            "cd /usr/src/app && timeout -s SIGKILL 10 python3 a.py")
                    .redirectErrorStream(true);

            return ContainerManager.initContainer(pb, containerName, result);

        } catch (IOException e) {
            result.setOut("Error writing code to file: " + e.getMessage());
            result.setTte(0.00F);
            return result;
        } finally {
            // Ensure the temporary file is cleaned up after execution
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}