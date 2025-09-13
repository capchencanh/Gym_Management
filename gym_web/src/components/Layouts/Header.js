import { useContext, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext, MyDispatchContext } from "../../configs/Contexts";
import { useAuth } from "../../hooks/useAuth";
 
const Header = () => {
    const [categories, setCategories] = useState([]);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const user = useContext(MyUserContext); 
    const dispatch = useContext(MyDispatchContext);
    const navigate = useNavigate();
    const { logout } = useAuth();

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

    const handleLogout = async () => {
        try {
            await logout();
        } catch (error) {
            dispatch({ type: "logout" });
            localStorage.clear();
            sessionStorage.clear();
            navigate('/login');
        }
    };

    const toggleDropdown = () => {
        setIsDropdownOpen(!isDropdownOpen);
    };

    const closeDropdown = () => {
        setIsDropdownOpen(false);
    };


    useEffect(() => {
        loadCates();
    }, []);

    return (
        <header className="App-header">
            <div className="header-container">
                <Link to={user ? "/home" : "/"} className="logo">
                    <i className="fas fa-dumbbell"></i>
                    Gym Management
                </Link>
                
                <nav className="nav-menu">
                    {user && (
                        <ul className="nav-menu">
                            <li className="nav-item">
                                <Link to="/home" className="nav-link">
                                    <i className="fas fa-home me-1"></i>
                                    Trang chủ
                                </Link>
                            </li>
                            
                            {categories.length > 0 && (
                                <li className="nav-item dropdown">
                                    <span 
                                        className="nav-link dropdown-toggle" 
                                        onClick={toggleDropdown}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <i className="fas fa-th-large me-1"></i>
                                        Danh mục
                                        <i className={`fas fa-chevron-down ms-1 ${isDropdownOpen ? 'rotate-180' : ''}`}></i>
                                    </span>
                                    {isDropdownOpen && (
                                        <div className="dropdown-menu show">
                                            {categories.map((c) => (
                                                <Link 
                                                    key={c.id} 
                                                    to={c.path}
                                                    className="dropdown-item"
                                                    onClick={closeDropdown}
                                                >
                                                    {c.name}
                                                </Link>
                                            ))}
                                        </div>
                                    )}
                                </li>
                            )}
                        </ul>
                    )}
                </nav>

                <div className="user-menu">
                    {user ? (
                        <>
                            <div className="user-info">
                                {user.avatar_url ? (
                                    <img
                                        src={user.avatar_url}
                                        alt={user.name}
                                        className="user-avatar"
                                    />
                                ) : (
                                    <i className="fas fa-user"></i>
                                )}
                                <span className="user-name">{user.name || user.email}</span>
                            </div>
                            
                            <div className="user-actions">
                                <Link to="/profile" className="btn btn-secondary btn-sm">
                                    <i className="fas fa-user me-1"></i>
                                    Profile
                                </Link>
                                <Link to="/pt-management" className="btn btn-secondary btn-sm">
                                    <i className="fas fa-user-tie me-1"></i>
                                    PT
                                </Link>
                                <Link to="/package" className="btn btn-secondary btn-sm">
                                    <i className="fas fa-dumbbell me-1"></i>
                                    Gói Tập
                                </Link>
                                <button onClick={handleLogout} className="btn btn-secondary btn-sm">
                                    <i className="fas fa-sign-out-alt me-1"></i>
                                    Đăng xuất
                                </button>
                            </div>
                        </>
                    ) : (
                        <div className="auth-buttons">
                            <Link to="/login" className="btn btn-secondary btn-sm">
                                <i className="fas fa-sign-in-alt me-1"></i>
                                Đăng nhập
                            </Link>
                            <Link to="/register" className="btn btn-primary btn-sm">
                                <i className="fas fa-user-plus me-1"></i>
                                Đăng ký
                            </Link>
                        </div>
                    )}
                </div>

            </div>
        </header>
    );
};

export default Header;