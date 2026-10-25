import React from 'react';
import { Container, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const HomePage = () => {
    return (
        <Container className="text-center" style={{ marginTop: '100px' }}>
            <h1 className="mb-3">물품 주문 관리 시스템</h1>
            <p className="text-muted mb-4">필요한 물품을 둘러보고 장바구니에 담아 주문해보세요.</p>
            <Link to="/products">
                <Button variant="primary" size="lg">
                    물품 둘러보기
                </Button>
            </Link>
        </Container>
    );
};

export default HomePage;