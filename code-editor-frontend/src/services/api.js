import axios from 'axios';

const API_URL = 'http://localhost:8082/api';

const api = {
  getUser: () => axios.get(`${API_URL}/auth/user`),
  registerUser: (user) => axios.post(`${API_URL}/register`, user),
  loginUser: (user) => axios.post(`${API_URL}/login`, user),
    /*, {
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        // Ensure that credentials are included if you're using session-based authentication
        'Access-Control-Allow-Credentials': 'true'
    },
    withCredentials: true  // Include cookies for session
}),*/
   getRoomsForUser: (username) => axios.get(`${API_URL}/rooms?username=${username}`, {
        withCredentials: true  // Ensure credentials like cookies or Authorization headers are sent
    }),
      createRoom: (room) => axios.post(`${API_URL}/createRoom`, room),
  getFilesInRoom: (roomId) => axios.get(`${API_URL}/rooms/${roomId}/files`, {
    withCredentials: true, 
  }),  
  createFile: ( roomId,file, username) => 
    axios.post(`${API_URL}/rooms/${roomId}/files/create?username=${username}`, file),
  getFileContent: (roomId, fileId, username) => 
    axios.get(`${API_URL}/rooms/${roomId}/files/${fileId}`, {
        params: { username } ,// Include username as a query parameter
        withCredentials: true, // Ensure credentials are sent

      }),
  getUserRoleInRoom: (roomId, username) => 
    axios.get(`${API_URL}/rooms/${roomId}/user-role`, {
      params: { username },
      withCredentials: true, // Ensure credentials are sent
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    }),
    executeCode: (roomId, fileId, code, language, username) => 
    axios.post(`${API_URL}/rooms/${roomId}/files/${fileId}/exec`, {
      code,
      language,
      username ,   
         withCredentials: true, // Ensure credentials are sent

    }),
  revertVersion: (roomId, fileId, versionId) => 
    axios.post(`${API_URL}/rooms/${roomId}/files/${fileId}/revert/${versionId}`, {
      withCredentials: true, // Ensure credentials are sent
    }),  
  listVersions: (roomId, fileId) => 
    axios.get(`${API_URL}/rooms/${roomId}/files/${fileId}/versions`, {
      withCredentials: true, // Ensure credentials are sent
    }),  

  saveFileAndVersion: (roomId, fileId, data) => 
    axios.post(`${API_URL}/rooms/${roomId}/files/${fileId}/saveAndVersion`, data, {
      withCredentials: true, // Ensure credentials are sent
    }),  

  // Corrected methods for comments
  addComment: (roomId, fileId, comment, username) => 
    axios.post(`${API_URL}/rooms/${roomId}/files/${fileId}/comments?username=${username}`, comment, {
      withCredentials: true,  }),
  
  getComments: (roomId, fileId) => 
    axios.get(`${API_URL}/rooms/${roomId}/files/${fileId}/comments`, {
      withCredentials: true,  }),
    
  deleteComment: (roomId, fileId, commentId, username) => 
    axios.delete(`${API_URL}/rooms/${roomId}/files/${fileId}/comments/${commentId}?username=${username}`, {
      withCredentials: true,  }),
  
  uploadFile: (formData, roomId, username) => axios.post(`${API_URL}/files/upload/${roomId}`, formData, {
  headers: {
    'Content-Type': 'multipart/form-data',
  },
  params: { username } // Send username as a query parameter
}),

downloadFile: (roomId, fileId) => axios.get(`${API_URL}/files/download/${roomId}/${fileId}`, {
  responseType: 'blob' // Ensure the response is treated as a binary file
}),
deleteFile: (roomId, fileId, username) => 
  axios.delete(`${API_URL}/files/delete/${roomId}/${fileId}`, {
      params: { username } // Send username as a query parameter
  }),
 // Add the cloneRoom method here
 cloneRoom: (roomId, newRoomId, newRoomName, username) => 
  axios.post(`${API_URL}/rooms/${roomId}/clone`, null, {
    params: { newRoomId, newRoomName, username } // Include newRoomName in the params
  }),

// Add the forkRoom method here if it's also missing
forkRoom: (roomId, newRoomId, newRoomName, username) => 
  axios.post(`${API_URL}/rooms/${roomId}/fork`, null, {
    params: { newRoomId, newRoomName, username } // Include newRoomName in the params
  }),
// Add the mergeFiles method here if it's also missing
mergeFiles: (sourceRoomName, sourceFileName, targetRoomName, targetFileName, username) => 
  axios.post(`${API_URL}/rooms/merge?sourceRoomName=${sourceRoomName}&sourceFileName=${sourceFileName}&targetRoomName=${targetRoomName}&targetFileName=${targetFileName}&username=${username}`),

getAllUsers: () => axios.get(`${API_URL}/users`, {
  withCredentials: true, // Ensure credentials are sent
}),

assignRoleToUser: (roomId, userData) => 
  axios.post(`${API_URL}/rooms/${roomId}/assignRole`, userData, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` } // Include token if necessary
  }),

  getRoomParticipants: (roomId) => 
   axios.get(`${API_URL}/rooms/${roomId}/participants`, {
    withCredentials: true, // Ensure credentials are sent
  }),
  

  };

export default api;
