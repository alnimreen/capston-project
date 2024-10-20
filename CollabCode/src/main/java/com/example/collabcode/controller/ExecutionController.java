//
//package com.example.collabcode.controller;
//
//import com.example.collabcode.model.Code;
//import com.example.collabcode.model.ExecResult;
//import com.example.collabcode.service.Impl.CodeExecutorImpl;
//import com.example.collabcode.repository.CodeRepository;        // Import CodeRepository
//import com.example.collabcode.repository.ExecResultRepository; // Import ExecResultRepository
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@CrossOrigin
//@RestController
//@RequestMapping("/api")
//public class ExecutionController {
//
//    @Autowired
//    private CodeExecutorImpl codeExecutor;
//
//    @Autowired
//    private CodeRepository codeRepository;                 // Inject CodeRepository
//    @Autowired
//    private ExecResultRepository execResultRepository;     // Inject ExecResultRepository
//
//    @PostMapping(value = "/exec", produces = "application/json", consumes = "application/json")
//    public ExecResult executeCode(@RequestBody Code code) {
//        // Execute the code and get the result
//        ExecResult execResult = codeExecutor.codeExecutor(code);
//
//        // Save the code and the execution result to the database
//        codeRepository.save(code);                 // Save the code
//        execResultRepository.save(execResult);     // Save the execution result
//
//        return execResult;                         // Return the execution result
//    }
//}
