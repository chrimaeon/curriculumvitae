// noinspection JSUnusedGlobalSymbols

/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
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
        const body = http.get<'text'>(`${BASE_URL}profile`).json() as JSONObject

        check(body, propertiesChecker(['name', 'phone', 'profileImagePath', 'address', 'email', 'intro']))
    })

    group('employment', () => {
        const body = http.get(`${BASE_URL}employment`).json() as JSONArray
        check(body, { 'has employments': (array) => array.length > 0 })

        const firstEntry = body[0] as JSONObject
        check(
            firstEntry,
            propertiesChecker(['id', 'jobTitle', 'employer', 'startDate', 'endDate', 'city', 'description']),
        )
    })

    group('skills', () => {
        const body = http.get(`${BASE_URL}skills`).json() as JSONArray
        check(body, { 'has skills': (array) => array.length > 0 })

        const firstEntry = body[0] as JSONObject
        check(firstEntry, propertiesChecker(['level', 'name']))
    })

    group('oss-projects', () => {
        const body = http.get(`${BASE_URL}oss-projects`).json() as JSONArray
        check(body, { 'has projects': (array) => array.length > 0 })

        const firstEntry = body[0] as JSONObject
        check(
            firstEntry,
            propertiesChecker(['name', 'description', 'url', 'topics', 'stars', 'private', 'fork', 'archived']),
        )
    })
}
