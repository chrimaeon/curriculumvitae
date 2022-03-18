/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#ifndef CV_NAME_H
#define CV_NAME_H

#include <string>

class Name {
private:
    std::string name_;
public:
    Name(const u_char *name, size_t len);

    ~Name() = default;

    void setName(const u_char *name, size_t len);

    const char *getName();
};

#endif //CV_NAME_H
