import http from 'k6/http';
import { sleep, check } from 'k6';

// API_URL can be overridden with: k6 run -e API_URL=http://host.docker.internal:8080
const API_URL = __ENV.API_URL || 'http://localhost:8080';

// Credentials injected through environment variables
const EMAIL = __ENV.K6_USERNAME;
const PASSWORD = __ENV.K6_PASSWORD;

if (!EMAIL || !PASSWORD) {
    throw new Error('Faltan las credenciales K6_USERNAME y K6_PASSWORD en el entorno.');
}

export let options = {
    vus: 50,
    duration: '30s',
    thresholds: {
        // RNF-01 (ISO 25010): p95 < 200 ms bajo 50 usuarios concurrentes
        'http_req_duration{operacion:escenarios}': ['p(95)<200'],
        'http_req_failed{operacion:escenarios}': ['rate<0.01'],
    },
};

/**
 * The login (BCrypt cost 12) runs ONCE in setup() as an
 * authentication cost, outside the measured flow. The http_req_duration metric measures
 * only the RNF-01 endpoint: GET /api/escenarios.
 */
export function setup() {
    const loginRes = http.post(`${API_URL}/api/auth/login`,
        JSON.stringify({ email: EMAIL, password: PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } });
    if (loginRes.status !== 200) {
        throw new Error(`Fallo el login en setup(): HTTP ${loginRes.status}`);
    }
    return { token: loginRes.json('accessToken'), email: EMAIL };
}

export default function (data) {
    const token = data.token;
    const listRes = http.get(`${API_URL}/api/escenarios`, {
        headers: { Authorization: `Bearer ${token}` },
        tags: { operacion: 'escenarios' },
    });
    check(listRes, {
        'listar status is 200': (r) => r.status === 200,
    });
    sleep(1);
}
