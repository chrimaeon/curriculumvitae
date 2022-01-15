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
import { check, Checkers, group, JSONArray, JSONObject } from 'k6'

const propertiesChecker = (props: string[]): Checkers<JSONObject> => {
    const checkers: Checkers<JSONObject> = {}
    props.forEach((prop) => (checkers[`has ${prop}`] = (json: JSONObject) => json[prop] !== undefined))

    return checkers
}

export default (): void => {
    group('profile', () => {
        const body: JSONObject = http.get<'text'>(`${BASE_URL}profile`).json()

        check(body, propertiesChecker(['name', 'phone', 'profileImagePath', 'address', 'email', 'intro']))
    })

    group('employment', () => {
        const body = http.get(`${BASE_URL}employment`).json()
        check<JSONArray>(body, { 'has employments': (array) => array.length > 0 })
        check<JSONObject>(
            body[0],
            propertiesChecker(['id', 'jobTitle', 'employer', 'startDate', 'endDate', 'city', 'description']),
        )
    })

    group('skills', () => {
        const body = http.get(`${BASE_URL}skills`).json()
        check<JSONArray>(body, { 'has skills': (array) => array.length > 0 })
        check<JSONObject>(body[0], propertiesChecker(['level', 'name']))
    })
}
