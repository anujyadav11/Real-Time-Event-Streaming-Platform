import http from 'k6/http';
import { check } from 'k6';
const BASE_URL =
    __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN =
    __ENV.TOKEN;
export const options = {
    scenarios: {
        order_load: {
            executor: 'ramping-vus',
            startVUs: 1,
            stages: [
                {
                    duration: '10s',
                    target: 10
                },
                {
                    duration: '20s',
                    target: 25
                },
                {
                    duration: '20s',
                    target: 50
                },
                {
                    duration: '20s',
                    target: 100
                },
                {
                    duration: '10s',
                    target: 0
                }
            ],
            gracefulRampDown: '5s'
        }
    },
    thresholds: {
        http_req_failed: [
            'rate<0.01'
        ],
        http_req_duration: [
            'p(95)<1000',
            'p(99)<2000'
        ]
    }
};
export default function () {
    const payload = JSON.stringify({
        productId: 1,
        quantity: 1
    });
    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    };
    if (TOKEN) {
        params.headers.Authorization =
            `Bearer ${TOKEN}`;
    }
    const response = http.post(
        `${BASE_URL}/api/orders`,
        payload,
        params
    );
    check(response, {
        'status is successful':
            (response) =>
                response.status >= 200 &&
                response.status < 300

    });
}