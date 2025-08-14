import React, { useState, useEffect } from 'react';
import { Navbar, Container, Nav, NavDropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Header = () => {
    const [categories, setCategories] = useState([]);

    
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
                        <Nav.Link as={Link} to="/login">Đăng nhập</Nav.Link>
                        <Nav.Link as={Link} to="/register">Đăng ký</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;