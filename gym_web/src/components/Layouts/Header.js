import React, { useState, useEffect } from 'react';
import { Navbar, Container, Nav, NavDropdown, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useUser, useDispatch } from '../../configs/UserProvider';

const Header = () => {
    const [categories, setCategories] = useState([]);
    const user = useUser();
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setCategories([
            { id: 1, name: 'Trang chủ', path: '/' },
            { id: 2, name: 'Hồ sơ cá nhân', path: '/profile' },
            { id: 3, name: 'Bài tập', path: '/workout' },
            { id: 4, name: 'Dinh dưỡng', path: '/diet' },
            { id: 5, name: 'Lớp tập', path: '/classes' },
            { id: 6, name: 'Chat PT', path: '/pt-chat' }
        ]);
    }, []);

    const handleLogout = () => {
        dispatch({ type: 'logout' });
        navigate('/');
    };


    const getUserDisplayName = () => {
        if (user?.name && user.name.trim()) {
            return user.name;
        }
        if (user?.email) {
            return user.email.split('@')[0]; 
        }
        return 'User';
    };

    return (
        <Navbar expand="lg" className="bg-primary shadow-sm" sticky="top" variant="dark">
            <Container>
                <Navbar.Brand as={Link} to="/">
                    <i className="fas fa-dumbbell me-2"></i>
                    Gym Management
                </Navbar.Brand>
                
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/">Trang chủ</Nav.Link>
                        
                        <NavDropdown title="Danh mục" id="categories-nav-dropdown">
                            {categories.map((category) => (
                                <NavDropdown.Item 
                                    as={Link} 
                                    key={category.id} 
                                    to={category.path}
                                >
                                    {category.name}
                                </NavDropdown.Item>
                            ))}
                        </NavDropdown>
                    </Nav>
                    
                    <Nav>
                        {user ? (
                            <>
                                <NavDropdown 
                                    title={
                                        <span>
                                            <i className="fas fa-user me-1"></i>
                                            {getUserDisplayName()}
                                        </span>
                                    } 
                                    id="user-nav-dropdown"
                                    align="end"
                                >
                                    <NavDropdown.Header>
                                        <small className="text-muted">
                                            <i className="fas fa-envelope me-1"></i>
                                            {user.email}
                                        </small>
                                    </NavDropdown.Header>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item as={Link} to="/profile">
                                        <i className="fas fa-user-cog me-2"></i>
                                        Hồ sơ cá nhân
                                    </NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/workout">
                                        <i className="fas fa-dumbbell me-2"></i>
                                        Ghi log bài tập
                                    </NavDropdown.Item>
                                    {user.fitness_goal && (
                                        <NavDropdown.Item as={Link} to="/goals">
                                            <i className="fas fa-bullseye me-2"></i>
                                            Mục tiêu: {user.fitness_goal}
                                        </NavDropdown.Item>
                                    )}
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item onClick={handleLogout}>
                                        <i className="fas fa-sign-out-alt me-2"></i>
                                        Đăng xuất
                                    </NavDropdown.Item>
                                </NavDropdown>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login">Đăng nhập</Nav.Link>
                                <Nav.Link as={Link} to="/register">Đăng ký</Nav.Link>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;