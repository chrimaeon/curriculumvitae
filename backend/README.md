# Curriculum Vitae BFF

## Available API's #####

GET

### /profile ###

| Name            | Type   | In     | Description                                                                              |
| --------------- | ------ | ------ | ---------------------------------------------------------------------------------------- |
| Accept-Language | String | Header | A HTTP language header; see https://datatracker.ietf.org/doc/html/rfc7231\#section-5.3.5 |

###### Response ######

    {
      "name": "My Name",
      "phone": "+1234567890",
      "profileImagePath": "/profile.png",
      "address": {
        "street": "Main Street 1",
        "city": "My Hometown",
        "postalCode": "42"
      },
      "email": "noreply@test.com",
      "intro": [
        "Intro line 1 ...",
        "Intro line 2 ...",
        "etc ... etc ... etc ..."
      ]
    }

GET

### /employment ###

###### Response ######

    {
      "id": 1,
      "jobTitle": "Software Developer",
      "employer": "CMG Mobile Apps",
      "startDate": "2010-06-01",
      "endDate": null,
      "city": "Graz",
      "description": [
        "Founder",
        "Software development"
      ]
    }

GET

### /oss-projects ###

###### Response ######

    [
      {
        "name": "my-project",
        "description": "My Open Source Project",
        "url": "https://cmgapps.com",
        "topics": [
          "kotlin",
          "android",
          "kotlin multiplatform"
        ],
        "stars": 42,
        "private": false,
        "fork": false,
        "archived": false,
        "pushedAt": "1979-09-02T18:00:00Z"
      }
    ]

GET

### /skills ###

###### Response ######

    [
      {
        "name": "Mobile Development",
        "level": 5
      },
      {
        "name": "Android",
        "level": 5
      }
    ]
