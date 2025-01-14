document.getElementById('sigupForm').addEventListener('submit', async function (e) {
    e.preventDefault(); // Ngăn chặn hành vi submit mặc định

    // Lấy giá trị từ các trường
    const fullname = document.getElementById('fullname').value.trim();
    const email = document.getElementById('email').value.trim();
    
    const phoneNum = document.getElementById('phoneNum').value.trim();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const rePassword = document.getElementById('Re-password').value.trim();
    const gender = document.getElementById('gender').value;
    const avatarFile = document.getElementById('fileAttachment').files[0];

    // Kiểm tra các trường không được để trống
    if (!fullname || !email || !phoneNum || !username || !password || !rePassword || !gender) {
        alert('Please fill in all required fields.');
        return;
    }

    // Kiểm tra mật khẩu trùng khớp
    if (password !== rePassword) {
        alert('Passwords do not match.');
        return;
    }

    // Tạo JSON object cho rd
    const rd = JSON.stringify({
        fullname: fullname,
        email: email,
        phoneNum: phoneNum,
        username: username,
        password: password,
        gender: gender,
    });
    // Tạo FormData để chứa dữ liệu
    const formData = new FormData();
    formData.append('rd', rd);

    if (avatarFile) {
        formData.append('image', avatarFile); // Đưa file vào FormData
    }

    try {
        // Gửi dữ liệu tới API
        const response = await fetch('/api/v1/user/add', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('Registration successful!');
            console.log(response);
            // Chuyển hướng sau khi đăng ký thành công
            window.location.href = '/login';
        }
        else{
            alert('Username already exist');
            console.log(response);
        }
        
    } catch (err) {
        console.error(err);
        alert('Something went wrong. Please try again.');
    }
});
