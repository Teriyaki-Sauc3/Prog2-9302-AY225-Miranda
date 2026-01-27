// Prelim Exam Score Calculator - JavaScript Version

// Get input elements
const inputs = {
    attendance: document.getElementById('attendance'),
    lab1: document.getElementById('lab1'),
    lab2: document.getElementById('lab2'),
    lab3: document.getElementById('lab3')
};

// Get output elements
const outputs = {
    labAvg: document.getElementById('labAvg'),
    classStanding: document.getElementById('classStanding'),
    passScore: document.getElementById('passScore'),
    excellentScore: document.getElementById('excellentScore'),
    remarks: document.getElementById('remarks')
};

// Add Enter key navigation
inputs.attendance.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') inputs.lab1.focus();
});

inputs.lab1.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') inputs.lab2.focus();
});

inputs.lab2.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') inputs.lab3.focus();
});

inputs.lab3.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') calculate();
});

// Calculate function
function calculate() {
    // Get values
    const attendance = parseFloat(inputs.attendance.value);
    const lab1 = parseFloat(inputs.lab1.value);
    const lab2 = parseFloat(inputs.lab2.value);
    const lab3 = parseFloat(inputs.lab3.value);
    
    // Validate inputs
    if (isNaN(attendance) || isNaN(lab1) || isNaN(lab2) || isNaN(lab3)) {
        alert('Please enter valid numbers for all fields.');
        return;
    }
    
    if (attendance < 0 || attendance > 100 || lab1 < 0 || lab1 > 100 || 
        lab2 < 0 || lab2 > 100 || lab3 < 0 || lab3 > 100) {
        alert('All values must be between 0 and 100.');
        return;
    }
    
    // Calculate Lab Work Average
    const labAvg = (lab1 + lab2 + lab3) / 3;
    
    // Calculate Class Standing
    // Class Standing = (Attendance x 0.40) + (Lab Work Average x 0.60)
    const classStanding = (attendance * 0.40) + (labAvg * 0.60);
    
    // Calculate Required Prelim Exam Scores
    // Prelim Grade = (Prelim Exam x 0.30) + (Class Standing x 0.70)
    // Required Prelim Exam = (Target - Class Standing x 0.70) / 0.30
    const requiredPass = (75 - (classStanding * 0.70)) / 0.30;
    const requiredExcellent = (100 - (classStanding * 0.70)) / 0.30;
    
    // Display results
    outputs.labAvg.textContent = labAvg.toFixed(2);
    outputs.classStanding.textContent = classStanding.toFixed(2);
    
    let remarks = '';
    
    // Pass score display
    outputs.passScore.classList.remove('success', 'danger');
    if (requiredPass <= 0) {
        outputs.passScore.textContent = '0 (Passed!)';
        outputs.passScore.classList.add('success');
        remarks += 'You will pass even with 0 on the exam!\n';
    } else if (requiredPass > 100) {
        outputs.passScore.textContent = requiredPass.toFixed(2) + ' (Impossible)';
        outputs.passScore.classList.add('danger');
        remarks += 'Passing is not possible.\n';
    } else {
        outputs.passScore.textContent = requiredPass.toFixed(2);
        remarks += `You need ${requiredPass.toFixed(2)} to pass.\n`;
    }
    
    // Excellent score display
    outputs.excellentScore.classList.remove('success', 'danger');
    if (requiredExcellent <= 0) {
        outputs.excellentScore.textContent = '0 (Guaranteed!)';
        outputs.excellentScore.classList.add('success');
        remarks += 'Excellent grade guaranteed!';
    } else if (requiredExcellent > 100) {
        outputs.excellentScore.textContent = requiredExcellent.toFixed(2) + ' (Impossible)';
        outputs.excellentScore.classList.add('danger');
        remarks += 'Excellent grade not achievable.';
    } else {
        outputs.excellentScore.textContent = requiredExcellent.toFixed(2);
        remarks += `You need ${requiredExcellent.toFixed(2)} for excellent.`;
    }
    
    outputs.remarks.textContent = remarks;
}

// Clear function
function clearFields() {
    inputs.attendance.value = '';
    inputs.lab1.value = '';
    inputs.lab2.value = '';
    inputs.lab3.value = '';
    
    outputs.labAvg.textContent = '---';
    outputs.classStanding.textContent = '---';
    outputs.passScore.textContent = '---';
    outputs.passScore.classList.remove('success', 'danger');
    outputs.excellentScore.textContent = '---';
    outputs.excellentScore.classList.remove('success', 'danger');
    outputs.remarks.textContent = 'Enter grades and press Calculate.';
    
    inputs.attendance.focus();
}
