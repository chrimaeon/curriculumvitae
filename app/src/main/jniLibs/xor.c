/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include "xor.h"

#define KEY "Curriculum Vitae"
#define KEY_LEN 16

void d(const unsigned char *src, const size_t len, char *dst) {
    for (size_t i = 0; i < len; ++i) {
        dst[i] = src[i] ^ KEY[i % KEY_LEN];
    }
    dst[len] = '\0';
}
