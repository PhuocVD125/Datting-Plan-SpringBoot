$(document).ready(function () {
    
    let originalData = {}; // Để lưu dữ liệu ban đầu khi fetch API

    // Fetch dữ liệu user khi trang load
    function fetchUserData() {
        $.ajax({
            url: `/api/v1/user/${userId}`,
            method: "GET",
            dataType: "json",
            success: function (data) {
                originalData = data; // Lưu dữ liệu ban đầu
                renderUserData(data); // Gán dữ liệu vào giao diện
            },
            error: function (xhr) {
                console.error("Error fetching user data:", xhr);
                alert("Failed to load user information.");
            },
        });
    }

    function renderUserData(data) {
        // Gán dữ liệu vào các trường HTML
        $("img.d-block.ui-w-80").attr("src", data.avatar || "https://bootdey.com/img/Content/avatar/avatar1.png");
        $("#name").val(data.fullname || "");
        $("#username").val(data.username || "");
        $("#phoneNum").val(data.phoneNum || "");
        $("#Email").val(data.email || "");
    }

    fetchUserData(); // Gọi hàm fetch khi trang load

    // Xử lý sự kiện chọn ảnh mới
    $("#image").change(function (e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (event) {
                $("img.d-block.ui-w-80").attr("src", event.target.result); // Thay đổi ảnh tạm thời
            };
            reader.readAsDataURL(file);
        }
    });

    // Xử lý sự kiện "Reset"
    $(".btn-default.md-btn-flat").click(function () {
        renderUserData(originalData); // Gán lại dữ liệu ban đầu
        $("#image").val(""); // Xóa ảnh đã chọn (nếu có)
    });

    // Xử lý sự kiện "Save changes"
    $("#saveChanges").click(function () {
        const updatedUser = {
            fullname: $("#name").val(),
            phoneNum: $("#phoneNum").val(),
            email: $("#Email").val(),
        };

        const formData = new FormData();
        formData.append("ud", JSON.stringify(updatedUser));
        formData.append("image", $("#image")[0].files[0]); // Gửi file ảnh (nếu có)

        $.ajax({
            url: `/api/v1/user/update/${userId}`,
            method: "PUT",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                alert(response); // Thông báo thành công
                fetchUserData(); // Fetch lại dữ liệu sau khi lưu
            },
            error: function (xhr) {
                console.error("Error updating user data:", xhr);
                alert("Failed to update user information.");
            },
        });
    });
});
