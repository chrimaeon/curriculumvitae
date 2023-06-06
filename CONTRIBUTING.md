# CONTRIBUTING

This project uses [ktlint] to check the code style. Adjust your editor/IDE accordingly.

For business logic add a unit test or integration test.

Dependencies are defined using `VERSION_CATALOGS` in a [TOML file][version-catalog-toml]. For more info
see [Sharing dependency versions between projects][version-catalog]

## Setup

### General

1. Copy [`config.properties.sample`](config.properties.sample) to `config.properties` and adjust to
   your development setup.

### Backend

1. Add your profile `JSON` files to the respective resource folders
   i.e. `backend/src/main/resourses/en/`
   see [`backend/src/main/resources/profile.json.sample`](backend/src/main/resources/profile.json.sample)
   for the structure.

2. Add a profile image to [backend/src/main/resources/assets](backend/src/main/resources/assets) and adjust the
   reference in your profile JSON's file

3. Add your employments to [`backend/src/main/resources`](backend/src/main/resources)
   see [`backend/src/main/resources/employments.json.sample`](backend/src/main/resources/employments.json.sample)

4. Add your skills to [`backend/src/main/resources`](backend/src/main/resources)
   see [`backend/src/main/resources/skills.json.sample`](backend/src/main/resources/skills.json.sample)


[ktlint]: https://github.com/pinterest/ktlint
[version-catalog]: https://docs.gradle.org/current/userguide/platforms.html
[version-catalog-toml]:https://docs.gradle.org/current/userguide/platforms.html#sub::toml-dependencies-format
