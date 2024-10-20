package com.example.collabcode.controller;

import com.example.collabcode.model.File;
import com.example.collabcode.model.Room;
import com.example.collabcode.model.Version;
import com.example.collabcode.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
   private  FileRepository fileRepository;
    @Autowired
    private RoomRepository roomRepository;

    private final String baseDirectory  = "uploads"; // Directory to store files

    @PostMapping("/upload/{roomId}")
    public ResponseEntity<Map<String, String>> uploadFile(@PathVariable String roomId, @RequestParam("file") MultipartFile file, @RequestParam String username) throws IOException {
        // Create room-specific directory
        String userRole = getUserRole(roomId, username);

        if (!userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You do not have permission to upload files in this room."));
        }

        Path roomDir = Paths.get(baseDirectory, roomId);
        if (!Files.exists(roomDir)) {
            Files.createDirectories(roomDir);  // Create directory if not exists
        }

        // Save file in the room's directory
        Path filePath = roomDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Store the uploaded file details and version in MongoDB
        File newFile = new File();
        newFile.setRoomId(roomId);
        newFile.setName(file.getOriginalFilename());
        newFile.setContent(new String(file.getBytes()));  // Store file content
        newFile.setLastModified(System.currentTimeMillis());
        newFile.setOwner(username);  // Set the user as the owner

        // Save file details in the file repository
        File savedFile = fileRepository.save(newFile); // Save and get the newly saved file

        // Update the room with the new file ID
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            room.getFileIds().add(savedFile.getId()); // Add the new file ID to the room
            roomRepository.save(room); // Save the updated room
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Room not found."));
        }

        // Save initial version
        Version version = new Version();
        version.setFileId(savedFile.getId());
        version.setCode(savedFile.getContent());
        version.setTimestamp(System.currentTimeMillis());
        version.setAuthor(username);

        versionRepository.save(version);
        System.out.println("File saved at: " + filePath);

        // Return the new file ID and message in the response
        return ResponseEntity.ok(Map.of("id", savedFile.getId(), "message", "File uploaded and version saved successfully in room " + roomId));
    }

    @GetMapping("/download/{roomId}/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String roomId, @PathVariable String fileId) {
        // Find the file by ID
        File file = fileRepository.findById(fileId).orElse(null);
        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] fileContent = file.getContent().getBytes(); // Assuming content is stored as a string
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(fileContent);
    }


    // Delete file
    @DeleteMapping("/delete/{roomId}/{fileId}") // Change filename to fileId
    public ResponseEntity<String> deleteFile(@PathVariable String roomId, @PathVariable String fileId, @RequestParam String username) throws IOException {
        String userRole = getUserRole(roomId, username);
        System.out.println(userRole);
        if (!userRole.equals("ADMIN") ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete files in this room.");
        }

        // Find the file by ID
        File file = fileRepository.findById(fileId).orElse(null); // Adjust this to use the correct method from your repository
        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        }
        Path filePath = Paths.get(baseDirectory, roomId, file.getName()); // Assuming you want to delete the actual file from storage
        Files.deleteIfExists(filePath);
        // Delete associated versions
        versionRepository.deleteByFileId(file.getId());
        // Delete file from MongoDB
        fileRepository.delete(file);

        return ResponseEntity.ok("File and its versions deleted successfully: " + file.getName());
    }

    private String getUserRole(String roomId, String username) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null && room.getUserRoles() != null) {
            return room.getUserRoles().get(username);
        }
        return null; // Return null or a default value if user is not found
    }
}
