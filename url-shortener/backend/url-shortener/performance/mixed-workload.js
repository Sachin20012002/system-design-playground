import http from 'k6/http';
import { check } from 'k6';
import { SharedArray } from 'k6/data';

const BASE_URL = 'http://host.docker.internal';

const shortCodes = new SharedArray('shortCodes', function () {
    return JSON.parse(open('./generated-shortcodes.json'));
});

export const options = {
    vus: 50,
    duration: '30s',
};

export default function () {

    const random = Math.random();

    // 95% Redirect
    if (random < 0.95) {

        const code =
            shortCodes[Math.floor(Math.random() * shortCodes.length)];

        const response = http.get(
            `${BASE_URL}/${code}`,
            {
                redirects: 0
            }
        );

        check(response, {
            'redirect': (r) => r.status === 302,
        });

    }

    // 5% Create
    else {

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

}