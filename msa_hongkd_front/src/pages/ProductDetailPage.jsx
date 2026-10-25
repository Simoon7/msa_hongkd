import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Form, Alert, Spinner, Badge } from 'react-bootstrap';
import { fetchWithAccess, isLoggedIn, getRole } from '../util/fetchUtil';

const BASE_URL = process.env.REACT_APP_API_BASE_URL;

const ProductDetailPage = () => {
    const { productId } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            setLoading(true);
            setError('');
            try {
                const response = await fetch(`${BASE_URL}/product-service/product/${productId}`);
                if (!response.ok) {
                    setError('물품 정보를 불러오지 못했습니다.');
                    return;
                }
                setProduct(await response.json());
            } catch (err) {
                setError('서버와 통신 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchProduct();
    }, [productId]);

    const handlePurchase = async () => {
        setError('');
        if (!isLoggedIn()) {
            navigate('/login');
            return;
        }

        const totalPrice = product.price * quantity;
        if (!window.confirm(`${product.name} ${quantity}개, 총 ${totalPrice.toLocaleString()}원에 구매하시겠습니까?`)) {
            return;
        }

        setSubmitting(true);
        try {
            const response = await fetchWithAccess('/ordering-service/ordering/create', {
                method: 'POST',
                body: JSON.stringify({ productId: Number(productId), quantity }),
            });

            if (!response.ok) {
                const data = await response.json();
                setError(data.message || '주문에 실패했습니다.');
                return;
            }

            navigate('/orders');
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Container className="mt-4"><Spinner animation="border" /></Container>;
    if (error && !product) return <Container className="mt-4"><Alert variant="danger">{error}</Alert></Container>;
    if (!product) return null;

    const soldOut = product.stockQuantity <= 0;
    const isAdmin = getRole() === 'ADMIN';

    return (
        <Container className="mt-4">
            <Row>
                <Col md={7}>
                    <h2>{product.name}</h2>
                    <Badge bg="secondary" className="mb-2">{product.category}</Badge>
                    <h4>{product.price.toLocaleString()}원</h4>
                    <p>{product.description}</p>
                    <p className={soldOut ? 'text-danger' : 'text-success'}>
                        {soldOut ? '품절' : `재고 ${product.stockQuantity}개`}
                    </p>

                    {error && <Alert variant="danger">{error}</Alert>}

                    {!soldOut && (
                        <Row className="align-items-center mb-3" style={{ maxWidth: '300px' }}>
                            <Col xs={4}>
                                <Form.Control
                                    type="number"
                                    min={1}
                                    max={product.stockQuantity}
                                    value={quantity}
                                    onChange={(e) => setQuantity(Number(e.target.value))}
                                />
                            </Col>
                            <Col xs={8}>
                                <Button variant="primary" onClick={handlePurchase} disabled={submitting} className="w-100">
                                    {submitting ? '처리 중...' : '구매하기'}
                                </Button>
                            </Col>
                        </Row>
                    )}

                    {isAdmin && (
                        <Button variant="outline-secondary" onClick={() => navigate(`/products/${productId}/edit`)}>
                            수정하기
                        </Button>
                    )}
                </Col>
            </Row>
        </Container>
    );
};

export default ProductDetailPage;