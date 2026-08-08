import http from 'k6/http';
import { check } from 'k6';

const BASE_URL =
    __ENV.BASE_URL || 'http://localhost:<gateway-port>';
/*
 * Deliberately nonexistent product.
 */
const PRODUCT_ID =
    __ENV.PRODUCT_ID || '999999';

const PRICING_URL =
    `${BASE_URL}/api/pricing/products/${PRODUCT_ID}`;

export const options = {
    vus: 50,
    duration: '20s',
    thresholds: {
        /*
         * A 404 from the application is expected,
         * so it should NOT be treated as a k6
         * request failure.
         */
        http_req_failed: [
            'rate<0.01'
        ],
        http_req_duration: [
            'p(95)<500'
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
                    endpoint: 'get-product-price-negative'
                }
            }
        );
    check(response, {
        /*
         * Nonexistent product should return 404.
         */
        'product returns 404':
            (response) =>
                response.status === 404

    });
}