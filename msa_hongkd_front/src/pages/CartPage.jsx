import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Table, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { fetchWithAccess, isLoggedIn } from '../util/fetchUtil';

const CartPage = () => {
    const navigate = useNavigate();

    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showConfirm, setShowConfirm] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        if (!isLoggedIn()) {
            navigate('/login');
            return;
        }
        fetchCart();
    }, []);

    const fetchCart = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await fetchWithAccess('/cart-service/cart');
            if (!response.ok) {
                setError('장바구니를 불러오지 못했습니다.');
                return;
            }
            const data = await response.json();
            setCart(data);
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleQuantityChange = async (cartItemId, quantity) => {
        if (quantity < 1) return;
        try {
            const response = await fetchWithAccess(`/cart-service/cart/items/${cartItemId}`, {
                method: 'PUT',
                body: JSON.stringify({ quantity }),
            });
            if (response.ok) fetchCart();
        } catch (err) {
            setError('수량 변경에 실패했습니다.');
        }
    };

    const handleRemove = async (cartItemId) => {
        try {
            const response = await fetchWithAccess(`/cart-service/cart/items/${cartItemId}`, {
                method: 'DELETE',
            });
            if (response.ok) fetchCart();
        } catch (err) {
            setError('삭제에 실패했습니다.');
        }
    };

    const handleConfirmPurchase = async () => {
        setSubmitting(true);
        setError('');
        try {
            const response = await fetchWithAccess('/ordering-service/ordering/create', {
                method: 'POST',
                body: JSON.stringify({}),
            });

            if (!response.ok) {
                const data = await response.json();
                setError(data.message || '주문 생성에 실패했습니다.');
                setShowConfirm(false);
                return;
            }

            setShowConfirm(false);
            navigate('/orders');
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
            setShowConfirm(false);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Container className="mt-4"><Spinner animation="border" /></Container>;

    return (
        <Container className="mt-4">
            <h2 className="mb-4">장바구니</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {cart && cart.items.length === 0 && (
                <Alert variant="info">장바구니가 비어 있습니다.</Alert>
            )}

            {cart && cart.items.length > 0 && (
                <>
                    <Table bordered>
                        <thead>
                            <tr>
                                <th>상품명</th>
                                <th>단가</th>
                                <th>수량</th>
                                <th>소계</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {cart.items.map((item) => (
                                <tr key={item.cartItemId}>
                                    <td>{item.productName}</td>
                                    <td>{item.price.toLocaleString()}원</td>
                                    <td style={{ width: '120px' }}>
                                        <Form.Control
                                            type="number"
                                            min={1}
                                            value={item.quantity}
                                            onChange={(e) =>
                                                handleQuantityChange(item.cartItemId, Number(e.target.value))
                                            }
                                        />
                                    </td>
                                    <td>{item.subtotal.toLocaleString()}원</td>
                                    <td>
                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            onClick={() => handleRemove(item.cartItemId)}
                                        >
                                            삭제
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    <div className="d-flex justify-content-between align-items-center mt-3">
                        <h4>총 합계: {cart.totalPrice.toLocaleString()}원</h4>
                        <Button variant="primary" size="lg" onClick={() => setShowConfirm(true)}>
                            구매하기
                        </Button>
                    </div>
                </>
            )}

            {/* 구매 확인 팝업 */}
            {showConfirm && cart && (
                <div
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        backgroundColor: 'rgba(0,0,0,0.5)',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        zIndex: 1000,
                    }}
                >
                    <div
                        style={{
                            backgroundColor: 'white',
                            padding: '30px',
                            borderRadius: '8px',
                            width: '400px',
                        }}
                    >
                        <h4>주문 확인</h4>
                        <Table borderless size="sm">
                            <tbody>
                                {cart.items.map((item) => (
                                    <tr key={item.cartItemId}>
                                        <td>{item.productName}</td>
                                        <td>{item.quantity}개</td>
                                        <td className="text-end">{item.subtotal.toLocaleString()}원</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                        <hr />
                        <h5 className="text-end">총 결제금액: {cart.totalPrice.toLocaleString()}원</h5>

                        <div className="d-flex justify-content-end mt-3">
                            <Button
                                variant="secondary"
                                className="me-2"
                                onClick={() => setShowConfirm(false)}
                                disabled={submitting}
                            >
                                취소
                            </Button>
                            <Button variant="primary" onClick={handleConfirmPurchase} disabled={submitting}>
                                {submitting ? '처리 중...' : '결제 확정'}
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </Container>
    );
};

export default CartPage;
