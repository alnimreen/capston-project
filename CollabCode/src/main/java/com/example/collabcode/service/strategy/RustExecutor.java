package com.example.collabcode.service.strategy;

import com.example.collabcode.model.Code;
import com.example.collabcode.model.ExecResult;
import com.example.collabcode.service.util.ContainerManager;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.UUID;

@Component("rust")
public class RustExecutor implements  CodeExecutionStrategy {

    @Override
    public ExecResult execCode(Code code){

            String containerName = code.getLang() + UUID.randomUUID();
            String dockerCommand = String.format("echo \"%s\" > main.rs && rustc main.rs && timeout -s SIGKILL 15  ./main ; exit", code.getCode().replace("\"", "\\\""));
            ProcessBuilder pb = new ProcessBuilder()
                    .command("docker", "run", "--rm", "--name", containerName, "--network", "none", "--memory", "300m", "cc-rust:dev", "sh", "-c", dockerCommand)
                    .redirectErrorStream(true);
            ExecResult result = new ExecResult();
            return ContainerManager.initContainer(pb, containerName, result);
    }
}
