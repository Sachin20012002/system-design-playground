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

    const code =
        shortCodes[Math.floor(Math.random() * shortCodes.length)];

    const response = http.get(
        `${BASE_URL}/${code}`,
        {
            redirects: 0
        }
    );

    check(response, {
        'redirect returned': (r) => r.status === 302,
    });

}