$(document).ready(function () {
    const apiEndpoint = '/api/v1/user'; // API endpoint để lấy danh sách user
    let currentPage = 0; // Trang hiện tại
    const pageSize = 10; // Số lượng user mỗi trang

    // Hàm lấy danh sách user từ API
    function fetchUsers(page) {
        $.ajax({
            url: `${apiEndpoint}/?page=${page}`, // Giả sử API hỗ trợ phân trang
            method: 'GET',
            dataType: 'json',
            success: function (response) {
                renderUsers(response.content);
                console.log(response.content);
                renderPagination(response.totalPages, page);
            },
            error: function (error) {
                console.error('Error fetching users:', error);
            }
        });
    }

    // Hàm hiển thị danh sách user vào bảng
    function renderUsers(users) {
        const $list = $('#recommendationList');
        $list.empty();

        if (users.length === 0) {
            $list.append('<tr><td colspan="10" class="text-center text-gray-500">No users found</td></tr>');
            return;
        }

        users.forEach(user => {
            const isActive = user.isActive ? 'Active' : 'Inactive';
            const roleClass = user.role === 'Admin' ? 'text-blue-600 font-bold' : 'text-gray-700';
            const row = `
                <tr class="border-b">
                    <td class="px-2 py-1">${user.id}</td>
                    <td class="px-2 py-1">${user.fullname}</td>
                    <td class="px-2 py-1">${user.gender}</td>
                    <td class="px-2 py-1">${user.email}</td>
                    <td class="px-2 py-1">${user.phoneNum}</td>
                    <td class="px-2 py-1 ${roleClass}">${user.role}</td>
                    <td class="px-2 py-1">
                        <img src="${user.avatar}" alt="Avatar" class="h-10 w-10 rounded-full object-cover">
                    </td>
                    <td class="px-2 py-1">${user.username}</td>
                    <td class="px-2 py-1">${isActive}</td>
                    <td class="px-2 py-1 text-center">
                        <button class="text-blue-500 hover:text-blue-700 mx-1" data-id="${user.id}" onclick="editUser(${user.id})">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="text-red-500 hover:text-red-700 mx-1" data-id="${user.id}" onclick="deleteUser(${user.id})">
                            <i class="ri-delete-bin-7-line"></i>
                        </button>
                        <button class="text-yellow-500 hover:text-yellow-700 mx-1 unactive-btn ${user.isActive ? 'btn-danger' : 'btn-success'}" data-id="${user.id}">
                            ${user.isActive ? 'Unactivate' : 'Activate'}
                        </button>
                    </td>
                </tr>`;
            $list.append(row);
        });

        bindUnactiveButtonEvents();
    }

    // Hàm hiển thị phân trang
    function renderPagination(totalPages, currentPage) {
        const $pagination = $('#pagination');
        $pagination.empty();

        for (let i = 1; i <= totalPages; i++) {
            const buttonClass = i === currentPage ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-700';
            const button = `
                <button class="px-3 py-1 rounded-lg ${buttonClass}" data-page="${i}">
                    ${i}
                </button>`;
            $pagination.append(button);
        }
    }

    // Sự kiện phân trang
    $('#pagination').on('click', 'button', function () {
        const page = $(this).data('page');
        currentPage = page;
        fetchUsers(page);
    });

    // Hàm chỉnh sửa user
    window.editUser = function (id) {
        window.location.href=`manage-user/edit/${id}`;
        // Thực hiện logic chỉnh sửa
    };

    // Hàm xóa user
    window.deleteUser = function (id) {
        if (confirm('Are you sure you want to delete this user?')) {
            $.ajax({
                url: `${apiEndpoint}/${id}`,
                method: 'DELETE',
                success: function () {
                    alert('User deleted successfully');
                    fetchUsers(currentPage);
                },
                error: function (error) {
                    console.error('Error deleting user:', error);
                }
            });
        }
    };

    // Hàm thay đổi trạng thái user (active/inactive)
    function toggleUserActiveStatus(userId) {
        const isActive = $(`.unactive-btn[data-id=${userId}]`).text().trim() === 'Unactivate';
        const action = isActive ? 'deactivate' : 'activate';

        $.ajax({
            url: `${apiEndpoint}/toggleActive/${userId}`,
            method: 'PUT',
            success: function (response) {
                alert(response);
                fetchUsers(currentPage); // Reload user list
            },
            error: function (error) {
                console.error('Error updating user status:', error);
            }
        });
    }

    // Sự kiện thay đổi trạng thái
    function bindUnactiveButtonEvents() {
        $('.unactive-btn').on('click', function () {
            var userId = $(this).data('id');
            toggleUserActiveStatus(userId);
        });
    }

    // Tải danh sách user lần đầu
    fetchUsers(currentPage);
});
