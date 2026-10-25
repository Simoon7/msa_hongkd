import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Container, Row, Spinner, Alert } from 'react-bootstrap';
import ProductCard from '../components/ProductCard';

const BASE_URL = process.env.REACT_APP_API_BASE_URL;

const ProductListPage = () => {
    const [searchParams] = useSearchParams();
    const keyword = searchParams.get('keyword') || '';

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            setError('');
            try {
                const url = keyword
                    ? `${BASE_URL}/product-service/product/list?keyword=${encodeURIComponent(keyword)}`
                    : `${BASE_URL}/product-service/product/list`;

                const response = await fetch(url);

                if (!response.ok) {
                    setError('물품 목록을 불러오지 못했습니다.');
                    return;
                }

                const data = await response.json();
                setProducts(data);
            } catch (err) {
                setError('서버와 통신 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [keyword]);

    return (
        <Container className="mt-4">
            <h2 className="mb-4">{keyword ? `"${keyword}" 검색 결과` : '물품목록'}</h2>

            {loading && <Spinner animation="border" />}
            {error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && products.length === 0 && (
                <Alert variant="info">등록된 물품이 없습니다.</Alert>
            )}

            <Row>
                {products.map((product) => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </Row>
        </Container>
    );
};

export default ProductListPage;
