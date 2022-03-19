/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include "Name.h"

extern "C" {
#include "xor.h"
}

std::string decodeName(const unsigned char *src, size_t len);

Name::Name(const unsigned char *name, size_t len) {
    name_ = decodeName(name, len);
}

void Name::setName(const unsigned char *name, size_t len) {
    name_ = decodeName(name, len);
}

const char *Name::getName() {
    return name_.c_str();
}

std::string Name::toString() {
    return name_;
}

std::string decodeName(const unsigned char *src, const size_t len) {
    char *name = new char[len+1];

    d(src, len, name);

    auto decoded = std::string(name);

    delete[] name;
    return decoded;
}
