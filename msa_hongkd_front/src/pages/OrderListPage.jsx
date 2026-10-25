import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Table, Button, Alert, Spinner, Badge } from 'react-bootstrap';
import { fetchWithAccess, isLoggedIn } from '../util/fetchUtil';

const BASE_URL = process.env.REACT_APP_API_BASE_URL;

const statusLabel = {
    ORDERED: { text: '주문완료', variant: 'success' },
    CANCELED: { text: '취소됨', variant: 'secondary' },
    REFUNDED: { text: '환불됨', variant: 'danger' },
};

const OrderListPage = () => {
    const navigate = useNavigate();

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [processingId, setProcessingId] = useState(null);

    useEffect(() => {
        if (!isLoggedIn()) {
            navigate('/login');
            return;
        }
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await fetchWithAccess('/ordering-service/ordering/my');
            if (!response.ok) {
                setError('주문내역을 불러오지 못했습니다.');
                return;
            }
            const rawOrders = await response.json();

            // Ordering이 상품명/가격을 안 갖고 있어서, product-service에서 따로 조회해서 화면용으로 조합
            const enriched = await Promise.all(
                rawOrders.map(async (order) => {
                    try {
                        const res = await fetch(`${BASE_URL}/product-service/product/${order.productId}`);
                        const product = res.ok ? await res.json() : null;
                        return {
                            ...order,
                            productName: product ? product.name : `상품 #${order.productId}`,
                            price: product ? product.price : 0,
                        };
                    } catch {
                        return { ...order, productName: `상품 #${order.productId}`, price: 0 };
                    }
                })
            );

            setOrders(enriched);
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (orderId) => {
        if (!window.confirm('이 주문을 취소하시겠습니까?')) return;

        setProcessingId(orderId);
        setError('');
        try {
            const response = await fetchWithAccess(`/ordering-service/ordering/${orderId}/cancel`, {
                method: 'PUT',
            });
            if (!response.ok) {
                const data = await response.json();
                setError(data.message || '취소에 실패했습니다.');
                return;
            }
            fetchOrders();
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setProcessingId(null);
        }
    };

    const handleRefund = async (orderId) => {
        if (!window.confirm('이 주문을 환불하시겠습니까?')) return;

        setProcessingId(orderId);
        setError('');
        try {
            const response = await fetchWithAccess(`/ordering-service/ordering/${orderId}/refund`, {
                method: 'PUT',
            });
            if (!response.ok) {
                const data = await response.json();
                setError(data.message || '환불에 실패했습니다.');
                return;
            }
            fetchOrders();
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setProcessingId(null);
        }
    };

    if (loading) return <Container className="mt-4"><Spinner animation="border" /></Container>;

    return (
        <Container className="mt-4">
            <h2 className="mb-4">주문내역</h2>

            {error && <Alert variant="danger">{error}</Alert>}
            {orders.length === 0 && <Alert variant="info">주문 내역이 없습니다.</Alert>}

            {orders.length > 0 && (
                <Table bordered>
                    <thead>
                        <tr>
                            <th>상품명</th><th>수량</th><th>합계</th><th>상태</th><th>주문일시</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => {
                            const status = statusLabel[order.orderStatus] || { text: order.orderStatus, variant: 'dark' };
                            const canAct = order.orderStatus === 'ORDERED';
                            const subtotal = order.price * order.quantity;
                            return (
                                <tr key={order.orderId}>
                                    <td>{order.productName}</td>
                                    <td>{order.quantity}개</td>
                                    <td>{subtotal.toLocaleString()}원</td>
                                    <td><Badge bg={status.variant}>{status.text}</Badge></td>
                                    <td><small>{new Date(order.createdTime).toLocaleString()}</small></td>
                                    <td>
                                        {canAct && (
                                            <>
                                                <Button variant="outline-secondary" size="sm" className="me-1"
                                                    disabled={processingId === order.orderId}
                                                    onClick={() => handleCancel(order.orderId)}>취소</Button>
                                                <Button variant="outline-danger" size="sm"
                                                    disabled={processingId === order.orderId}
                                                    onClick={() => handleRefund(order.orderId)}>환불</Button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </Table>
            )}
        </Container>
    );
};

export default OrderListPage;