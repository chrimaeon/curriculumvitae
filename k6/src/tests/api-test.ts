// noinspection JSUnusedGlobalSymbols

/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import http from 'k6/http'
import { BASE_URL } from './requests'
import { check } from 'k6'
import { expect } from 'chai'

export default (): void => {
    let body = http.get(`${BASE_URL}profile`).json()
    check(body, {
        'has profile info': (json) => {
            expect(json).to.have.all.keys('name', 'phone', 'profileImageUrl', 'address', 'email', 'intro')
            return true
        },
    })

    body = http.get<'text'>(`${BASE_URL}employment`).json()
    check(body, {
        'has employment info': (json) => {
            expect(json).to.have.length.greaterThan(0)
            expect(json[0]).to.have.all.keys(
                'id',
                'jobTitle',
                'employer',
                'startDate',
                'endDate',
                'city',
                'description',
            )
            return true
        },
    })

    body = http.get<'text'>(`${BASE_URL}skills`).json()
    check(body, {
        'has skills info': (json) => {
            expect(json).to.have.length.greaterThan(0)
            expect(json[0]).to.have.all.keys('level', 'name')
            return true
        },
    })
}
