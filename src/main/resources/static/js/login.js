/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


document.getElementById('loginForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Ngừng hành động mặc định (submit)

    // Lấy giá trị từ các trường form
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    console.log(username);
    console.log(password);

    // Tạo đối tượng LoginDto
    const loginDto = {
        username: username,
        password: password
    };

    // Gửi request đến backend API
    fetch('/api/v1/user/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginDto) // Chuyển đổi đối tượng loginDto thành JSON
    })
    .then(response => response.json())
    .then(data => {  
            window.location.href = '/home'; // Redirect to the dashboard or main page 
                console.log(data);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred. Please try again later.');
    });
});