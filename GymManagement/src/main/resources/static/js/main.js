// Main JavaScript file for Gym Management System
console.log('Gym Management System - JavaScript loaded');

// Event listener cho nút xóa user
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing delete buttons...');
    
    // Gắn sự kiện cho các nút có class delete-btn
    const deleteButtons = document.querySelectorAll('.delete-btn');
    console.log('Found', deleteButtons.length, 'delete buttons');
    
    deleteButtons.forEach(function(btn, index) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.getAttribute('data-user-id');
            const userName = this.getAttribute('data-user-name');
            if (userId && userName) {
                confirmDelete(userId, userName);
            } else {
                console.error('Missing data attributes:', {userId, userName});
            }
        });
    });
});

// Hàm confirmDelete cho user
function confirmDelete(userId, userName) {
    if (confirm('Bạn có chắc chắn muốn xóa tài khoản "' + userName + '"?')) {
        var form = document.getElementById('deleteForm');
        if (form) {
            form.action = '/admin/users/delete/' + userId;
            form.submit();
        } else {
            console.error('deleteForm not found');
            alert('Lỗi: Không tìm thấy form xóa!');
        }
    }
}





 