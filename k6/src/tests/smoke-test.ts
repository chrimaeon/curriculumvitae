import http from 'k6/http'
import { sleep } from 'k6'
import { Options } from 'k6/options'

export const options: Partial<Options> = {
    vus: 1,
    duration: '1m',

    thresholds: {
        http_req_duration: ['p(99)<1000'],
    },
}

const BASE_URL = __ENV.BASE_URL

export default (): void => {
    http.batch([
        ['GET', `${BASE_URL}/profile`],
        ['GET', `${BASE_URL}/employment`],
    ])
    sleep(1)
}
