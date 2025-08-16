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

    const formatWorkoutHistoryFromDB = (dbData) => {
        const sessions = {};
        
        dbData.forEach(log => {
            const sessionKey = `${log.session_date}_${log.workout_type}`;
            
            if (!sessions[sessionKey]) {
                sessions[sessionKey] = {
                    id: log.log_id,
                    date: log.session_date,
                    type: log.workout_type,
                    exercises: [],
                    notes: log.notes
                };
            }
            
            if (log.workout_type === 'strength') {
                let exercise = sessions[sessionKey].exercises.find(ex => 
                    ex.name === log.exercise_name && ex.exercise_order === log.exercise_order
                );
                
                if (!exercise) {
                    exercise = {
                        name: log.exercise_name,
                        sets: []
                    };
                    sessions[sessionKey].exercises.push(exercise);
                }
                
                if (log.set_number) {
                    exercise.sets.push({
                        set: log.set_number,
                        weight: log.weight_kg,
                        reps: log.reps,
                        rest: log.rest_seconds
                    });
                }
            } else {
                let exercise = sessions[sessionKey].exercises.find(ex => 
                    ex.name === log.exercise_name && ex.exercise_order === log.exercise_order
                );
                
                if (!exercise) {
                    exercise = {
                        name: log.exercise_name,
                        duration: log.duration_minutes,
                        calories: log.calories_burned,
                        intensity: log.intensity
                    };
                    sessions[sessionKey].exercises.push(exercise);
                }
            }
        });
        
        return Object.values(sessions);
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
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Ngày tập</Form.Label>
                                        <Form.Control
                                            type="date"
                                            value={workoutData.date}
                                            onChange={(e) => setWorkoutData({...workoutData, date: e.target.value})}
                                        />
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
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
                                                                placeholder="VD: Đi bộ dốc, Chạy..."
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
                            {loading ? (
                                <div className="text-center py-4">
                                    <Spinner animation="border" role="status">
                                        <span className="visually-hidden">Đang tải...</span>
                                    </Spinner>
                                    <p className="mt-2 text-muted">Đang tải lịch sử bài tập...</p>
                                </div>
                            ) : workoutHistory.length === 0 ? (
                                <div className="text-center py-4 text-muted">
                                    <i className="fas fa-history fa-2x mb-2"></i>
                                    <p>Chưa có lịch sử bài tập nào</p>
                                </div>
                            ) : (
                                workoutHistory.map(workout => (
                                    <div key={workout.id} className="mb-3 p-3 border rounded">
                                        <div className="d-flex justify-content-between align-items-center mb-2">
                                            <h6 className="mb-0">{workout.date}</h6>
                                            <Badge bg={workout.type === 'strength' ? 'primary' : 'success'}>
                                                {workout.type === 'strength' ? 'Tạ' : 'Cardio'}
                                            </Badge>
                                        </div>
                                        
                                        {workout.type === 'strength' ? (
                                            <div>
                                                {workout.exercises.map((exercise, index) => (
                                                    <div key={index} className="mb-2">
                                                        <strong>{exercise.name}</strong>
                                                        <div className="text-muted small">
                                                            {exercise.sets.length} sets
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <div>
                                                {workout.exercises.map((cardio, index) => (
                                                    <div key={index} className="mb-2">
                                                        <strong>{cardio.name}</strong>
                                                        <div className="text-muted small">
                                                            {cardio.duration} phút
                                                            {cardio.calories && ` • ${cardio.calories} cal`}
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                        
                                        {workout.notes && (
                                            <div className="text-muted small mt-2">
                                                <i className="fas fa-comment me-1"></i>
                                                {workout.notes}
                                            </div>
                                        )}
                                    </div>
                                ))
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default WorkoutLog;
