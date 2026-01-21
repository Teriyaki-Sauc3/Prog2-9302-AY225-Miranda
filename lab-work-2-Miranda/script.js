// Predefined valid credentials (hardcoded for this assignment)
const validCredentials = {
    username: "admin",
    password: "password123"
};

// Attendance records array to store login history
let attendanceRecords = [];

// Get DOM elements
const loginForm = document.getElementById('loginForm');
const messageDiv = document.getElementById('message');
const timestampSection = document.getElementById('timestampSection');
const timestampSpan = document.getElementById('timestamp');
const logoutBtn = document.getElementById('logoutBtn');
const timestampRecordsContainer = document.getElementById('timestampRecordsContainer');
const timestampRecordsList = document.getElementById('timestampRecordsList');
const downloadTimestampsBtn = document.getElementById('downloadTimestampsBtn');
const beepSound = document.getElementById('beepSound');

// Current logged in user info
let currentUser = null;

// Toggle password visibility
const togglePassword = document.getElementById('togglePassword');
const passwordInput = document.getElementById('password');
const eyeIcon = document.getElementById('eyeIcon');
const eyeOffIcon = document.getElementById('eyeOffIcon');

togglePassword.addEventListener('click', function() {
    // Toggle the type attribute
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    
    // Toggle the eye icons
    if (type === 'password') {
        eyeIcon.style.display = 'block';
        eyeOffIcon.style.display = 'none';
    } else {
        eyeIcon.style.display = 'none';
        eyeOffIcon.style.display = 'block';
    }
});

// Handle form submission
loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    // Validate credentials
    if (validateLogin(username, password)) {
        handleSuccessfulLogin(username);
    } else {
        handleFailedLogin();
    }
});

// Validate login credentials
function validateLogin(username, password) {
    return username === validCredentials.username && password === validCredentials.password;
}

// Handle successful login
function handleSuccessfulLogin(username) {
    // Get current timestamp
    const now = new Date();
    const formattedTimestamp = formatDateTime(now);
    
    // Store current user info
    currentUser = {
        username: username,
        timestamp: formattedTimestamp,
        dateObject: now
    };
    
    // Add to attendance records
    attendanceRecords.push({
        username: username,
        timestamp: formattedTimestamp
    });
    
    // Update UI
    messageDiv.textContent = `Welcome, ${username}!`;
    messageDiv.className = 'message success';
    
    // Display timestamp
    timestampSpan.textContent = formattedTimestamp;
    timestampSection.style.display = 'block';
    
    // Hide login form
    loginForm.style.display = 'none';
    
    // Update timestamp records display
    updateTimestampRecordsDisplay();
}

// Handle failed login
function handleFailedLogin() {
    // Display error message
    messageDiv.textContent = 'Invalid username or password. Please try again.';
    messageDiv.className = 'message error';
    
    // Play beep sound
    playBeepSound();
    
    // Clear password field
    document.getElementById('password').value = '';
}

// Play beep sound for failed login
function playBeepSound() {
    // Reset the audio to start
    beepSound.currentTime = 0;
    
    // Play the sound
    beepSound.play().catch(function(error) {
        console.log('Audio playback failed:', error);
        // Fallback: Use Web Audio API to generate a beep
        generateBeep();
    });
}

// Fallback beep generator using Web Audio API
function generateBeep() {
    try {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);
        
        oscillator.frequency.value = 800; // Frequency in Hz
        oscillator.type = 'square';
        
        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
        
        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.3);
    } catch (e) {
        console.log('Web Audio API not supported');
    }
}

// Format date and time as MM/DD/YYYY HH:MM:SS
function formatDateTime(date) {
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${month}/${day}/${year} ${hours}:${minutes}:${seconds}`;
}

// Update timestamp records display (in container below main)
function updateTimestampRecordsDisplay() {
    timestampRecordsContainer.style.display = 'block';
    timestampRecordsList.innerHTML = '';
    
    if (attendanceRecords.length === 0) {
        timestampRecordsList.innerHTML = '<div class="no-records">No timestamp records yet.</div>';
        return;
    }
    
    attendanceRecords.forEach(function(record, index) {
        const recordDiv = document.createElement('div');
        recordDiv.className = 'timestamp-record-item';
        recordDiv.innerHTML = `
            <span class="record-number">#${index + 1}</span> 
            <span class="record-user">${record.username}</span><br>
            <span class="record-time">🕐 ${record.timestamp}</span>
        `;
        timestampRecordsList.appendChild(recordDiv);
    });
}

// Download timestamps
downloadTimestampsBtn.addEventListener('click', function() {
    downloadTimestamps();
});

// Download timestamps as a file
function downloadTimestamps() {
    if (attendanceRecords.length === 0) {
        alert('No timestamp records to download.');
        return;
    }
    
    let content = "=================================\n";
    content += "      TIMESTAMP RECORDS REPORT    \n";
    content += "=================================\n\n";
    content += "Generated: " + formatDateTime(new Date()) + "\n\n";
    content += "---------------------------------\n";
    content += "TIMESTAMP RECORDS:\n";
    content += "---------------------------------\n\n";
    
    attendanceRecords.forEach(function(record, index) {
        content += `Record #${index + 1}\n`;
        content += `Username: ${record.username}\n`;
        content += `Timestamp: ${record.timestamp}\n`;
        content += "---------------------------------\n";
    });
    
    content += "\nTotal Records: " + attendanceRecords.length + "\n";
    content += "\n=================================\n";
    content += "        END OF REPORT            \n";
    content += "=================================\n";
    
    // Create blob and download
    const blob = new Blob([content], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = 'timestamp_records.txt';
    link.click();
    
    // Clean up
    window.URL.revokeObjectURL(link.href);
}

// Logout functionality
logoutBtn.addEventListener('click', function() {
    // Reset UI
    loginForm.style.display = 'block';
    timestampSection.style.display = 'none';
    messageDiv.textContent = '';
    messageDiv.className = 'message';
    
    // Clear form fields
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    
    // Clear current user
    currentUser = null;
});
