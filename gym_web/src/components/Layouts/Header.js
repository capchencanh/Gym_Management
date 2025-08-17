import { useContext, useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext, MyDispatchContext } from "../../configs/Contexts";

const Header = () => {
    const [categories, setCategories] = useState([]);
    const user = useContext(MyUserContext); 
    const dispatch = useContext(MyDispatchContext);
    const navigate = useNavigate();

    const loadCates = async () => {
        try {
            if (endpoints['categories2']) {
                let res = await Apis.get(endpoints['categories2']);
                setCategories(res.data);
            } else {
                setCategories([]);
            }
        } catch (error) {
            setCategories([]);
        }
    };

    const handleLogout = () => {
        dispatch({ type: "logout" });
        navigate(user ? "/" : "/login");
    };

    useEffect(() => {
        loadCates();
    }, []);

    return (
        <Navbar expand="lg" className="bg-body-tertiary shadow-sm" sticky="top">
            <Container>
                <Navbar.Brand as={Link} to={user ? "/home" : "/"}>
                    DT's SocialNetwork
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {user && <Nav.Link as={Link} to="/home">Trang chủ</Nav.Link>}

                        {categories.length > 0 && (
                            <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                                {categories.map((c) => (
                                    <NavDropdown.Item 
                                        as={Link} 
                                        key={c.id} 
                                        to={c.path}
                                    >
                                        {c.name}
                                    </NavDropdown.Item>
                                ))}
                            </NavDropdown>
                        )}
                    </Nav>

                    <Nav>
                        {user ? (
                            <>
                                <Nav.Link as={Link} to="/profile" className="d-flex align-items-center">
                                    {user.avatar && (
                                        <img
                                            src={user.avatar}
                                            alt={user.username || user.name}
                                            style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "8px" }}
                                        />
                                    )}
                                    {user.fullName || user.username || user.name || user.email}
                                </Nav.Link>
                                <Nav.Link as={Link} to="/pt-management" className="d-flex align-items-center">
                                    <i className="fas fa-user-tie me-1"></i>
                                    Personal Trainer
                                </Nav.Link>
                                <Button variant="outline-danger" onClick={handleLogout} size="sm" className="ms-lg-2 align-self-center mt-2 mt-lg-0">
                                    Đăng xuất
                                </Button>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login" className="ms-lg-2">
                                    <Button variant="outline-primary" size="sm">Đăng nhập</Button>
                                </Nav.Link>
                                <Nav.Link as={Link} to="/register">
                                    <Button variant="primary" size="sm">Đăng ký</Button>
                                </Nav.Link>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;