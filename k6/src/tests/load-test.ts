import http from 'k6/http'
import { sleep } from 'k6'
import { Options } from 'k6/options'

export const options: Partial<Options> = {
    stages: [
        { duration: '5m', target: 100 }, // simulate ramp-up of traffic from 1 to 100 users over 5 minutes.
        { duration: '10m', target: 100 }, // stay at 100 users for 10 minutes
        { duration: '5m', target: 0 }, // ramp-down to 0 users
    ],

    thresholds: {
        http_req_duration: ['p(99)<1500'],
    },
}

const BASE_URL = __ENV.BASE_URL

// noinspection JSUnusedGlobalSymbols
export default (): void => {
    http.batch([
        { method: 'GET', url: `${BASE_URL}/profile` },
        { method: 'GET', url: `${BASE_URL}/employment` },
    ])
    sleep(1)
}
