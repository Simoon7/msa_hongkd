import React from 'react';
import { Card, Button, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const ProductCard = ({ product }) => {
    return (
        <Col md={4} className="mb-4">
            <Card>
                {product.imageUrl && (
                    <Card.Img variant="top" src={product.imageUrl} style={{ height: '200px', objectFit: 'cover' }} />
                )}
                <Card.Body>
                    <Card.Title>{product.name}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">{product.category}</Card.Subtitle>
                    <Card.Text>{product.price.toLocaleString()}원</Card.Text>
                    <Card.Text>
                        <small className={product.stockQuantity > 0 ? 'text-success' : 'text-danger'}>
                            {product.stockQuantity > 0 ? `재고 ${product.stockQuantity}개` : '품절'}
                        </small>
                    </Card.Text>
                    <Link to={`/products/${product.id}`}>
                        <Button variant="outline-primary">상세보기</Button>
                    </Link>
                </Card.Body>
            </Card>
        </Col>
    );
};

export default ProductCard;