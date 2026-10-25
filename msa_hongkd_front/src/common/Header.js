import React from 'react';
import { Button, Container, Form, Nav, Navbar } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { isLoggedIn, getRole, clearTokens } from '../util/fetchUtil';

const Header = () => {
    const navigate = useNavigate();
    const [keyword, setKeyword] = React.useState('');

    const loggedIn = isLoggedIn();
    const role = getRole();

    const handleSearch = (e) => {
        e.preventDefault();
        navigate(`/products?keyword=${encodeURIComponent(keyword)}`);
    };

    const handleLogout = () => {
        clearTokens();
        window.location.href = '/';
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container fluid>
                <Navbar.Brand as={Link} to="/">MSA주문시스템</Navbar.Brand>
                <Navbar.Toggle aria-controls="navbarScroll" />
                <Navbar.Collapse id="navbarScroll">
                    <Nav className="me-auto my-2 my-lg-0" style={{ maxHeight: '100px' }} navbarScroll>
                        <Nav.Link as={Link} to="/">Home</Nav.Link>
                        <Nav.Link as={Link} to="/products">물품목록</Nav.Link>

                        {role === 'ADMIN' && (
                            <Nav.Link as={Link} to="/products/new">물품등록</Nav.Link>
                        )}

                        {!loggedIn && (
                            <>
                                <Nav.Link as={Link} to="/join">회원가입</Nav.Link>
                                <Nav.Link as={Link} to="/login">로그인</Nav.Link>
                            </>
                        )}

                        {loggedIn && (
                            <>
                                <Nav.Link as={Link} to="/orders">주문내역</Nav.Link>
                                <Nav.Link onClick={handleLogout} style={{ cursor: 'pointer' }}>로그아웃</Nav.Link>
                            </>
                        )}
                    </Nav>

                    <Form className="d-flex" onSubmit={handleSearch}>
                        <Form.Control
                            type="search"
                            placeholder="물품 검색"
                            className="me-2"
                            aria-label="Search"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                        />
                        <Button type="submit" variant="outline-success">Search</Button>
                    </Form>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;