import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';
import CodeEditor from './CodeEditor';
import { useUser } from './UserContext'; // Import the useUser hook
import { useNavigate } from 'react-router-dom'; // Import navigate
import styles from './styles/roomDetailsStyle.module.css'; // Adjust the path as necessary

function RoomDetails() {
  const { roomId } = useParams();
  const [files, setFiles] = useState([]); // Handle files
  const [currentPath, setCurrentPath] = useState(''); // Handle current path in the file system
  const [fileName, setName] = useState('');

  const [userRole, setUserRole] = useState(null);
  const { user } = useUser();
  const [selectedFileForUpload, setSelectedFileForUpload] = useState(null);
  const [newRoomId, setNewRoomId] = useState('');
  const [selectedUser, setSelectedUser] = useState('');
  const [selectedRole, setSelectedRole] = useState(''); // Role selection
  const [users, setUsers] = useState([]); // State for users
  const [participants, setParticipants] = useState([]); // To store participants and their roles
  const navigate = useNavigate(); // Initialize navigate
  const [newRoomName, setNewRoomName] = useState(''); // New state for newRoomName
  const [socket, setSocket] = useState(null);
  const [errorMessage, setErrorMessage] = useState(''); // State for error messages
  const [successMessage, setSuccessMessage] = useState(''); // State for success messages

  // Clear messages after 3 seconds
  const clearMessages = () => {
    setTimeout(() => {
      setErrorMessage('');
      setSuccessMessage('');
    }, 3000);
  };
  useEffect(() => {
    const fetchUserRole = async () => {
      if (!user) {
        console.warn('User is not defined yet, skipping role fetch.');
        return;
      }
      if (user && user.username) {
        console.log('Fetching role for user:', user.userId);  
        console.log('Fetching role for user:', user.username);  
        // Fetch role in the specific room
        try {
          const roomResponse = await api.getUserRoleInRoom(roomId, user.username);
         console.log("roomResponse ",roomResponse.data.role)
          if (roomResponse.status === 200) {
            setUserRole(roomResponse.data.role);
          }
        } catch (error) {
          console.error('Error fetching user role in room:', error);

          setErrorMessage('Error fetching user role in room:', error.response ? error.response.data : error.message);
          clearMessages();
        }
      } else {
        console.warn('User is not logged in or username is missing.');
      }
    };
  
    fetchUserRole();
  }, [user, roomId]);
  
  useEffect(() => {
    if (!roomId) {
      console.error("Room ID is undefined. Cannot establish WebSocket connection.");
      return;
  }
  console.log("roomId",roomId)
    const webSocket = new WebSocket(`ws://localhost:8082/websocket/${roomId}`);

    webSocket.onopen = () => {
      console.log('WebSocket connection opened');
    };

    webSocket.onmessage = (event) => {
      const message = event.data;
      setMessages((prevMessages) => [...prevMessages, message]);
    };

    webSocket.onclose = () => {
      console.log('WebSocket connection closed');
    };

    setSocket(webSocket);

    return () => {
      if (socket) {
        socket.close(); 
      }
    };
  }, [roomId, socket]);

 
  useEffect(() => {
  const fetchFiles = async () => {
    try {
      const response = await api.getFilesInRoom(roomId);
      console.log("response fetchFiles",response.data)
      if (response.status === 200) {
        setFiles(response.data); // Update the files state
      }
    } catch (error) {
      setErrorMessage(`Error fetching user role in room: ${error.response ? error.response.data : error.message}`);
      clearMessages();
    }
  };

  if (roomId) { // Only fetch files if clonedRoomId is available
    fetchFiles();
  }
}, [roomId]);

 // Fetch participants in the room
useEffect(() => {
  const fetchParticipants = async () => {
    try {
      const response = await api.getRoomParticipants(roomId); // API call to get participants
      console.log("response participents ",response.data)

      if (response.status === 200) {
        setParticipants(response.data); // Expecting array of {username, role}
      }
    } catch (error) {
      setErrorMessage('Error fetching participants:', error.response ? error.response.data : error.message);
      clearMessages();
    }
  };

    fetchParticipants();
  }, [roomId]);

// Fetch all users for role assignment
useEffect(() => {
  const fetchUsers = async () => {
    try {

      const response = await api.getAllUsers(); // Fetch all users from the API
      console.log("response get users ",response.data)
      if (response.status === 200) {
        setUsers(response.data);
      }
    } catch (error) {
      setErrorMessage('Error fetching users:', error.response ? error.response.data : error.message);
      clearMessages();
    }
  };

  fetchUsers();
}, []);


const handleAssignRole = async () => {
  if (selectedUser && selectedRole) {
    try {
      const response = await api.assignRoleToUser(roomId, { 
        username: selectedUser, 
        role: selectedRole, 
        assignerUsername: user.username // Include current user's username
    });
      if (response.status === 200) {
        setSuccessMessage('Role assigned successfully!');
        clearMessages();
        setSelectedUser('');
        setSelectedRole('');
      }
    } catch (error) {
      setErrorMessage('Error assigning role:', error.response ? error.response.data : error.message);
      clearMessages();
    }
  } else {
    setErrorMessage('Please select a user and a role.');
    clearMessages();
  }
};
const handleCloneRoom = async () => {
  if (userRole !== 'ADMIN') {
    setErrorMessage('You do not have permission to clone this room.');
    clearMessages();
    return;
  }

  if (!newRoomId || !newRoomName) {
    setErrorMessage('Please enter both a new room ID and name to clone.');
    clearMessages();
    return;
  }

  try {
    const response = await api.cloneRoom(roomId, newRoomId, newRoomName, user.username); // Pass newRoomName
    if (response.status === 200) {
      setSuccessMessage('Room cloned successfully!');
      clearMessages();
      // Log the cloned room response
      console.log('Cloned Room Response:', response.data);
      
      // Assuming the cloned room response returns the new room ID
      const clonedRoomId = response.data.id; // Adjust based on your response structure
      
      // Fetch files for the newly cloned room
      console.log("Fetching files for cloned room ID:", clonedRoomId);
      const filesResponse = await api.getFilesInRoom(clonedRoomId);
      setFiles(filesResponse.data);
    }
  } catch (error) {
    setErrorMessage('Error cloning room:', error.response ? error.response.data : error.message);
    clearMessages();
  }
};


  const handleForkRoom = async () => {
    if (userRole !== 'ADMIN' ) {
      setErrorMessage('You do not have permission to fork this room.');
      clearMessages();
      return;
    }

    if (!newRoomId || !newRoomName) {
      setErrorMessage('Please enter a new room ID to fork.');
      clearMessages();
      return;
    }

    try {
      const response = await api.forkRoom(roomId, newRoomId, newRoomName, user.username);
      if (response.status === 200) {
        setSuccessMessage('Room forked successfully!');
        clearMessages();
        const forkedRoomId = response.data.id; // Adjust based on your response structure
      
        // Fetch files for the newly cloned room
        console.log("Fetching files for cloned room ID:", forkedRoomId);
        const filesResponse = await api.getFilesInRoom(forkedRoomId);
        setFiles(filesResponse.data);
      }
    } catch (error) {
      setErrorMessage('Error forking room:', error.response ? error.response.data : error.message);
      clearMessages();
    }
  };
  const handleMergeFiles = async () => {
    if (userRole !== 'ADMIN') {
      setErrorMessage('You do not have permission to merge files.');
        clearMessages();
        return;
    }

    const sourceRoomName = prompt('Enter source room name:');
    const sourceFileName = prompt('Enter source file name:');
    const targetRoomName = prompt('Enter target room name:');
    const targetFileName = prompt('Enter target file name:');

    if (!sourceRoomName || !sourceFileName || !targetRoomName || !targetFileName) {
      setErrorMessage('Source room name, source file name, target room name, and target file name are required.');
        clearMessages();
        return;
    }

    try {
        const response = await api.mergeFiles(sourceRoomName, sourceFileName, targetRoomName, targetFileName, user.username);
        if (response.status === 200) {
            setSuccessMessage('Files merged successfully!');
            clearMessages();
          }
    } catch (error) {
      setErrorMessage('Error merging files:', error.response ? error.response.data : error.message);
      clearMessages();
    }
};


  const handleCreateFile = async () => {
    if (userRole !== 'ADMIN' ) {
      setErrorMessage('You do not have permission to create files.');
      clearMessages();
      return;
    }
    if (!user || !user.userId) {
      setErrorMessage('User is not defined or not logged in.');
      clearMessages();  
      return;
    }
    
    // Log the values of fileId and fileName
    console.log('File Name:', fileName);

    if (!fileName) {
      setErrorMessage('Please provide a file ID and a file name.');
      clearMessages();    
      return;
    }

    const newFile = {
        name: fileName,
        owner: user.userId, // Set the owner to userId directly from context
        path: currentPath,
    };

    try {
      const response = await api.createFile(roomId,newFile, user.username); // Pass the username here
      if (response.status === 200) {
        const updatedFilesResponse = await api.getFilesInRoom(roomId); 
        if (updatedFilesResponse.status === 200) {
          setFiles(updatedFilesResponse.data); // Update the local file list
      }    
          setName(''); // Reset file name
        }
    } catch (error) {
      setErrorMessage('Error creating file:', error.response ? error.response.data : error.message);
      clearMessages();  
    }
}; 

// Input for file name
<input
    type="text"
    value={fileName}
    onChange={(e) => setName(e.target.value)}
    placeholder="File name"
/>

 // Fetch file content when the component mounts or the fileId changes

 const fetchFileContent = async (fileId) => {
  console.log('Fetching content for Room ID:', roomId);
  console.log('Fetching content for File ID:', fileId);
  
  try {
    const response = await api.getFileContent(roomId, fileId, user.username);
    if (response.status === 200) {
      console.log('File response:', response.data);
      const fileContent = response.data.file.content;
      console.log('File content:', fileContent);
      
      if (fileContent) {
        setCode(fileContent);
      } else {
        console.warn('File is empty, setting default content.');
      }
    } else {
      setErrorMessage('Failed to fetch file content:', response.status);
      clearMessages();  
    }
  } catch (error) {
    setErrorMessage('Error fetching file content:', error.response ? error.response.data : error.message);
    clearMessages();  
  }
};

const handleFileClick = async (file) => {
  console.log('Selected file:', file); // Log the selected file
      // Ensure that file.id is defined before calling fetchFileContent
      if (!file || !file.id) {
        setErrorMessage('File ID is undefined, cannot fetch content.');
        clearMessages();  
        return;
    }
    await fetchFileContent(file.id); // Fetch the file content
  setName(file.name); // Set the file name
  navigate(`/rooms/${roomId}/files/${file.id}`); // Navigate after content is set
};



  const handleUploadFile = async () => {
    if (userRole !== 'ADMIN') {
      setErrorMessage('You do not have permission to upload files.');
        clearMessages();  
        return;
    }

    if (!selectedFileForUpload) {
      setErrorMessage('Please select a file to upload.');
        clearMessages();  
        return;
    }

    const formData = new FormData();
    formData.append('file', selectedFileForUpload);
    formData.append('path', currentPath); // If you are not using this, you can remove it

    try {
        const response = await api.uploadFile(formData, roomId, user.username);
        if (response.status === 200) {
            const newFileId = response.data.id; // Get the ID from the response
            console.log('Response after upload:', response.data); // Ensure this has the expected structure

            // Include the newly uploaded file in the state
            setFiles([...files, { id: newFileId, name: selectedFileForUpload.name }]);

            console.log('Fetching file content for ID:', newFileId); // Log file ID before fetching

            // Fetch the content of the newly uploaded file
            await fetchFileContent(newFileId); // Fetch the content using the new ID

            setSelectedFileForUpload(null); // Reset the file input
        }
    } catch (error) {
      setErrorMessage('Error uploading file:', error.response ? error.response.data : error.message);
      clearMessages();
    }
};


const handleDownloadFile = async (file, e) => {
  e.stopPropagation(); // Prevent the file click event
  console.log("Downloading file with ID: ", file.id);

  try {
    const response = await api.downloadFile(roomId, file.id);
    if (response.status === 200) {
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', file.name);
      document.body.appendChild(link);
      link.click();
      link.remove();
    }
  } catch (error) {
    setErrorMessage('Error downloading file:', error.response ? error.response.data : error.message);
    clearMessages();
  }
};

const handleDeleteFile = async (file, e) => {
  e.stopPropagation(); // Prevent the file click event
  if (userRole !== 'ADMIN' ) {
    setErrorMessage('You do not have permission to delete files.');
    clearMessages();
    return;
  }
  try {
    const response = await api.deleteFile(roomId, file.id, user.username); // Pass username as well
    if (response.status === 200) {
      setFiles(files.filter(f => f.id !== file.id)); // Remove the deleted file from the state
    }
  } catch (error) {
    setErrorMessage('Error deleting file:', error.response ? error.response.data : error.message);
    clearMessages();
  }
};

// In your render method, make sure to pass the event to the handlers
<ul className={styles.filesList}>
  {files.map((file) => (
    <li key={file.id} className={styles.fileItem} onClick={() => handleFileClick(file)}>
      {file.name}
      <button onClick={(e) => handleDownloadFile(file, e)}>Download</button>
      <button onClick={(e) => handleDeleteFile(file, e)}>Delete</button>
    </li>
  ))}
</ul>


  return (
    <div className={styles.container}>
      {/* Sidebar for Role Assignment */}
      <div className={styles.sidebar}>
        <h2>Assign Role to User</h2>
        <select value={selectedUser} onChange={(e) => setSelectedUser(e.target.value)}>
          <option value="">Select User</option>
          {users.map((user) => (
            <option key={user.userId} value={user.username}>
              {user.username}
            </option>
          ))}
        </select>
        <select value={selectedRole} onChange={(e) => setSelectedRole(e.target.value)}>
          <option value="">Select Role</option>
          <option value="ADMIN">Admin</option>
          <option value="EDITOR">Editor</option>
          <option value="VIEWER">Viewer</option>
        </select>
        <button onClick={handleAssignRole}>Assign Role</button>
        <div>
    <h2>Participants</h2>
    <ul>
      {participants.map((participant, index) => (
        <li key={index}>
          {participant.username} ({participant.role})
        </li>
      ))}
    </ul>
  </div>
      </div>

      {/* Main Content */}
      <div className={styles.mainContent}>
      {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
      {successMessage && <div className={styles.successMessage}>{successMessage}</div>}

        {/* Files Section */}
        <h2>Files in The Room </h2>
        <div className={styles.filesContainer}>
          <ul className={styles.filesList}>
            {files.map((file) => (
              <li key={file.id} className={styles.fileItem} onClick={() => handleFileClick(file)}>
                {file.name}
                <button onClick={(e) => handleDownloadFile(file,e)}>Download</button>
                <button onClick={(e) => handleDeleteFile(file,e)}>Delete</button>
              </li>
            ))}
          </ul>
        </div>

        {/* File Upload and Create File Options */}
        <div className={styles.fileActions}>
          <h2>Create File</h2>
      
          <input
            type="text"
            value={fileName}
            onChange={(e) => setName(e.target.value)}
            placeholder="File name"
          />
          <button onClick={handleCreateFile}>Create File</button>

          <div className={styles.uploadFile}>
            <h2>Upload File</h2>
            <input
              type="file"
              onChange={(e) => setSelectedFileForUpload(e.target.files[0])}
            />
            <button onClick={handleUploadFile}>Upload</button>
          </div>
        </div>
 
        {/* Clone, Fork, Merge Section */}
        <div className={styles.cloneForkMerge}>
          <h2>Clone, Fork and Merge</h2>
          <div className={styles.cloneForkContainer}>
            <div>
              <h3>Clone Room</h3>
              <input
                type="text"
                value={newRoomId}
                onChange={(e) => setNewRoomId(e.target.value)}
                placeholder="New Room ID"
              />
                <input
        type="text"
        value={newRoomName}  // Add this for the new room name
        onChange={(e) => setNewRoomName(e.target.value)}  // Handle room name input
        placeholder="New Room Name"
      />
              <button onClick={handleCloneRoom}>Clone</button>
            </div>
            <div>
              <h3>Fork Room</h3>
              <input
                type="text"
                value={newRoomId}
                onChange={(e) => setNewRoomId(e.target.value)}
                placeholder="New Room ID"
              />
                   <input
        type="text"
        value={newRoomName}  // Add this for the new room name
        onChange={(e) => setNewRoomName(e.target.value)}  // Handle room name input
        placeholder="New Room Name"
      />
              <button onClick={handleForkRoom}>Fork</button>
            </div>
            <div>
              <h3>Merge Files</h3>
              <button onClick={handleMergeFiles}>Merge</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RoomDetails;
