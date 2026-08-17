import http from 'k6/http';
import { check } from 'k6';

/*
 * Pricing Service base URL.
 *
 * You can override this when running k6:
 *
 * BASE_URL=http://localhost:8083 k6 run pricing-cache-test.js
 */
const BASE_URL =
    __ENV.BASE_URL || 'http://localhost:8080';
/*
 * Product to test.
 *
 * Example:
 *
 * PRODUCT_ID=1 k6 run pricing-cache-test.js
 */
const PRODUCT_ID =
    __ENV.PRODUCT_ID || '1';
/*
 * Your actual Spring Boot endpoint:
 *
 * GET /api/pricing/products/{productId}
 */
const PRICING_URL =
    `${BASE_URL}/api/pricing/products/${PRODUCT_ID}`;
export const options = {
    scenarios: {
        pricing_load: {
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
        /*
         * Less than 1% of requests should fail.
         */
        http_req_failed: [
            'rate<0.01'
        ],
        /*
         * 95% of requests should complete
         * within 500 ms.
         */
        http_req_duration: [
            'p(95)<500',
            /*
             * 99% of requests should complete
             * within 1 second.
             */
            'p(99)<1000'
        ]
    }
};
export default function () {
    const response =
        http.get(
            PRICING_URL,
            {
                tags: {
                    service: 'pricing-service',
                    endpoint: 'get-product-price'
                }
            }
        );
    check(response, {
        /*
         * Existing product should return HTTP 200.
         */
        'status is 200':
            (response) =>
                response.status === 200,
        /*
         * Response should contain the requested
         * product ID.
         */
        'response contains productId':
            (response) =>
                response.body.includes(
                    PRODUCT_ID
                )
    });
}