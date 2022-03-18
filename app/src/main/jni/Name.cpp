/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <malloc.h>
#include "Name.h"

extern "C" {
#include "xor.h"
}

std::string decodeName(const u_char *src, size_t len);

Name::Name(const u_char *name, size_t len) {
    name_ = decodeName(name, len);
}

void Name::setName(const u_char *name, size_t len) {
    name_ = decodeName(name, len);
}

const char *Name::getName() {
    return name_.c_str();
}

std::string decodeName(const u_char *src, const size_t len) {
    char *name;

    if (!(name = (char *) (malloc(sizeof(char) * len + 1)))) {
        return nullptr;
    }

    d(src, len, name);
    return std::string(name);
}
