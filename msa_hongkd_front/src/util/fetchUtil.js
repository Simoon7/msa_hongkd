
const BASE_URL = process.env.REACT_APP_API_BASE_URL;

// 로그인 성공 시 토큰 저장
export function saveTokens(id, accessToken, refreshToken) {
    localStorage.setItem('memberId', id);
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
}

// 로그아웃 시 토큰 제거
export function clearTokens() {
    localStorage.removeItem('memberId');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
}

export function isLoggedIn() {
    return !!localStorage.getItem('accessToken');
}

export function getRole() {
    const token = localStorage.getItem('accessToken');
    if (!token) return null;
    try {
        const payload = token.split('.')[1];
        const decoded = JSON.parse(atob(payload));
        return decoded.role;
    } catch (e) {
        return null;
    }
}
// Refresh Token으로 Access Token 재발급
async function refreshAccessToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return null;

    const response = await fetch(`${BASE_URL}/member-service/member/refresh-token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
        clearTokens();
        return null;
    }

    const newAccessToken = await response.text();
    localStorage.setItem('accessToken', newAccessToken);
    return newAccessToken;
}

// Authorization 헤더를 자동으로 붙이고, 401이면 토큰 재발급 후 한 번 재시도
export async function fetchWithAccess(url, options = {}) {
    const accessToken = localStorage.getItem('accessToken');

    const response = await fetch(`${BASE_URL}${url}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
            ...options.headers,
        },
    });

    if (response.status !== 401) {
        return response;
    }

    // 401이고, 재발급 시도할 Refresh Token도 없으면 그냥 원래 응답 반환
    if (!localStorage.getItem('refreshToken')) {
        return response;
    }

    const newAccessToken = await refreshAccessToken();
    if (!newAccessToken) {
        window.location.href = '/login';
        return response;
    }

    // 재발급 성공 → 원래 요청 한 번 더
    return fetch(`${BASE_URL}${url}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${newAccessToken}`,
            ...options.headers,
        },
    });
}
