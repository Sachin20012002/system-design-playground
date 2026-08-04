import http from "k6/http";
import {check, sleep} from "k6";

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
    stages: [
        {duration: "15s", target: 50},
        {duration: "30s", target: 100},
        {duration: "30s", target: 200},
        {duration: "15s", target: 0},
    ],

    thresholds: {
        http_req_duration: ["p(95)<200"],
        checks: ["rate>0.99"],
    },
};

export default function () {
    const response = http.get(`${baseUrl}/api/v1/test`);

    check(response, {
        "status is 200 or 429": (r) =>
            r.status === 200 || r.status === 429,
    });

    sleep(1);
}
