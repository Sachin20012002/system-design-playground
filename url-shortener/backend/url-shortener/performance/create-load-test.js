import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://host.docker.internal';

export const options = {
    vus: 20,
    duration: '30s',
};

export default function () {

    const payload = JSON.stringify({
        longUrl: `https://example.com/${Math.random()}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const response = http.post(
        `${BASE_URL}/api/v1/urls`,
        payload,
        params
    );

    check(response, {
        'created': (r) => r.status === 200,
    });

}