-- Script kiểm tra database để tìm nguyên nhân trạng thái vẫn PENDING
-- Chạy từng câu lệnh một để kiểm tra

-- 1. Kiểm tra cấu trúc bảng pt_assignments
DESCRIBE pt_assignments;

-- 2. Kiểm tra dữ liệu trong pt_assignments
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at,
    updated_at,
    is_deleted,
    request_notes
FROM pt_assignments 
WHERE user_id = 5;

-- 3. Kiểm tra tất cả assignments
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at,
    updated_at,
    is_deleted
FROM pt_assignments 
ORDER BY assignment_id;

-- 4. Kiểm tra training_sessions
SELECT 
    session_id,
    user_id,
    trainer_id,
    session_date,
    start_time,
    end_time,
    status,
    notes
FROM training_sessions 
WHERE user_id = 5;

-- 5. Kiểm tra users
SELECT 
    user_id,
    name,
    email,
    role,
    is_deleted
FROM users 
WHERE user_id = 5;

-- 6. Kiểm tra trainers
SELECT 
    trainer_id,
    user_id,
    specialization,
    is_deleted
FROM trainers 
WHERE trainer_id = 71;

-- 7. Kiểm tra assignments theo trainer
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at
FROM pt_assignments 
WHERE trainer_id = 71;

-- 8. Kiểm tra assignments theo user
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at
FROM pt_assignments 
WHERE user_id = 5;

-- 9. Kiểm tra assignments có status PENDING
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at
FROM pt_assignments 
WHERE status = 'PENDING';

-- 10. Kiểm tra assignments có status ACTIVE
SELECT 
    assignment_id,
    user_id,
    trainer_id,
    status,
    created_at
FROM pt_assignments 
WHERE status = 'ACTIVE';
