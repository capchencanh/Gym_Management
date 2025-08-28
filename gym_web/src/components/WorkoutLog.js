import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Table, Badge, Alert, Spinner } from 'react-bootstrap';
import Apis from '../configs/Apis';
import { useUser } from '../configs/UserProvider';
import { useNavigate } from 'react-router-dom';

const WorkoutLog = () => {
    const user = useUser();
    const navigate = useNavigate();
    
    useEffect(() => {
        if (!user) {
            navigate('/login');
        }
    }, [user, navigate]);

    const [workoutData, setWorkoutData] = useState({
        session_name: '',
        date: new Date().toISOString().split('T')[0],
        workoutType: 'strength', 
        notes: ''
    });

    const [strengthExercises, setStrengthExercises] = useState([]);
    const [cardioExercises, setCardioExercises] = useState([]);
    const [workoutHistory, setWorkoutHistory] = useState([]);
    
    
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');




    useEffect(() => {
        if (user) {
            loadWorkoutHistory();
        }
    }, [user]);

    const loadWorkoutHistory = async () => {
        try {
            setLoading(true);
            setError('');
            
            const response = await Apis.get(`api/workout-logs/user/${user.user_id || user.id}`);
            
            if (response.data) {
                const formattedHistory = formatWorkoutHistoryFromDB(response.data);
                setWorkoutHistory(formattedHistory);
            }
        } catch (err) {
            setError('Không thể tải lịch sử bài tập. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

   // Trong file WorkoutLog.js
// DÁN VÀO ĐÚNG VỊ TRÍ CỦA HÀM CŨ
const formatWorkoutHistoryFromDB = (dbData) => {
    const sessions = {};
    
    dbData.forEach(log => {
        const sessionKey = `${log.session_date}_${log.session_name || 'Buổi tập'}`;
        
        if (!sessions[sessionKey]) {
            sessions[sessionKey] = {
                id: sessionKey,
                date: log.session_date,
                name: log.session_name || 'Buổi tập chung',
                exercises: [],
                notes: log.notes
            };
        }
        
        let exercise = sessions[sessionKey].exercises.find(ex => ex.name === log.exercise_name);
        
        if (!exercise) {
            exercise = {
                name: log.exercise_name,
                type: log.workout_type,
                sets: [],
                duration: log.workout_type === 'cardio' ? log.duration_minutes : null,
                calories: log.workout_type === 'cardio' ? log.calories_burned : null,
                comments: [] // Thêm mảng comments cho mỗi bài tập
            };
            sessions[sessionKey].exercises.push(exercise);
        }
        
        // Gộp comment từ các log vào chung một bài tập
        if (log.comments && log.comments.length > 0) {
            exercise.comments.push(...log.comments);
        }
        
        if (log.workout_type === 'strength' && log.set_number) {
            exercise.sets.push({
                set: log.set_number,
                weight: log.weight_kg,
                reps: log.reps,
            });
        }
    });
    
    return Object.values(sessions).sort((a, b) => new Date(b.date) - new Date(a.date));
};

    const addStrengthExercise = () => {
        const newExercise = {
            id: Date.now(),
            name: '',
            sets: [{ set: 1, weight: '', reps: '', rest: 60 }]
        };
        setStrengthExercises([...strengthExercises, newExercise]);
    };

    const addSet = (exerciseId) => {
        setStrengthExercises(exercises => 
            exercises.map(ex => {
                if (ex.id === exerciseId) {
                    const newSetNumber = ex.sets.length + 1;
                    return {
                        ...ex,
                        sets: [...ex.sets, { set: newSetNumber, weight: '', reps: '', rest: 60 }]
                    };
                }
                return ex;
            })
        );
    };

    const removeSet = (exerciseId, setIndex) => {
        setStrengthExercises(exercises => 
            exercises.map(ex => {
                if (ex.id === exerciseId) {
                    const newSets = ex.sets.filter((_, index) => index !== setIndex);
                    return {
                        ...ex,
                        sets: newSets.map((set, index) => ({ ...set, set: index + 1 }))
                    };
                }
                return ex;
            })
        );
    };

    const updateSet = (exerciseId, setIndex, field, value) => {
        setStrengthExercises(exercises => 
            exercises.map(ex => {
                if (ex.id === exerciseId) {
                    const newSets = [...ex.sets];
                    newSets[setIndex] = { ...newSets[setIndex], [field]: value };
                    return { ...ex, sets: newSets };
                }
                return ex;
            })
        );
    };

    const addCardioExercise = () => {
        const newCardio = {
            id: Date.now(),
            name: '',
            duration: '',
            calories: '',
            intensity: 'medium'
        };
        setCardioExercises([...cardioExercises, newCardio]);
    };

    const validateWorkoutData = () => {
        if (workoutData.workoutType === 'strength') {
            if (strengthExercises.length === 0) {
                setError('Vui lòng thêm ít nhất một bài tập strength');
                return false;
            }
            
            for (let exercise of strengthExercises) {
                if (!exercise.name.trim()) {
                    setError('Vui lòng nhập tên bài tập');
                    return false;
                }
                
                for (let set of exercise.sets) {
                    if (!set.weight || !set.reps) {
                        setError('Vui lòng nhập đầy đủ weight và reps cho tất cả sets');
                        return false;
                    }
                }
            }
        } else {
            if (cardioExercises.length === 0) {
                setError('Vui lòng thêm ít nhất một bài tập cardio');
                return false;
            }
            
            for (let cardio of cardioExercises) {
                if (!cardio.name.trim() || !cardio.duration) {
                    setError('Vui lòng nhập đầy đủ tên bài tập và thời gian');
                    return false;
                }
            }
        }
        
        return true;
    };

    const saveWorkout = async () => {
        if (!validateWorkoutData()) {
            return;
        }

        try {
            setSaving(true);
            setError('');
            setSuccess('');

            const workoutLogs = [];
            const exercises = workoutData.workoutType === 'strength' ? strengthExercises : cardioExercises;
            
            exercises.forEach((exercise, exerciseIndex) => {
                if (workoutData.workoutType === 'strength') {
                    exercise.sets.forEach((set, setIndex) => {
                        workoutLogs.push({
                            user_id: user.user_id || user.id,
                            session_date: workoutData.date,
                            workout_type: 'strength',
                            exercise_name: exercise.name,
                            exercise_order: exerciseIndex + 1,
                            set_number: set.set,
                            weight_kg: parseFloat(set.weight),
                            reps: parseInt(set.reps),
                            rest_seconds: parseInt(set.rest),
                            notes: workoutData.notes
                        });
                    });
                } else {
                    workoutLogs.push({
                        user_id: user.user_id || user.id,
                        session_date: workoutData.date,
                        workout_type: 'cardio',
                        exercise_name: exercise.name,
                        exercise_order: exerciseIndex + 1,
                        duration_minutes: parseInt(exercise.duration),
                        calories_burned: exercise.calories ? parseFloat(exercise.calories) : null,
                        intensity: exercise.intensity,
                        notes: workoutData.notes
                    });
                }
            });
            
            for (let log of workoutLogs) {
                await Apis.post(`api/workout-logs`, log);
            }

            setSuccess('Lưu buổi tập thành công!');
            
            setWorkoutData({
                date: new Date().toISOString().split('T')[0],
                workoutType: 'strength',
                notes: ''
            });
            setStrengthExercises([]);
            setCardioExercises([]);
            
            await loadWorkoutHistory();
            
        } catch (err) {
            setError('Không thể lưu buổi tập. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    useEffect(() => {
        if (error || success) {
            const timer = setTimeout(() => {
                setError('');
                setSuccess('');
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [error, success]);

    return (
        <Container className="mt-4">
            <Row>
                <Col>
                    <h2>Ghi log bài tập</h2>
                    <p className="text-muted">Ghi lại chi tiết buổi tập của bạn một cách chuyên nghiệp</p>
                </Col>
            </Row>

            {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}
            
            {success && (
                <Alert variant="success" dismissible onClose={() => setSuccess('')}>
                    <i className="fas fa-check-circle me-2"></i>
                    {success}
                </Alert>
            )}

            <Row className="mt-4">
                <Col lg={8}>
                    <Card>
                        <Card.Header>
                            <h5>Thông tin buổi tập</h5>
                        </Card.Header>
                        <Card.Body>
                           <Row>
    <Col md={4}>
        <Form.Group className="mb-3">
            <Form.Label>Tên buổi tập</Form.Label>
            <Form.Control 
                type="text" 
                placeholder="VD: Ngày tập ngực..." 
                value={workoutData.session_name} 
                onChange={(e) => setWorkoutData({...workoutData, session_name: e.target.value})} 
            />
        </Form.Group>
    </Col>
    <Col md={4}>
        <Form.Group className="mb-3">
            <Form.Label>Ngày tập</Form.Label>
            <Form.Control
                type="date"
                value={workoutData.date}
                onChange={(e) => setWorkoutData({...workoutData, date: e.target.value})}
            />
        </Form.Group>
    </Col>
    <Col md={4}>
        <Form.Group className="mb-3">
            <Form.Label>Loại bài tập</Form.Label>
            <Form.Select
                value={workoutData.workoutType}
                onChange={(e) => setWorkoutData({...workoutData, workoutType: e.target.value})}
            >
                <option value="strength">Tập tạ (Strength)</option>
                <option value="cardio">Cardio</option>
            </Form.Select>
        </Form.Group>
    </Col>
</Row>

                            {workoutData.workoutType === 'strength' ? (
                                <div>
                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <h6>Bài tập tạ</h6>
                                        <Button variant="success" size="sm" onClick={addStrengthExercise}>
                                            <i className="fas fa-plus me-1"></i>Thêm bài tập
                                        </Button>
                                    </div>

                                    {strengthExercises.map((exercise, exerciseIndex) => (
                                        <Card key={exercise.id} className="mb-3 border-primary">
                                            <Card.Body>
                                                <Row>
                                                    <Col md={4}>
                                                        <Form.Group className="mb-3">
                                                            <Form.Label>Tên bài tập</Form.Label>
                                                            <Form.Control
                                                                type="text"
                                                                placeholder="VD: Bench Press, Squat..."
                                                                value={exercise.name}
                                                                onChange={(e) => {
                                                                    const newExercises = [...strengthExercises];
                                                                    newExercises[exerciseIndex].name = e.target.value;
                                                                    setStrengthExercises(newExercises);
                                                                }}
                                                            />
                                                        </Form.Group>
                                                    </Col>
                                                    <Col md={8}>
                                                        <div className="d-flex justify-content-between align-items-center mb-2">
                                                            <h6>Sets</h6>
                                                            <Button 
                                                                variant="outline-primary" 
                                                                size="sm" 
                                                                onClick={() => addSet(exercise.id)}
                                                            >
                                                                <i className="fas fa-plus me-1"></i>Thêm set
                                                            </Button>
                                                        </div>
                                                        
                                                        <Table size="sm">
                                                            <thead>
                                                                <tr>
                                                                    <th>Set</th>
                                                                    <th>Weight (kg)</th>
                                                                    <th>Reps</th>
                                                                    <th>Rest (s)</th>
                                                                    <th>Action</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                {exercise.sets.map((set, setIndex) => (
                                                                    <tr key={setIndex}>
                                                                        <td>{set.set}</td>
                                                                        <td>
                                                                            <Form.Control
                                                                                size="sm"
                                                                                type="number"
                                                                                placeholder="0"
                                                                                value={set.weight}
                                                                                onChange={(e) => updateSet(exercise.id, setIndex, 'weight', e.target.value)}
                                                                            />
                                                                        </td>
                                                                        <td>
                                                                            <Form.Control
                                                                                size="sm"
                                                                                type="number"
                                                                                placeholder="0"
                                                                                value={set.reps}
                                                                                onChange={(e) => updateSet(exercise.id, setIndex, 'reps', e.target.value)}
                                                                            />
                                                                        </td>
                                                                        <td>
                                                                            <Form.Control
                                                                                size="sm"
                                                                                type="number"
                                                                                placeholder="60"
                                                                                value={set.rest}
                                                                                onChange={(e) => updateSet(exercise.id, setIndex, 'rest', e.target.value)}
                                                                            />
                                                                        </td>
                                                                        <td>
                                                                            {exercise.sets.length > 1 && (
                                                                                <Button 
                                                                                    variant="outline-danger" 
                                                                                    size="sm"
                                                                                    onClick={() => removeSet(exercise.id, setIndex)}
                                                                                >
                                                                                    <i className="fas fa-trash"></i>
                                                                                </Button>
                                                                            )}
                                                                        </td>
                                                                    </tr>
                                                                ))}
                                                            </tbody>
                                                        </Table>
                                                    </Col>
                                                </Row>
                                            </Card.Body>
                                        </Card>
                                    ))}
                                </div>
                            ) : (
                                <div>
                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <h6>Bài tập Cardio</h6>
                                        <Button variant="success" size="sm" onClick={addCardioExercise}>
                                            <i className="fas fa-plus me-1"></i>Thêm bài tập
                                        </Button>
                                    </div>

                                    {cardioExercises.map((cardio, index) => (
                                        <Card key={cardio.id} className="mb-3 border-success">
                                            <Card.Body>
                                                <Row>
                                                    <Col md={3}>
                                                        <Form.Group className="mb-3">
                                                            <Form.Label>Tên bài tập</Form.Label>
                                                            <Form.Control
                                                                type="text"
                                                                placeholder="VD: Đi bộ dốc..."
                                                                value={cardio.name}
                                                                onChange={(e) => {
                                                                    const newCardio = [...cardioExercises];
                                                                    newCardio[index].name = e.target.value;
                                                                    setCardioExercises(newCardio);
                                                                }}
                                                            />
                                                        </Form.Group>
                                                    </Col>
                                                    <Col md={3}>
                                                        <Form.Group className="mb-3">
                                                            <Form.Label>Thời gian (phút)</Form.Label>
                                                            <Form.Control
                                                                type="number"
                                                                placeholder="30"
                                                                value={cardio.duration}
                                                                onChange={(e) => {
                                                                    const newCardio = [...cardioExercises];
                                                                    newCardio[index].duration = e.target.value;
                                                                    setCardioExercises(newCardio);
                                                                }}
                                                            />
                                                        </Form.Group>
                                                    </Col>
                                                    <Col md={3}>
                                                        <Form.Group className="mb-3">
                                                            <Form.Label>Calories</Form.Label>
                                                            <Form.Control
                                                                type="number"
                                                                placeholder="200"
                                                                value={cardio.calories}
                                                                onChange={(e) => {
                                                                    const newCardio = [...cardioExercises];
                                                                    newCardio[index].calories = e.target.value;
                                                                    setCardioExercises(newCardio);
                                                                }}
                                                            />
                                                        </Form.Group>
                                                    </Col>
                                                    <Col md={3}>
                                                        <Form.Group className="mb-3">
                                                            <Form.Label>Cường độ</Form.Label>
                                                            <Form.Select
                                                                value={cardio.intensity}
                                                                onChange={(e) => {
                                                                    const newCardio = [...cardioExercises];
                                                                    newCardio[index].intensity = e.target.value;
                                                                    setCardioExercises(newCardio);
                                                                }}
                                                            >
                                                                <option value="low">Thấp</option>
                                                                <option value="medium">Trung bình</option>
                                                                <option value="high">Cao</option>
                                                            </Form.Select>
                                                        </Form.Group>
                                                    </Col>
                                                </Row>
                                            </Card.Body>
                                        </Card>
                                    ))}
                                </div>
                            )}

                            <Form.Group className="mb-3">
                                <Form.Label>Ghi chú</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    placeholder="Ghi chú về buổi tập, cảm giác, mục tiêu..."
                                    value={workoutData.notes}
                                    onChange={(e) => setWorkoutData({...workoutData, notes: e.target.value})}
                                />
                            </Form.Group>

                            <Button 
                                variant="primary" 
                                size="lg" 
                                onClick={saveWorkout}
                                disabled={
                                    saving ||
                                    (workoutData.workoutType === 'strength' 
                                        ? strengthExercises.length === 0 
                                        : cardioExercises.length === 0)
                                }
                            >
                                {saving ? (
                                    <>
                                        <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                            className="me-2"
                                        />
                                        Đang lưu...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        Lưu buổi tập
                                    </>
                                )}
                            </Button>
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={4}>
                    <Card>
                        <Card.Header>
                            <div className="d-flex justify-content-between align-items-center">
                                <h5>Lịch sử bài tập</h5>
                                <Button 
                                    variant="outline-secondary" 
                                    size="sm" 
                                    onClick={loadWorkoutHistory}
                                    disabled={loading}
                                >
                                    <i className="fas fa-sync-alt me-1"></i>
                                    {loading ? 'Đang tải...' : 'Làm mới'}
                                </Button>
                            </div>
                        </Card.Header>
                        <Card.Body>
                          <Card.Body style={{maxHeight: '80vh', overflowY: 'auto'}}>
    {loading ? (
        <div className="text-center py-5"><Spinner animation="border" /><p className="mt-2">Đang tải...</p></div>
    ) : workoutHistory.length === 0 ? (
        <div className="text-center text-muted py-5"><i className="fas fa-history fa-2x mb-2"></i><p>Chưa có lịch sử bài tập nào</p></div>
    ) : (
        <div>
            {workoutHistory.map(session => (
                <Card key={session.id} className="mb-3 workout-session-card">
                    <Card.Header className="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 className="mb-0">{session.name}</h6>
                            <small className="text-muted">{new Date(session.date).toLocaleDateString('vi-VN')}</small>
                        </div>
                        {session.exercises.length > 0 && 
                            <Badge bg={session.exercises[0].type === 'strength' ? 'primary' : 'success'}>
                                {session.exercises[0].type === 'strength' ? 'Tạ' : 'Cardio'}
                            </Badge>
                        }
                    </Card.Header>
                    <Card.Body>
                        {session.exercises.map((exercise, index) => (
                            <div key={index} className="exercise-details mb-2">
                                <strong><i className={`fas ${exercise.type === 'strength' ? 'fa-dumbbell' : 'fa-running'} me-2`}></i>{exercise.name}</strong>
                                {exercise.type === 'strength' ? (
                                    <Table striped bordered size="sm" className="mt-1 mb-0">
                                        <thead><tr><th>Set</th><th>Kg</th><th>Reps</th></tr></thead>
                                        <tbody>
                                            {exercise.sets.map((set, setIndex) => (
                                                <tr key={setIndex}><td>{set.set}</td><td>{set.weight}</td><td>{set.reps}</td></tr>
                                            ))}
                                        </tbody>
                                    </Table>
                                ) : (
                                    <div className="cardio-info mt-1 text-muted">
                                        <span><i className="fas fa-clock me-1"></i> {exercise.duration || 0} phút</span>
                                        <span className="ms-3"><i className="fas fa-fire me-1"></i> {exercise.calories || 0} cal</span>
                                    </div>
                                )}
                                {exercise.comments && exercise.comments.length > 0 && (
    <div className="pt-comments-section mt-2">
        {exercise.comments.map((comment, cIndex) => (
            <div key={cIndex} className="pt-comment">
                <p className="comment-text mb-0">
                    <i className="fas fa-comment-dots text-primary me-2"></i>
                    {comment.comment}
                </p>
                <small className="comment-meta text-muted">
                    - PT {comment.pt_name} lúc {comment.created_at}
                </small>
            </div>
        ))}
    </div>
)}
                            </div>
                        ))}
                        {session.notes && (
                            <div className="session-notes border-top pt-2 mt-2">
                                <p className="mb-0"><strong><i className="fas fa-sticky-note me-2"></i>Ghi chú:</strong> <span className="text-muted fst-italic">{session.notes}</span></p>
                            </div>
                        )}
                    </Card.Body>
                </Card>
            ))}
        </div>
    )}
</Card.Body>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default WorkoutLog;
