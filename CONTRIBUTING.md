# CONTRIBUTING

This project uses [ktlint] to check the code style. Adjust your editor/IDE accordingly.

For business logic add a unit test or integration test.

Dependencies are defined using the incubating feature `VERSION_CATALOGS`. For more info
see [Sharing dependency versions between projects
][version catalog]

## Setup

### General

1. Copy [`config.properties.sample`](config.properties.sample) to `config.properties` and adjust to
   your development setup.

### Backend

1. Add your profile `JSON` files to the respective resource folders
   i.e. `backend/src/main/resourses/en/`
   see [`backend/src/main/resources/profile.json.sample`](backend/src/main/resources/profile.json.sample)
   for the structure.
2. Add your employments to [`backend/src/main/resources/employments`](backend/src/main/resources/employments)
   see [`backend/src/main/resources/employment.json.sample`](backend/src/main/resources/employment.json.sample)
3. Add a profile image to [backend/src/main/resources/assets](backend/src/main/resources/assets) and adjust the
   reference in your profile JSON's file

[ktlint]: https://github.com/pinterest/ktlint

[version catalog]: https://docs.gradle.org/current/userguide/platforms.html
