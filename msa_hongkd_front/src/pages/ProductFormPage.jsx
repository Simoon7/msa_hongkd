import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { fetchWithAccess, getRole } from '../util/fetchUtil';

const BASE_URL = process.env.REACT_APP_API_BASE_URL;

const ProductFormPage = () => {
    const { productId } = useParams(); // 있으면 수정모드, 없으면 등록모드
    const isEdit = !!productId;
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: '',
        category: '',
        price: '',
        stockQuantity: '',
        description: '',
    });
    const [loading, setLoading] = useState(isEdit);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (getRole() !== 'ADMIN') {
            alert('관리자만 접근할 수 있습니다.');
            navigate('/products');
            return;
        }

        if (isEdit) {
            const fetchProduct = async () => {
                try {
                    const response = await fetch(`${BASE_URL}/product-service/product/${productId}`);
                    if (!response.ok) {
                        setError('상품 정보를 불러오지 못했습니다.');
                        return;
                    }
                    const data = await response.json();
                    setForm({
                        name: data.name,
                        category: data.category,
                        price: data.price,
                        stockQuantity: data.stockQuantity,
                        description: data.description || '',
                    });
                } catch (err) {
                    setError('서버와 통신 중 오류가 발생했습니다.');
                } finally {
                    setLoading(false);
                }
            };
            fetchProduct();
        }
    }, [productId]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError('');

        const body = {
            name: form.name,
            category: form.category,
            price: Number(form.price),
            stockQuantity: Number(form.stockQuantity),
            description: form.description,
        };

        try {
            const url = isEdit ? `/product-service/product/${productId}` : `/product-service/product/create`;
            const method = isEdit ? 'PUT' : 'POST';

            const response = await fetchWithAccess(url, {
                method,
                body: JSON.stringify(body),
            });

            if (!response.ok) {
                const data = await response.json();
                setError(data.message || '저장에 실패했습니다.');
                return;
            }

            if (isEdit) {
                navigate(`/products/${productId}`);
            } else {
                const newId = await response.json();
                navigate(`/products/${newId}`);
            }
        } catch (err) {
            setError('서버와 통신 중 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Container className="mt-4"><Spinner animation="border" /></Container>;

    return (
        <Container style={{ maxWidth: '500px' }} className="mt-4">
            <h2 className="mb-4">{isEdit ? '물품 수정' : '물품 등록'}</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>물품명</Form.Label>
                    <Form.Control
                        type="text"
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>카테고리</Form.Label>
                    <Form.Control
                        type="text"
                        name="category"
                        placeholder="예: 가전, 생활용품, 컴퓨터"
                        value={form.category}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>가격</Form.Label>
                    <Form.Control
                        type="number"
                        name="price"
                        min={0}
                        value={form.price}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>재고 수량</Form.Label>
                    <Form.Control
                        type="number"
                        name="stockQuantity"
                        min={0}
                        value={form.stockQuantity}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>설명</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                    />
                </Form.Group>

                <Button type="submit" variant="primary" className="w-100" disabled={submitting}>
                    {submitting ? '저장 중...' : isEdit ? '수정하기' : '등록하기'}
                </Button>
            </Form>
        </Container>
    );
};

export default ProductFormPage;